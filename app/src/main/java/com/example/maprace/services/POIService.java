package com.example.maprace.services;

import android.os.AsyncTask;

import androidx.core.util.Consumer;

import org.osmdroid.bonuspack.location.NominatimPOIProvider;
import org.osmdroid.bonuspack.location.POI;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.List;

public class POIService extends AsyncTask<Object, Void, List<POI>>  {
    private final Consumer<List<POI>> consumer;

    public POIService(Consumer<List<POI>> consumer) {
        this.consumer = consumer;
    }

    public static void fetchPOIs(GeoPoint startPoint, String[] poiTypes, int maxResultsPerCategory,
                                 double maxDistance, Consumer<List<POI>> consumer) {
        new POIService(consumer).execute(startPoint, poiTypes, maxResultsPerCategory, maxDistance);
    }

    protected List<POI> doInBackground(Object... params) {
        GeoPoint startPoint = (GeoPoint) params[0];
        String[] poiTypes = (String[]) params[1];
        Integer maxResultsPerCategory = (Integer) params[2];
        Double maxDistance = (Double) params[3];
        NominatimPOIProvider poiProvider = new NominatimPOIProvider("OSMBonusPackTutoUserAgent");
        //GeoNamesPOIProvider poiProvider = new GeoNamesPOIProvider("maprace"); // get wikipedia entries
        List<POI> pois = new ArrayList<POI>();
        for (String poiType : poiTypes) {
            List<POI> pois_of_type = poiProvider.getPOICloseTo(startPoint, poiType, maxResultsPerCategory, maxDistance);
            pois.addAll(pois_of_type);
        }
        return pois;
    }

    protected void onPostExecute(List<POI> pois) {
        if (pois == null || pois.isEmpty()) {
            System.err.println("Problems occurred when fetching POIs - Empty array");
            // TODO: What to do if no nearby locations exists ?
        } else {
            // TODO: Shuffle POIs according to preference ranking ?
            consumer.accept(pois);
        }
    }
}
