package com.example.echolauncher;

import android.graphics.Color;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WeatherWidget extends Widget {
    WeatherWidget() {
        super.identifier = "widget.weather";
        super.color = Color.CYAN;
        super.textPositions.put('L', "Retrieving weather...");

        client = new OkHttpClient();
        request = new Request.Builder()
                .url(URL)
                .build();
    }

    @Override
    public void Tick() {
        getWeather();

        super.textPositions.put('L', temperature);
        super.textPositions.put('R', state);
    }

    private void getWeather() {
        String httpResponse;

        try {
            Response response = client.newCall(request).execute();
            httpResponse = response.body().string();
            reader = new JSONObject(httpResponse);
            temperature = reader.getJSONObject("main").getString("temp");
            temperature += "°C";
            state = reader.getJSONArray("weather").getJSONObject(0).getString("main");
        } catch (IOException e) {
            temperature = "Error getting weather!\nPlease check your connection";
            state = "";
        } catch (JSONException e) {
            temperature = "Failed to parse JSON";
            state = "";
        }
    }

    private final String API_KEY = "22be092a97f45bcefc68001640396830",
        URL = "https://api.openweathermap.org/data/2.5/weather?lat=51.5&lon=-0.11&units=metric&appid=" + API_KEY;
    private OkHttpClient client;
    private Request request;
    private JSONObject reader;
    private String temperature, state;
}
