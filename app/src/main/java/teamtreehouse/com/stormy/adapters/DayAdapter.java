package teamtreehouse.com.stormy.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import teamtreehouse.com.stormy.R;
import teamtreehouse.com.stormy.weather.Dia;

/**
 * Created by Juan on 11/03/2015.
 */
public class DayAdapter extends BaseAdapter {
    private Context mContext;
    private Dia[] mDias;

    public DayAdapter( Context context, Dia[] dias){
        mContext = context;
        mDias = dias;
    }
    @Override
    public int getCount() {
        return mDias.length;
    }

    @Override
    public Object getItem(int position) {
        return mDias[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.daily_list_item, null);
            holder = new ViewHolder();
            holder.iconImageView = (ImageView) convertView.findViewById(R.id.iconImageView);
            holder.dayLabel = (TextView) convertView.findViewById(R.id.dayNameLabel);
            holder.temperatureLabel = (TextView) convertView.findViewById(R.id.temperatureLabel);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Dia dia = mDias[position];
        holder.iconImageView.setImageResource(dia.getIconId());
        holder.temperatureLabel.setText(dia.getTemperatureMax()+"");
        holder.dayLabel.setText(dia.getDayOfTheWeek());
        return convertView;
    }

    private static class ViewHolder{
        ImageView iconImageView;
        TextView temperatureLabel;
        TextView dayLabel;
    }
}
