package teamtreehouse.com.stormy.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import teamtreehouse.com.stormy.R;
import teamtreehouse.com.stormy.weather.Actual;
import teamtreehouse.com.stormy.weather.Dia;
import teamtreehouse.com.stormy.weather.Pronostico;
import teamtreehouse.com.stormy.weather.Hora;


public class MainActivity extends ActionBarActivity {

    public static final String TAG = MainActivity.class.getSimpleName();
    public static final String DAILY_FORECAST = "DAILY_FORECAST";
    public static final String PRONOSTICO_POR_HORAS = "PRONOSTICO_POR_HORAS";

    private Pronostico mPronostico;

    @InjectView(R.id.timeLabel) TextView mTimeLabel;
    @InjectView(R.id.temperatureLabel) TextView mTemperatureLabel;
    @InjectView(R.id.humidityValue) TextView mHumidityValue;
    @InjectView(R.id.precipValue) TextView mPrecipValue;
    @InjectView(R.id.summaryLabel) TextView mSummaryLabel;
    @InjectView(R.id.iconImageView) ImageView mIconImageView;
    @InjectView(R.id.refreshImageView) ImageView mRefreshImageView;
    @InjectView(R.id.progressBar) ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        mProgressBar.setVisibility(View.INVISIBLE);

        final double latitude = -31.4295;
        final double longitude = -64.1853;

        mRefreshImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getForecast(latitude, longitude);
            }
        });

        getForecast(latitude, longitude);

        Log.d(TAG, "Main UI code is running!");
    }

    private void getForecast(double latitude, double longitude) {
        String apiKey = "adb44e9f6fec19f4fbf0c137a087c498";
        String forecastUrl = "https://api.forecast.io/forecast/" + apiKey +
                "/" + latitude + "," + longitude;

        if (isNetworkAvailable()) {
            toggleRefresh();

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(forecastUrl)
                    .build();

            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toggleRefresh();
                        }
                    });
                    alertUserAboutError();
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toggleRefresh();
                        }
                    });

                    try {
                        String jsonData = response.body().string();
                        Log.v(TAG, jsonData);
                        if (response.isSuccessful()) {
                            mPronostico = parseForecastDetails(jsonData);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateDisplay();
                                }
                            });
                        } else {
                            alertUserAboutError();
                        }
                    }
                    catch (IOException e) {
                        Log.e(TAG, "Exception caught: ", e);
                    }
                    catch (JSONException e) {
                        Log.e(TAG, "Exception caught: ", e);
                    }
                }
            });
        }
        else {
            Toast.makeText(this, getString(R.string.network_unavailable_message),
                    Toast.LENGTH_LONG).show();
        }
    }

    private void toggleRefresh() {
        if (mProgressBar.getVisibility() == View.INVISIBLE) {
            mProgressBar.setVisibility(View.VISIBLE);
            mRefreshImageView.setVisibility(View.INVISIBLE);
        }
        else {
            mProgressBar.setVisibility(View.INVISIBLE);
            mRefreshImageView.setVisibility(View.VISIBLE);
        }
    }

    private void updateDisplay() {
        Actual actual = mPronostico.getActual();

        mTemperatureLabel.setText(actual.getTemperature() + "");
        mTimeLabel.setText("At " + actual.getFormattedTime() + " it will be");
        mHumidityValue.setText(actual.getHumidity() + "");
        mPrecipValue.setText(actual.getPrecipChance() + "%");
        mSummaryLabel.setText(actual.getSummary());

        Drawable drawable = getResources().getDrawable(actual.getIconId());
        mIconImageView.setImageDrawable(drawable);
    }

    private Pronostico parseForecastDetails (String jsonData) throws JSONException {
        Pronostico pronostico = new Pronostico();
        pronostico.setActual(getCurrentDetails(jsonData));
        pronostico.setHourlyForecast(getHourlyForecast(jsonData));
        pronostico.setPronosticoDiario(getDailyForecast(jsonData));
        return pronostico;
    }

    private Hora[] getHourlyForecast(String jsonData) throws JSONException {
        JSONObject pronostico = new JSONObject(jsonData);
        String timezone = pronostico.getString("timezone");
        JSONObject porHoras = pronostico.getJSONObject("hourly");
        JSONArray info = porHoras.getJSONArray("data");

        Hora[] horas = new Hora[info.length()];

        for (int i = 0; i< info.length();i++){
            JSONObject jsonHour = info.getJSONObject(i);
            Hora hora = new Hora();
            hora.setSummary(jsonHour.getString("summary"));
            hora.setTemperature(jsonHour.getDouble("temperature"));
            hora.setIcon(jsonHour.getString("icon"));
            hora.setTime(jsonHour.getLong("time"));
            hora.setTimezone(timezone);

            horas[i]= hora;
        }

        return horas;
    }

    private Dia[] getDailyForecast(String jsonData) throws JSONException {
        JSONObject pronostico = new JSONObject(jsonData);
        String timezone = pronostico.getString("timezone");
        JSONObject porDia = pronostico.getJSONObject("daily");
        JSONArray info = porDia.getJSONArray("data");

        Dia[] dias = new Dia[info.length()];

        for (int i = 0; i< info.length();i++){
            JSONObject jsonDay = info.getJSONObject(i);
            Dia dia = new Dia();
            dia.setSummary(jsonDay.getString("summary"));
            dia.setTemperatureMax(jsonDay.getDouble("temperatureMax"));
            dia.setIcon(jsonDay.getString("icon"));
            dia.setTime(jsonDay.getLong("time"));
            dia.setTimeZone(timezone);

            dias[i]= dia;
        }
        return dias;
    }


    private Actual getCurrentDetails(String jsonData) throws JSONException {
        JSONObject forecast = new JSONObject(jsonData);
        String timezone = forecast.getString("timezone");
        Log.i(TAG, "From JSON: " + timezone);

        JSONObject currently = forecast.getJSONObject("currently");

        Actual actual = new Actual();
        actual.setHumidity(currently.getDouble("humidity"));
        actual.setTime(currently.getLong("time"));
        actual.setIcon(currently.getString("icon"));
        actual.setPrecipChance(currently.getDouble("precipProbability"));
        actual.setSummary(currently.getString("summary"));
        actual.setTemperature(currently.getDouble("temperature"));
        actual.setTimeZone(timezone);

        Log.d(TAG, actual.getFormattedTime());

        return actual;
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }

        return isAvailable;
    }

    private void alertUserAboutError() {
        AlertDialogFragment dialog = new AlertDialogFragment();
        dialog.show(getFragmentManager(), "error_dialog");
    }

    @OnClick (R.id.dailyButton)
        public void startDailyACtivity(View view){
            Intent intent = new Intent(this, PronosticoDiarioActivity.class);
            intent.putExtra(DAILY_FORECAST,mPronostico.getPronosticoDiario());
            startActivity(intent);
    }

    @OnClick (R.id.hourlyButton) public void startHourlyActivity (View view){
        Intent intent = new Intent(this, PronosticoPorHorasActivity.class);
        intent.putExtra(PRONOSTICO_POR_HORAS, mPronostico.getPronosticoPorHoras());
        startActivity(intent);
    }
}