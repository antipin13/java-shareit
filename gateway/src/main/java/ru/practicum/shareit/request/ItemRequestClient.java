package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItems;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpMethod;

import java.net.URI;
import java.util.List;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRequestClient {

    final RestTemplate restTemplate;

    @Value("${shareit-server.url}")
    String baseUrl;

    public ItemRequestClient() {
        this.restTemplate = new RestTemplate();
    }

    public ItemRequestDto saveRequest(Long userId, NewItemRequest request) {
        URI uri = UriComponentsBuilder.fromHttpUrl(baseUrl + "/requests").build().toUri();
        HttpHeaders headers = createHeaders(userId);
        HttpEntity<NewItemRequest> entity = new HttpEntity<>(request, headers);
        ResponseEntity<ItemRequestDto> response = restTemplate.postForEntity(uri, entity, ItemRequestDto.class);
        return response.getBody();
    }

    public List<ItemRequestDto> findItemRequestsByRequestorId(Long userId) {
        URI uri = UriComponentsBuilder.fromHttpUrl(baseUrl + "/requests").build().toUri();
        HttpHeaders headers = createHeaders(userId);
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<List<ItemRequestDto>> response = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<ItemRequestDto>>() {}
        );
        return response.getBody();
    }

    public List<ItemRequestDto> findItemRequestsOtherUsers(Long userId) {
        URI uri = UriComponentsBuilder.fromHttpUrl(baseUrl + "/requests/all").build().toUri();
        HttpHeaders headers = createHeaders(userId);
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<List<ItemRequestDto>> response = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<ItemRequestDto>>() {}
        );
        return response.getBody();
    }

    public ItemRequestDtoWithItems findById(Long id) {
        URI uri = UriComponentsBuilder.fromHttpUrl(baseUrl + "/requests/" + id).build().toUri();
        HttpHeaders headers = createHeaders(null); // Заголовок не нужен, если не требуется
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<ItemRequestDtoWithItems> response = restTemplate.exchange(uri, HttpMethod.GET, entity, ItemRequestDtoWithItems.class);
        return response.getBody();
    }

    private HttpHeaders createHeaders(Long userId) {
        HttpHeaders headers = new HttpHeaders();
        if (userId != null) {
            headers.add("X-Sharer-User-Id", String.valueOf(userId));
        }
        return headers;
    }
}
