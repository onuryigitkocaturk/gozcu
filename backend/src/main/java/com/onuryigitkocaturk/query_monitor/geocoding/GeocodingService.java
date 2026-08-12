package com.onuryigitkocaturk.query_monitor.geocoding;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

// tarayıcıdan enlem boylam alındı, dışarıya istek atmadan csv dosyasındaki il/ilçelerin en yakın olanı seçildi
@Component
public class GeocodingService {

    private static final String RESOURCE_PATH = "tr_districts.csv";
    private static final double EARTH_RADIUS_KM = 6371.0;

    private static final double MAX_MATCH_DISTANCE_KM = 150.0;

    private record DistrictPoint(String district, String province, double latitude, double longitude) {
    }

    private final List<DistrictPoint> districts;

    public GeocodingService() {
        this.districts = loadDistricts();
    }

    // okuyamazsa null, okuyup 150km'den fazla fark varsa Türkiye dışı dönüyor.
    public String reverseGeocode(double latitude, double longitude) {
        if (districts.isEmpty()) {
            return null;
        }

        DistrictPoint nearest = null;
        double nearestDistanceKm = Double.MAX_VALUE;

        for (DistrictPoint point : districts) {
            double distanceKm = haversineDistanceKm(latitude, longitude, point.latitude(), point.longitude());
            if (distanceKm < nearestDistanceKm) {
                nearestDistanceKm = distanceKm;
                nearest = point;
            }
        }

        if (nearest == null) {
            return null;
        }
        if (nearestDistanceKm > MAX_MATCH_DISTANCE_KM) {
            return "Türkiye dışı";
        }
        return nearest.district() + ", " + nearest.province();
    }

    private double haversineDistanceKm(double lat1, double lon1, double lat2, double lon2) {
        double lat1Rad = Math.toRadians(lat1);
        double lat2Rad = Math.toRadians(lat2);
        double deltaLat = Math.toRadians(lat2 - lat1);
        double deltaLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2)
                + Math.cos(lat1Rad) * Math.cos(lat2Rad)
                * Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }

    // uygulama açılırken bir kez çalışır, sonuç bellekte tutulur, her çağrıda dosya tekrar okunmaz
    private List<DistrictPoint> loadDistricts() {
        List<DistrictPoint> result = new ArrayList<>();
        ClassPathResource resource = new ClassPathResource(RESOURCE_PATH);

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            String line = reader.readLine(); // baslik satiri (ilce,il,lat,lon) atlanir
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                String[] fields = line.split(",");
                if (fields.length != 4) {
                    continue;
                }
                String district = fields[0];
                String province = fields[1];
                double latitude = Double.parseDouble(fields[2]);
                double longitude = Double.parseDouble(fields[3]);
                result.add(new DistrictPoint(district, province, latitude, longitude));
            }
        } catch (IOException e) {
            throw new IllegalStateException("Il/ilce veri seti (" + RESOURCE_PATH + ") okunamadi", e);
        }

        return result;
    }
}
