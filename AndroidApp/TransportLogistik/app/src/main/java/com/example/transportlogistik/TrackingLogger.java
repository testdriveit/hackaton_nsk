package com.example.transportlogistik;

import android.os.AsyncTask;
import android.os.Build;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class TrackingLogger {

    private int vehicleId;

    //Сервер для логирования
    private final String urlForLogs = "http://185.135.83.140:8080/rest/v1/input";

    TrackingLogger(int vehicleId){
        this.vehicleId = vehicleId;

    }

    private JSONObject getOriginJSON(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("vehicleid", vehicleId);
            return jsonObject;

        } catch (JSONException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public void Log(String latitude, String longitude){
        JSONObject jsonForLog = getOriginJSON();
        if (jsonForLog == null) {
            return;
        }
        try {
            jsonForLog.put("latitude", latitude);
            jsonForLog.put("longitude", longitude);
        } catch (JSONException ex) {
            ex.printStackTrace();
            return;
        }

        AsyncLogRequest asyncLogRequest = new AsyncLogRequest();
        asyncLogRequest.execute(jsonForLog);
    }

    //JSON логер
    private class AsyncLogRequest extends AsyncTask<JSONObject, Void, Void> {
        @Override
        protected Void doInBackground(JSONObject... jsonObjects) {
            try {
                URL url = new URL(urlForLogs);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
                wr.writeBytes(jsonObjects[0].toString());
                wr.flush();
                wr.close();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = bufferedReader.readLine()) != null) {
                    response.append(inputLine);
                }
                bufferedReader.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
