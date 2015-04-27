package teamtreehouse.com.stormy.weather;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Juan on 09/03/2015.
 */
public class Hora implements Parcelable{
    private long mTime;
    private String mSummary;
    private double mTemperature;
    private String mIcon;
    private String mTimezone;

    public Hora(){}

    public long getTime() {
        return mTime;
    }

    public void setTime(long time) {
        mTime = time;
    }

    public String getSummary() {
        return mSummary;
    }

    public void setSummary(String summary) {
        mSummary = summary;
    }

    public int getTemperature() {
        return (int) Math.round((mTemperature - 32)/1.8);
    }

    public void setTemperature(double temperature) {
        mTemperature = temperature;
    }

    public String getIcon() {
        return mIcon;
    }

    public int getIconId(){
        return Pronostico.getIconId(mIcon);
    }

    public void setIcon(String icon) {
        mIcon = icon;
    }

    public String getTimezone() {
        return mTimezone;
    }

    public void setTimezone(String timezone) {
        mTimezone = timezone;
    }

    public String getHora(){
        SimpleDateFormat formatter = new SimpleDateFormat("h a");
        Date date = new Date(mTime *1000);
        return formatter.format(date);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mTime);
        dest.writeDouble(mTemperature);
        dest.writeString(mSummary);
        dest.writeString(mIcon);
        dest.writeString(mTimezone);
    }

    private Hora(Parcel in){
        mTime = in.readLong();
        mTemperature = in.readDouble();
        mSummary = in.readString();
        mIcon = in.readString();
        mTimezone = in.readString();
    }

    public static final Creator<Hora> CREATOR = new Creator<Hora>() {
        @Override
        public Hora createFromParcel(Parcel source) {
            return new Hora(source);
        }

        @Override
        public Hora[] newArray(int size) {
            return new Hora[size];
        }
    };
}
