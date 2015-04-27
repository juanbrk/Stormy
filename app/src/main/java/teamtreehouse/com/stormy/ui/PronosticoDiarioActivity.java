package teamtreehouse.com.stormy.ui;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;

import butterknife.ButterKnife;
import butterknife.InjectView;
import teamtreehouse.com.stormy.R;
import teamtreehouse.com.stormy.adapters.DayAdapter;
import teamtreehouse.com.stormy.weather.Dia;

public class PronosticoDiarioActivity extends Activity {
    @InjectView(android.R.id.list) ListView mlistView;
    @InjectView(android.R.id.empty) TextView mEmptyTextView;
    private Dia[] mDays;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_forecast);
        ButterKnife.inject(this);

        Intent intent = getIntent();
        Parcelable [] parcelables = intent.getParcelableArrayExtra(MainActivity.DAILY_FORECAST);
        mDays = Arrays.copyOf(parcelables,parcelables.length,Dia[].class);

        DayAdapter adapter = new DayAdapter(this, mDays);
        mlistView.setAdapter(adapter);
        mlistView.setEmptyView(mEmptyTextView);
        mlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String diaDeLaSemana = mDays[position].getDayOfTheWeek();
                String condiciones = mDays[position].getSummary();
                String temperatura = mDays[position].getTemperatureMax() + "";
                String message = String.format("El %s la maxima sera de %s y estara %s", diaDeLaSemana,
                        temperatura,
                        condiciones);
                Toast.makeText(PronosticoDiarioActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }
}
