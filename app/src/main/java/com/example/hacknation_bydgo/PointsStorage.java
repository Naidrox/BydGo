package com.example.hacknation_bydgo;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PointsStorage {

    private static final String PREF_NAME = "points_storage";
    private static final String KEY_POINTS = "points";

    public static void savePoints(Context context, Point[] points) {
        JSONArray array = new JSONArray();

        try {
            for (Point p : points) {
                JSONObject obj = new JSONObject();
                obj.put("id", p.getId());
                obj.put("lat", p.getLat());
                obj.put("lon", p.getLon());
                obj.put("name", p.getName());
                obj.put("description", p.getDescription());
                obj.put("visited", p.isVisited());
                array.put(obj);
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit()
                .putString(KEY_POINTS, array.toString())
                .apply();
    }

    public static Point[] loadPoints(Context context, Point[] defaultPoints) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_POINTS, null);

        if (json == null) {
            savePoints(context, defaultPoints);
            return defaultPoints;
        }

        try {
            JSONArray array = new JSONArray(json);
            // Odczyt istniejących
            List<Point> existingPoints = new  ArrayList<>();
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                existingPoints.add(new Point(
                        obj.getLong("id"),
                        obj.getDouble("lat"),
                        obj.getDouble("lon"),
                        obj.getString("name"),
                        obj.getString("description"),
                        obj.getBoolean("visited")
                ));
            }

            // Sprawdzenie nowych punktów z repo
            for (Point p : defaultPoints) {
                boolean found = false;
                for (Point ep : existingPoints) {
                    if (ep.getId() == p.getId()) {
                        found = true;
                        break;
                    }
                }
                if (!found) existingPoints.add(p); // dodaj nowy punkt
            }

            Point[] result = existingPoints.toArray(new Point[0]);
            savePoints(context, result); // zapisz zaktualizowaną listę
            return result;

        } catch (JSONException e) {
            savePoints(context, defaultPoints);
            return defaultPoints;
        }
    }

}
