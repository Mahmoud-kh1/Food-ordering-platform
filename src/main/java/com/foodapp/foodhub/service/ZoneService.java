package com.foodapp.foodhub.service;

import org.springframework.stereotype.Service;

@Service
public class ZoneService{

    private static final int EARTH_RADIUS_KM = 6371;

    /**
     * Calculates if a customer's location is within a restaurant radius.
     */
    public boolean isWithinDeliveryZone(double customerLat, double customerLon,
                                        double restaurantLat, double restaurantLon,
                                        double deliveryRadiusKm) {

        double distance = calculateDistanceInKm(customerLat, customerLon, restaurantLat, restaurantLon);

        return distance <= deliveryRadiusKm;
    }

    /**
     * Haversine formula to calculate distance between two lat/lon points.
     */
    private double calculateDistanceInKm(double lat1, double lon1, double lat2, double lon2) {
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_KM * c;
    }
}
