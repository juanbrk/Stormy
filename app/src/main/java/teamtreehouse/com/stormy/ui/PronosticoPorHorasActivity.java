package teamtreehouse.com.stormy.ui;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Arrays;

import butterknife.ButterKnife;
import butterknife.InjectView;
import teamtreehouse.com.stormy.R;
import teamtreehouse.com.stormy.adapters.HourAdapter;
import teamtreehouse.com.stormy.weather.Hora;

public class PronosticoPorHorasActivity extends ActionBarActivity {


    private Hora[] mHoras;
    @InjectView(R.id.recyclerView) RecyclerView mRecyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pronostico_por_horas);
        ButterKnife.inject(this);

        Intent intent = getIntent();
        Parcelable[] parcelables = intent.getParcelableArrayExtra(MainActivity.PRONOSTICO_POR_HORAS);
        mHoras = Arrays.copyOf(parcelables, parcelables.length, Hora[].class);

        HourAdapter adapter = new HourAdapter(mHoras);
        mRecyclerView.setAdapter(adapter);
        RecyclerView.LayoutManager  layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

    }
}
