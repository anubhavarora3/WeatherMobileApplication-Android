package com.example.anubharora.stormy;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.anubharora.stormy.fragments.AlertDialogFragment;
import com.example.anubharora.stormy.models.CurrentWeather;
import com.example.anubharora.stormy.models.WeatherDetails;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

    CurrentWeather mCurrentWeather;
    WeatherDetails mWeekWeather;
    Description d = null;

    // TextView temp;
    @BindView(R.id.temp)
    TextView tempratureLabel;
    @BindView(R.id.degree)
    ImageView degree;
    @BindView(R.id.time)
    TextView time;
    @BindView(R.id.location)
    TextView location;
    @BindView(R.id.icon)
    ImageView icon;
    @BindView(R.id.refresh)
    ImageView refresh;
    @BindView(R.id.humidityVal)
    TextView humidityValue;
    @BindView(R.id.precepVal)
    TextView precepValue;
    @BindView(R.id.summary)
    TextView summary;
    @BindView(R.id.progressBar)
    ProgressBar progress;
    @BindView(R.id.lChart)
    LineChart mLineChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        progress.setVisibility(View.INVISIBLE);

        //linechart
        ArrayList<Entry> arrayList = new ArrayList<>();
        arrayList.add(new Entry(0f, 100000f));
        arrayList.add(new Entry(1f, 120000f));
        arrayList.add(new Entry(2f, 140000f));
        arrayList.add(new Entry(3f, 110000f));
        arrayList.add(new Entry(4f, 100000f));
        arrayList.add(new Entry(5f, 160000f));

        LineDataSet lineDataSet = new LineDataSet(arrayList,"#temprature");
        lineDataSet.setColor(Color.BLUE);
        lineDataSet.setValueTextColor(Color.BLACK);
        LineData lineData = new LineData(lineDataSet);

        mLineChart.setData(lineData);
        mLineChart.invalidate();
        mLineChart.getAxisLeft().setEnabled(false);
        mLineChart.getAxisRight().setEnabled(false);
        mLineChart.getXAxis().setEnabled(false);
        mLineChart.setTouchEnabled(false);
        mLineChart.getAxisLeft().setDrawGridLines(false);
        mLineChart.getXAxis().setDrawGridLines(false);
        mLineChart.getAxisRight().setDrawGridLines(false);
        mLineChart.setDescription(d);
        mLineChart.getLegend().setEnabled(false);



        final double latitude = 12.9716;
        final double longitude = 77.5946;

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getForecast(latitude, longitude);
            }
        });

        getForecast(latitude, longitude);

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                    .build(MainActivity.this);
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void getForecast(double latitude, double longitude) {
        final String apiKey = "bbf5d5e4a2a098491449279cd62f0baa";

        String forecastUrl = "https://api.darksky.net/forecast/" + apiKey + "/"
                + latitude + "," + longitude;

        if (isNetworkAvailable()) {

            toggelRefresh();

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(forecastUrl)
                    .build();

            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toggelRefresh();
                        }
                    });
                    toggelRefresh();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toggelRefresh();
                        }
                    });
                    try {
                        String jsonData = response.body().string();
                        //Log.v(TAG, jsonData);
                        if (response.isSuccessful()) {
                            mCurrentWeather = getCurrentDetails(jsonData);
                            mWeekWeather = getWeekWeather(jsonData);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateDisplay();
                                }
                            });


                        } else {

                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            Toast.makeText(MainActivity.this, R.string.network_failure, Toast.LENGTH_LONG).show();
        }
    }

    private void toggelRefresh() {

        if (progress.getVisibility() == View.INVISIBLE) {
            progress.setVisibility(View.VISIBLE);
            refresh.setVisibility(View.INVISIBLE);
        } else {
            progress.setVisibility(View.INVISIBLE);
            refresh.setVisibility(View.VISIBLE);
        }

    }


    private CurrentWeather getCurrentDetails(String jsonData) throws JSONException {

        JSONObject forecast = new JSONObject(jsonData);
        String timezone = forecast.getString("timezone");
        JSONObject currently = forecast.getJSONObject("currently");

        JSONObject daily = forecast.getJSONObject("daily");
        JSONArray dailyData = daily.getJSONArray("data");

        WeatherDetails weatherDetails = new WeatherDetails();

        for (int i = 0; i < dailyData.length(); i++) {

            JSONObject details = dailyData.getJSONObject(i);
            weatherDetails.setIcon(details.getString("icon"));
            weatherDetails.setTempMin(details.getDouble("temperatureMin"));
            weatherDetails.setTempMax(details.getDouble("temperatureMax"));
        }

        CurrentWeather currentweather = new CurrentWeather();
        currentweather.setHumidity(currently.getDouble("humidity"));
        currentweather.setTime(currently.getLong("time"));
        currentweather.setIcon(currently.getString("icon"));
        currentweather.setPrecipChance(currently.getDouble("precipProbability"));
        currentweather.setSummary(currently.getString("summary"));
        currentweather.setTemprature(currently.getDouble("temperature"));
        currentweather.setTimezone(timezone);
        return currentweather;
    }

    private WeatherDetails getWeekWeather(String jsonData) throws JSONException {

        JSONObject forecast = new JSONObject(jsonData);
        JSONObject daily = forecast.getJSONObject("daily");
        JSONArray dailyData = daily.getJSONArray("data");

        WeatherDetails weatherDetails = new WeatherDetails();

        for (int i = 0; i < dailyData.length(); i++) {

            JSONObject details = dailyData.getJSONObject(i);
            weatherDetails.setIcon(details.getString("icon"));
            weatherDetails.setTempMin(details.getDouble("temperatureMin"));
            weatherDetails.setTempMax(details.getDouble("temperatureMax"));
        }
        return weatherDetails;
    }

    private void updateDisplay() {
        tempratureLabel.setText(mCurrentWeather.getTemprature() + "");
        time.setText("At " + mCurrentWeather.getFormattedTime() + " it will be");
        humidityValue.setText(mCurrentWeather.getHumidity() + "");
        precepValue.setText(mCurrentWeather.getPrecipChance() + "");
        summary.setText(mCurrentWeather.getSummary());

        Drawable drawable = getResources().getDrawable(mCurrentWeather.getIconId());
        icon.setImageDrawable(drawable);

    }

    private boolean isNetworkAvailable() {

        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();

        boolean isAvailable = false;
        if (info != null && info.isConnected()) {
            isAvailable = true;
        }

        return isAvailable;
    }

    private void alertUserAboutError() {
        AlertDialogFragment fragment = new AlertDialogFragment();
        fragment.show(getFragmentManager(), "Error_Dialog");
    }

//    private void changeLocation(View v) {
//
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                String address = trimPlace(place.getAddress().toString());
                location.setText(address);
                LatLng latLng = place.getLatLng();
                getForecast(latLng.latitude, latLng.longitude);
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }

        }
    }

    private String trimPlace(String place) {

        String rplace = "";
        place = place + ",";
        ArrayList<String> arlist = new ArrayList<String>();
        String temp = "";
        int count = 0;
        for (int i = 0; i < place.length(); ) {
            temp = "";
            while (place.charAt(i) != ',') {
                temp += place.charAt(i);
                ++i;
            }
            ++count;
            ++i;
            arlist.add(temp);
        }
        rplace = arlist.get(0) + ", " + arlist.get(count - 1);
        return rplace;
    }
}
