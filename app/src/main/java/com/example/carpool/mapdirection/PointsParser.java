package com.example.carpool.mapdirection;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import com.example.carpool.common.ApplicationContext;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


public class PointsParser extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
    TaskLoadedCallback taskCallback;
    String directionMode = "driving";
    GoogleMap map;

    private static final String TAG = "PointsParser";

    public PointsParser(Context mContext, String directionMode, GoogleMap map) {
        this.taskCallback = (TaskLoadedCallback) mContext;
        this.directionMode = directionMode;
        this.map = map;
    }

    // Parsing the data in non-ui thread
    @Override
    protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

        JSONObject jObject;
        List<List<HashMap<String, String>>> routes = null;

        try {
            jObject = new JSONObject(jsonData[0]);

            //All these
            JSONArray routesNew = jObject.getJSONArray("routes");

            JSONObject newObject = routesNew.getJSONObject(0);

            JSONArray legs = newObject.getJSONArray("legs");

            JSONObject legsObject = legs.getJSONObject(0);

            JSONObject time = legsObject.getJSONObject("duration");
            String duration = time.getString("text");

            Log.i("PointsParser", "doInBackground: " + duration);
            ApplicationContext.setDuration(duration);

            DataParser parser = new DataParser();

            // Starts parsing data
            routes = parser.parse(jObject);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return routes;
    }

    // Executes in UI thread, after the parsing process
    @Override
    protected void onPostExecute(List<List<HashMap<String, String>>> result) {
        ArrayList<LatLng> points;
        PolylineOptions lineOptions = null;
        // Traversing through all the routes
        if (result!= null) {
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();
                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);
                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);
                    double lat = Double.parseDouble(Objects.requireNonNull(point.get("lat")));
                    double lng = Double.parseDouble(Objects.requireNonNull(point.get("lng")));
                    LatLng position = new LatLng(lat, lng);
                    points.add(position);
                }
                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                if (directionMode.equalsIgnoreCase("walking")) {
                    lineOptions.width(10);
                    lineOptions.color(Color.rgb(0, 197, 154));
                } else {
                    lineOptions.width(10);
                    lineOptions.color(Color.rgb(0, 197, 154));
                }
            }

            // Drawing polyline in the Google Map for the i-th route
            if (lineOptions != null) {
                map.addPolyline(lineOptions);
                taskCallback.onTaskDone(lineOptions);

            } else {
                Log.d("MinhMX", "without Polylines drawn");
            }
        }
    }
}
