package com.onuryigitkocaturk.query_monitor.geocoding;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Locale;

/**
 * Tarayicidan alinan ham enlem/boylami ("39.78, 32.81" gibi) okunur bir
 * il/ilce adina ("Cankaya, Ankara") cevirir - OpenStreetMap'in ucretsiz,
 * API anahtari gerektirmeyen Nominatim servisi ile (reverse geocoding).
 * Sadece login mailindeki gorseli zenginlestirmek icin kullanilir, hicbir
 * guven/yetkilendirme kararina girmez. Servis yavas/erisilemez olursa
 * cagiran taraf (LoginVerificationServiceImpl) ham koordinatlara geri doner
 * - login akisini asla bozmaz.
 */
@Component
public class GeocodingService {

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(3))
            .build();
    private final ObjectMapper objectMapper;

    public GeocodingService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /** Basarisiz olursa (zaman asimi, servis hatasi, bos sonuc) null doner. */
    public String reverseGeocode(double latitude, double longitude) {
        try {
            String url = String.format(Locale.US,
                    "https://nominatim.openstreetmap.org/reverse?lat=%f&lon=%f&format=json&accept-language=tr",
                    latitude, longitude);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    // Nominatim kullanim politikasi tanimlanabilir bir User-Agent zorunlu kilar.
                    .header("User-Agent", "GozcuMonitoringApp/1.0 (login-verification)")
                    .timeout(Duration.ofSeconds(3))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                return null;
            }

            JsonNode address = objectMapper.readTree(response.body()).get("address");
            if (address == null) {
                return null;
            }

            // Oncelik ilce seviyesinde: "suburb" bir mahalle oldugu icin (orn.
            // "Bahcelievler Mahallesi") bilerek en sona birakildi, sadece daha
            // iyi bir alan yoksa fallback olarak kullanilir.
            String district = firstNonBlank(address, "city_district", "town", "municipality", "county", "suburb");
            String province = firstNonBlank(address, "province", "state", "city");

            if (district != null && province != null && !district.equals(province)) {
                return district + ", " + province;
            }
            return province != null ? province : district;
        } catch (Exception e) {
            return null;
        }
    }

    private String firstNonBlank(JsonNode address, String... fields) {
        for (String field : fields) {
            JsonNode node = address.get(field);
            if (node != null && !node.asText().isBlank()) {
                return node.asText();
            }
        }
        return null;
    }
}
