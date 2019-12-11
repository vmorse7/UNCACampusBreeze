package com.unca.android.uncacampusbreeze;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapPolygons {
    private static MapPolygons instance = null;

    private Map<String, Polygon> mPolygonsOnMap;

    private MapPolygons() {
        mPolygonsOnMap = new HashMap<String, Polygon>();
    }

    public static MapPolygons getInstance() {
        if (instance == null) {
            instance = new MapPolygons();
        }
        return  instance;
    }

    public void insertPolygonInMap(String polygonName, Polygon polygon) {
        mPolygonsOnMap.put(polygonName, polygon);
    }

    public Map<String, Polygon> getAllPolygonsInMap() {
        return mPolygonsOnMap;
    }

    public Map<String, Polygon> getAllPolygonsDeviceIsIn(LatLng locationOfDevice) {
        Map<String, Polygon> a = new HashMap<>();
        for (String polygonKey : mPolygonsOnMap.keySet()) {
            if (isDeviceInPolygon(locationOfDevice, mPolygonsOnMap.get(polygonKey))) {
                a.put(polygonKey, mPolygonsOnMap.get(polygonKey));
            }
        }
        return a;
    }

    private boolean isDeviceInPolygon(LatLng locationOfDevice, Polygon polygon) {
        // en.wikipedia.org/wiki/Point_in_polygon
        boolean inPolygon = false;
        List<LatLng> points = polygon.getPoints();

        LatLng j = points.get(points.size() - 1);
        for (LatLng i : points) {
            if ((i.latitude > locationOfDevice.latitude) != (j.latitude > locationOfDevice.latitude)) {
                double a = i.longitude + (j.longitude - i.longitude) * (locationOfDevice.latitude - i.latitude) / (j.latitude - i.latitude);
                if (locationOfDevice.longitude < a) {
                    inPolygon = !inPolygon;
                }
            }
            j = i;
        }
        return inPolygon;
    }
}
