package com.spreadsheet.service.impl;

import com.spreadsheet.model.dto.UserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserProfileServiceImplTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private UserProfileServiceImpl userProfileService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(userProfileService, "userProfileServiceUrl", "http://localhost:8080");
    }

    @Test
    void testIsValidUser_ValidActiveUser_ReturnsTrue() {
        // Arrange
        String userId = "1";
        UserResponse userProfile = new UserResponse();
        userProfile.setUserId(1L);

        ResponseEntity<UserResponse> response = new ResponseEntity<>(userProfile, HttpStatus.OK);
        when(restTemplate.getForEntity(eq("http://localhost:8080/v1/user/1"), eq(UserResponse.class)))
                .thenReturn(response);

        // Act
        boolean result = userProfileService.isValidUser(userId);

        // Assert
        assertTrue(result);
    }

    @Test
    void testIsValidUser_ValidInactiveUser_ReturnsFalse() {
        // Arrange
        String userId = "1";
        UserResponse userProfile = new UserResponse();
        userProfile.setUserId(null);

        ResponseEntity<UserResponse> response = new ResponseEntity<>(userProfile, HttpStatus.OK);
        when(restTemplate.getForEntity(eq("http://localhost:8080/v1/user/1"), eq(UserResponse.class)))
                .thenReturn(response);

        // Act
        boolean result = userProfileService.isValidUser(userId);

        // Assert
        assertFalse(result);
    }

    @Test
    void testIsValidUser_UserNotFound_ReturnsFalse() {
        // Arrange
        String userId = "999";
        when(restTemplate.getForEntity(eq("http://localhost:8080/v1/user/999"), eq(UserResponse.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        // Act
        boolean result = userProfileService.isValidUser(userId);

        // Assert
        assertFalse(result);
    }

    @Test
    void testIsValidUser_InvalidUserIdFormat_ReturnsFalse() {
        // Act
        boolean result = userProfileService.isValidUser("invalid");

        // Assert
        assertFalse(result);
    }

    @Test
    void testIsValidUser_NullUserId_ReturnsFalse() {
        // Act
        boolean result = userProfileService.isValidUser(null);

        // Assert
        assertFalse(result);
    }

    @Test
    void testIsValidUser_EmptyUserId_ReturnsFalse() {
        // Act
        boolean result = userProfileService.isValidUser("");

        // Assert
        assertFalse(result);
    }
}
