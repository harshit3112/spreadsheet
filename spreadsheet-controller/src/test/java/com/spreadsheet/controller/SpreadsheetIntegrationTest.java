package com.spreadsheet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spreadsheet.model.dto.CreateSheetRequest;
import com.spreadsheet.model.dto.UpdateSheetRequest;
import com.spreadsheet.model.dto.CellUpdate;
import com.spreadsheet.model.enums.CellType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
class SpreadsheetIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateAndRetrieveSheet() throws Exception {
        // Create a new sheet
        CreateSheetRequest createRequest = new CreateSheetRequest("testUser");
        
        MvcResult createResult = mockMvc.perform(post("/v1/sheet")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("Sheet created successfully")))
                .andExpect(jsonPath("$.data", notNullValue()))
                .andReturn();

        // Extract sheet ID from response
        String responseContent = createResult.getResponse().getContentAsString();
        Long sheetId = objectMapper.readTree(responseContent).get("data").asLong();

        // Retrieve the created sheet
        mockMvc.perform(get("/v1/sheet/{id}", sheetId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("Sheet retrieved successfully")))
                .andExpect(jsonPath("$.data.userId", is("testUser")))
                .andExpect(jsonPath("$.data.createdAt", notNullValue()))
                .andExpect(jsonPath("$.data.sheetData", notNullValue()))
                .andExpect(jsonPath("$.data.userPermissions", hasSize(1)))
                .andExpect(jsonPath("$.data.userPermissions[0].userId", is("testUser")))
                .andExpect(jsonPath("$.data.userPermissions[0].permission", is("EDIT")));
    }

    @Test
    void testUpdateSheetWithValues() throws Exception {
        // Create a new sheet
        CreateSheetRequest createRequest = new CreateSheetRequest("testUser");
        
        MvcResult createResult = mockMvc.perform(post("/v1/sheet")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        Long sheetId = objectMapper.readTree(createResult.getResponse().getContentAsString())
                .get("data").asLong();

        // Update sheet with cell values
        CellUpdate cellUpdate1 = new CellUpdate(1, 1, CellType.VALUE, "10", null);
        CellUpdate cellUpdate2 = new CellUpdate(1, 2, CellType.VALUE, "20", null);
        CellUpdate cellUpdate3 = new CellUpdate(1, 3, CellType.EXPRESSION, null, "=A1+B1");
        
        UpdateSheetRequest updateRequest = new UpdateSheetRequest(
                Arrays.asList(cellUpdate1, cellUpdate2, cellUpdate3)
        );

        mockMvc.perform(put("/v1/sheet/{id}", sheetId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("Sheet updated successfully")))
                .andExpect(jsonPath("$.data.sheetData", hasKey("A1")))
                .andExpect(jsonPath("$.data.sheetData", hasKey("B1")))
                .andExpect(jsonPath("$.data.sheetData", hasKey("C1")))
                .andExpect(jsonPath("$.data.sheetData.A1.cellType", is("VALUE")))
                .andExpect(jsonPath("$.data.sheetData.A1.value", is("10")))
                .andExpect(jsonPath("$.data.sheetData.B1.cellType", is("VALUE")))
                .andExpect(jsonPath("$.data.sheetData.B1.value", is("20")))
                .andExpect(jsonPath("$.data.sheetData.C1.cellType", is("EXPRESSION")))
                .andExpect(jsonPath("$.data.sheetData.C1.expression", is("=A1+B1")))
                .andExpect(jsonPath("$.data.sheetData.C1.value", is("30")));
    }

    @Test
    void testUpdateSheetWithComplexExpressions() throws Exception {
        // Create a new sheet
        CreateSheetRequest createRequest = new CreateSheetRequest("testUser");
        
        MvcResult createResult = mockMvc.perform(post("/v1/sheet")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        Long sheetId = objectMapper.readTree(createResult.getResponse().getContentAsString())
                .get("data").asLong();

        // Update sheet with complex expressions
        CellUpdate cellUpdate1 = new CellUpdate(1, 1, CellType.VALUE, "5", null);
        CellUpdate cellUpdate2 = new CellUpdate(2, 1, CellType.VALUE, "10", null);
        CellUpdate cellUpdate3 = new CellUpdate(3, 1, CellType.VALUE, "15", null);
        CellUpdate cellUpdate4 = new CellUpdate(4, 1, CellType.EXPRESSION, null, "=SUM(A1:A3)");
        CellUpdate cellUpdate5 = new CellUpdate(5, 1, CellType.EXPRESSION, null, "=AVG(A1:A3)");
        
        UpdateSheetRequest updateRequest = new UpdateSheetRequest(
                Arrays.asList(cellUpdate1, cellUpdate2, cellUpdate3, cellUpdate4, cellUpdate5)
        );

        mockMvc.perform(put("/v1/sheet/{id}", sheetId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.sheetData.A4.value", is("30.0")))
                .andExpect(jsonPath("$.data.sheetData.A5.value", is("10.0")));
    }

    @Test
    void testCreateSheetWithInvalidInput() throws Exception {
        // Test with empty user ID
        CreateSheetRequest invalidRequest = new CreateSheetRequest("");
        
        mockMvc.perform(post("/v1/sheet")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateNonExistentSheet() throws Exception {
        CellUpdate cellUpdate = new CellUpdate(1, 1, CellType.VALUE, "10", null);
        UpdateSheetRequest updateRequest = new UpdateSheetRequest(Arrays.asList(cellUpdate));

        mockMvc.perform(put("/v1/sheet/{id}", 999L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.error", containsString("Sheet not found")));
    }

    @Test
    void testRetrieveNonExistentSheet() throws Exception {
        mockMvc.perform(get("/v1/sheet/{id}", 999L))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.error", containsString("Sheet not found")));
    }

    @Test
    void testUpdateSheetWithInvalidCellUpdate() throws Exception {
        // Create a new sheet first
        CreateSheetRequest createRequest = new CreateSheetRequest("testUser");
        
        MvcResult createResult = mockMvc.perform(post("/v1/sheet")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        Long sheetId = objectMapper.readTree(createResult.getResponse().getContentAsString())
                .get("data").asLong();

        // Test with invalid row number
        CellUpdate invalidCellUpdate = new CellUpdate(0, 1, CellType.VALUE, "10", null);
        UpdateSheetRequest updateRequest = new UpdateSheetRequest(Arrays.asList(invalidCellUpdate));

        mockMvc.perform(put("/v1/sheet/{id}", sheetId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest());
    }
}
