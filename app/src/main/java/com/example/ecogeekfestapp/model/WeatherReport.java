package com.example.ecogeekfestapp.model;

public class WeatherReport {
    Main main;

    public Main getMain() {
        return main;
    }

    public class Main {
        float temp;
        int humidity;

        public float getTemp() {
            return temp;
        }

        public int getHumidity() {
            return humidity;
        }
    }

}
