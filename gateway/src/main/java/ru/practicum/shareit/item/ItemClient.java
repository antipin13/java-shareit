package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.shareit.item.dto.*;

import java.net.URI;
import java.util.List;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemClient {

    final RestTemplate restTemplate;

    @Value("${shareit-server.url}")
    String baseUrl;

    public ItemClient() {
        this.restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory(HttpClients.createDefault()));
    }

    public ItemDto saveItem(Long userId, NewItemRequest request) {
        URI uri = UriComponentsBuilder.fromHttpUrl(baseUrl + "/items").build().toUri();
        HttpHeaders headers = createHeaders(userId);
        HttpEntity<NewItemRequest> entity = new HttpEntity<>(request, headers);
        ResponseEntity<ItemDto> response = restTemplate.postForEntity(uri, entity, ItemDto.class);
        return response.getBody();
    }

    public ItemDto updateItem(Long userId, UpdateItemRequest request, Long itemId) {
        URI uri = UriComponentsBuilder.fromHttpUrl(baseUrl + "/items/" + itemId).build().toUri();
        HttpHeaders headers = createHeaders(userId);
        HttpEntity<UpdateItemRequest> entity = new HttpEntity<>(request, headers);
        ResponseEntity<ItemDto> response = restTemplate.exchange(uri, HttpMethod.PATCH, entity, ItemDto.class);
        return response.getBody();
    }

    public ItemWithCommentsDto findItemById(Long userId, Long itemId) {
        URI uri = UriComponentsBuilder.fromHttpUrl(baseUrl + "/items/" + itemId).build().toUri();
        HttpHeaders headers = createHeaders(userId);
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<ItemWithCommentsDto> response = restTemplate.exchange(uri, HttpMethod.GET, entity, ItemWithCommentsDto.class);
        return response.getBody();
    }

    public List<ItemWithDateDto> findItemsByUserId(Long userId) {
        URI uri = UriComponentsBuilder.fromHttpUrl(baseUrl + "/items").build().toUri();
        HttpHeaders headers = createHeaders(userId);
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<List<ItemWithDateDto>> response = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<ItemWithDateDto>>() {}
        );
        return response.getBody();
    }

    public List<ItemDto> findItemsByTextAndAvailable(Long userId, String text) {
        URI uri = UriComponentsBuilder.fromHttpUrl(baseUrl + "/items/search")
                .queryParam("text", text)
                .build()
                .toUri();
        HttpHeaders headers = createHeaders(userId);
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<List<ItemDto>> response = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<ItemDto>>() {}
        );
        return response.getBody();
    }

    public CommentDto saveComment(Long itemId, Long userId, NewCommentRequest request) {
        URI uri = UriComponentsBuilder.fromHttpUrl(baseUrl + "/items/" + itemId + "/comment").build().toUri();
        HttpHeaders headers = createHeaders(userId);
        HttpEntity<NewCommentRequest> entity = new HttpEntity<>(request, headers);
        ResponseEntity<CommentDto> response = restTemplate.postForEntity(uri, entity, CommentDto.class);
        return response.getBody();
    }

    private HttpHeaders createHeaders(Long userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Sharer-User-Id", String.valueOf(userId));
        return headers;
    }
}
