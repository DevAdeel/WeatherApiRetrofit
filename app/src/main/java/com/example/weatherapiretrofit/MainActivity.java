package com.example.weatherapiretrofit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    public static final String BaseUrl = "http://api.openweathermap.org/";
    public static final String ApiKey = "eb85ef4dabd846892e3ca9efbfe79b13";
    EditText input_cityName;
    TextView txt_weatherResult;
    Button btn_getWeather;
    public static String lat = "35";
    public static String lon = "139";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        input_cityName = findViewById(R.id.input_cityName);
        txt_weatherResult = findViewById(R.id.txt_weatherResult);
        btn_getWeather = findViewById(R.id.btn_getWeather);
        btn_getWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //CheckPermissions();
                ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.INTERNET},210);
                getCurrentData(input_cityName.getText().toString());
            }
        });
    }

    void getCurrentData(final String cityName) {
        try{
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        WeatherService service = retrofit.create(WeatherService.class);
        Call<WebApi> call = service.getCurrentWeatherData(cityName, ApiKey);
        call.enqueue(new Callback<WebApi>() {
            @Override
            public void onResponse(Call<WebApi> call, Response<WebApi> response) {
                if (response.isSuccessful()) {
                    WebApi weatherResponse = response.body();
                    assert weatherResponse != null;

                    String stringBuilder = "City: "+cityName + "," + weatherResponse.sys.getCountry() +
                            "\n" +
                            "Temperature: " +
                            weatherResponse.main.getTemp() +
                            "\n" +
                            "Temperature(Min): " +
                            weatherResponse.main.getTempMin() +
                            "\n" +
                            "Temperature(Max): " +
                            weatherResponse.main.getTempMax() +
                            "\n" +
                            "Clouds: " +
                            weatherResponse.clouds.getAll() +
                            "\n" +
                            "Pressure: " +
                            weatherResponse.main.getPressure();

                    txt_weatherResult.setText(stringBuilder);
                }
                else{
                    txt_weatherResult.setText("Not Found! Check City Name");
                }
            }

            @Override
            public void onFailure(Call<WebApi> call, Throwable t) {
                txt_weatherResult.setText(t.getMessage());
            }
        });}
        catch (Exception ex)
        {
            txt_weatherResult.setText(ex.toString());
        }
    }

    void CheckPermissions() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) {
            /*Toast.makeText(MainActivity.this,"Permission is Already Granted!",Toast.LENGTH_SHORT).show();*/
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.INTERNET)) {
                new AlertDialog.Builder(this)
                        .setTitle("Internet Permission Needed")
                        .setMessage("Internet Permission is Needed to Access Weather Information")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.INTERNET}, 210);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create().show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, 210);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 210) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.INTERNET)) {
                    new AlertDialog.Builder(this)
                            .setTitle("Internet Permission Needed")
                            .setMessage("Internet Permission is Compulsory for this Feature \r\nGrant Now?")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent();
                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                                    intent.setData(uri);
                                    startActivity(intent);
                                    Toast.makeText(MainActivity.this, "Grant Permission from Permission Tab", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .create().show();
                }
            }
        }

    }
}
