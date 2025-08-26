package ru.practicum.shareit.user;

import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.shareit.user.dto.UserDto;

import java.net.URI;

@Component
public class UserClient {
    private final RestTemplate restTemplate;

    @Value("${shareit-server.url}")
    private String baseUrl;

    public UserClient() {
        this.restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory(HttpClients.createDefault()));
    }

    public UserDto saveUser(NewUserRequest request) {
        URI uri = UriComponentsBuilder.fromHttpUrl(baseUrl + "/users").build().toUri();
        HttpEntity<NewUserRequest> entity = new HttpEntity<>(request);
        ResponseEntity<UserDto> response = restTemplate.postForEntity(uri, entity, UserDto.class);
        return response.getBody();
    }

    public UserDto updateUser(UpdateUserRequest request, Long id) {
        URI uri = UriComponentsBuilder.fromHttpUrl(baseUrl + "/users/" + id).build().toUri();
        HttpEntity<UpdateUserRequest> entity = new HttpEntity<>(request);
        ResponseEntity<UserDto> response = restTemplate.exchange(uri, HttpMethod.PATCH, entity, UserDto.class);
        return response.getBody();
    }

    public UserDto findUserById(Long id) {
        URI uri = UriComponentsBuilder.fromHttpUrl(baseUrl + "/users/" + id).build().toUri();
        ResponseEntity<UserDto> response = restTemplate.getForEntity(uri, UserDto.class);
        return response.getBody();
    }

    public void removeUser(Long id) {
        URI uri = UriComponentsBuilder.fromHttpUrl(baseUrl + "/users/" + id).build().toUri();
        restTemplate.delete(uri);
    }
}
