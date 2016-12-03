package com.example.anubharora.stormy.models;

/**
 * Created by anubharora on 10/26/16.
 */

public class WeatherDetails {


    private String mIcon;
    private double tempMin;
    private double tempMax;

    public String getIcon() {
        return mIcon;
    }

    public void setIcon(String icon) {
        mIcon = icon;
    }

    public double getTempMin() {
        return tempMin;
    }

    public void setTempMin(double tempMin) {
        this.tempMin = tempMin;
    }

    public double getTempMax() {
        return tempMax;
    }

    public void setTempMax(double tempMax) {
        this.tempMax = tempMax;
    }
}
