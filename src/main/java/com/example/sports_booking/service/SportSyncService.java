package com.example.sports_booking.service;

import com.example.sports_booking.entity.Sport;
import com.example.sports_booking.repository.SportRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SportSyncService {

    private final SportRepository repo;
    private final RestTemplate rest = new RestTemplate();

    @PostConstruct
    @SuppressWarnings("unchecked")
    public void sync() {
        try {
            String url = "https://stapubox.com/sportslist/";
            Map<String, Object> response = rest.getForObject(url, Map.class);

            List<Map<String, Object>> sports =
                    (List<Map<String, Object>>) response.get("data");

            for (var s : sports) {
                String sportId = String.valueOf(s.get("sport_id"));
                repo.findBySportId(sportId)
                        .orElseGet(() -> {
                            Sport sp = new Sport();
                            sp.setSportId(sportId);
                            sp.setSportName(s.get("sport_name").toString());
                            return repo.save(sp);
                        });
            }
        } catch (Exception e) {
            // Silently fail - API might be unavailable on startup
            // Log can be added if needed
        }
    }
}
