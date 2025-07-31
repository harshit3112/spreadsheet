package com.spreadsheet.service.impl;

import com.spreadsheet.model.dto.UserResponse;
import com.spreadsheet.service.UserProfileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@Service
@Slf4j
public class UserProfileServiceImpl implements UserProfileService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${user.profile.service.url:http://localhost:8080}")
    private String userProfileServiceUrl;

    @Override
    public boolean isValidUser(String userId) {
        try {
            log.info("Validating user with ID: {}", userId);
            
            // Convert String to Long for API call
            Long userIdLong;
            try {
                userIdLong = Long.parseLong(userId.trim());
            } catch (NumberFormatException e) {
                log.warn("Invalid user ID format: {}", userId);
                return false;
            }
            
            String url = userProfileServiceUrl + "/v1/user/" + userIdLong;
            ResponseEntity<UserResponse> response = restTemplate.getForEntity(url, UserResponse.class);
            
            if (response.getStatusCode() == HttpStatus.OK && Objects.nonNull(response.getBody())) {
                UserResponse userProfile = response.getBody();
                boolean isValid = Objects.nonNull(userProfile.getUserId());
                
                log.info("User validation result for ID {}: {}", userId, isValid);
                return isValid;
            }
            
            log.warn("Invalid response received for user ID: {}", userId);
            return false;
            
        } catch (HttpClientErrorException.NotFound e) {
            log.warn("User not found with ID: {}", userId);
            return false;
        } catch (Exception e) {
            log.error("Error validating user with ID: {}", userId, e);
            return false;
        }
    }
}
