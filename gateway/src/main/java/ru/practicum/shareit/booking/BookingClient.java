package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
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

import java.net.URI;
import java.util.List;

@Component
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingClient {

    final RestTemplate restTemplate;

    @Value("${shareit-server.url}")
    String baseUrl;

    public BookingClient() {
        this.restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory(HttpClients.createDefault()));
    }

    public BookingDto saveBooking(Long userId, NewBookingRequest request) {
        URI uri = UriComponentsBuilder.fromHttpUrl(baseUrl + "/bookings").build().toUri();
        HttpHeaders headers = createHeaders(userId);
        HttpEntity<NewBookingRequest> entity = new HttpEntity<>(request, headers);
        ResponseEntity<BookingDto> response = restTemplate.postForEntity(uri, entity, BookingDto.class);
        return response.getBody();
    }

    public BookingDto approveBooking(Long id, Long userId, Boolean approved) {
        URI uri = UriComponentsBuilder.fromHttpUrl(baseUrl + "/bookings/" + id)
                .queryParam("approved", approved)
                .build().toUri();
        HttpHeaders headers = createHeaders(userId);
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<BookingDto> response = restTemplate.exchange(
                uri,
                HttpMethod.PATCH,
                entity,
                BookingDto.class
        );
        return response.getBody();
    }

    public BookingDto getBookingByBookerIdOrOwnerId(Long id, Long userId) {
        URI uri = UriComponentsBuilder.fromHttpUrl(baseUrl + "/bookings/" + id).build().toUri();
        HttpHeaders headers = createHeaders(userId);
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<BookingDto> response = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                entity,
                BookingDto.class
        );
        return response.getBody();
    }

    public List<BookingDto> getBookingByBooker(Long userId) {
        URI uri = UriComponentsBuilder.fromHttpUrl(baseUrl + "/bookings").build().toUri();
        HttpHeaders headers = createHeaders(userId);
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<List<BookingDto>> response = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<BookingDto>>() {
                }
        );
        return response.getBody();
    }

    private HttpHeaders createHeaders(Long userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Sharer-User-Id", String.valueOf(userId));
        return headers;
    }
}
