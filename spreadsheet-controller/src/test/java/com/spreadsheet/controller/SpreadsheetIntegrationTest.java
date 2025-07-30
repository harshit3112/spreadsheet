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
        CreateSheetRequest createRequest = new CreateSheetRequest();
        createRequest.setName("Test Sheet");
        createRequest.setUserId("testUser");
        createRequest.setDescription("Test Description");
        
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
        CreateSheetRequest createRequest = new CreateSheetRequest();
        createRequest.setName("Test Sheet");
        createRequest.setUserId("testUser");
        
        MvcResult createResult = mockMvc.perform(post("/v1/sheet")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        Long sheetId = objectMapper.readTree(createResult.getResponse().getContentAsString())
                .get("data").asLong();

        // Update sheet with cell values
        CellUpdate cellUpdate1 = new CellUpdate();
        cellUpdate1.setRowNumber(1);
        cellUpdate1.setColumnNumber(1);
        cellUpdate1.setCellType(CellType.VALUE);
        cellUpdate1.setValue("10");
        
        CellUpdate cellUpdate2 = new CellUpdate();
        cellUpdate2.setRowNumber(1);
        cellUpdate2.setColumnNumber(2);
        cellUpdate2.setCellType(CellType.VALUE);
        cellUpdate2.setValue("20");
        
        CellUpdate cellUpdate3 = new CellUpdate();
        cellUpdate3.setRowNumber(1);
        cellUpdate3.setColumnNumber(3);
        cellUpdate3.setCellType(CellType.EXPRESSION);
        cellUpdate3.setExpression("=A1+B1");
        
        UpdateSheetRequest updateRequest = new UpdateSheetRequest();
        updateRequest.setCells(Arrays.asList(cellUpdate1, cellUpdate2, cellUpdate3));

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
        CreateSheetRequest createRequest = new CreateSheetRequest();
        createRequest.setName("Test Sheet");
        createRequest.setUserId("testUser");
        
        MvcResult createResult = mockMvc.perform(post("/v1/sheet")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        Long sheetId = objectMapper.readTree(createResult.getResponse().getContentAsString())
                .get("data").asLong();

        // Update sheet with complex expressions
        CellUpdate cellUpdate1 = new CellUpdate();
        cellUpdate1.setRowNumber(1);
        cellUpdate1.setColumnNumber(1);
        cellUpdate1.setCellType(CellType.VALUE);
        cellUpdate1.setValue("5");
        
        CellUpdate cellUpdate2 = new CellUpdate();
        cellUpdate2.setRowNumber(2);
        cellUpdate2.setColumnNumber(1);
        cellUpdate2.setCellType(CellType.VALUE);
        cellUpdate2.setValue("10");
        
        CellUpdate cellUpdate3 = new CellUpdate();
        cellUpdate3.setRowNumber(3);
        cellUpdate3.setColumnNumber(1);
        cellUpdate3.setCellType(CellType.VALUE);
        cellUpdate3.setValue("15");
        
        CellUpdate cellUpdate4 = new CellUpdate();
        cellUpdate4.setRowNumber(4);
        cellUpdate4.setColumnNumber(1);
        cellUpdate4.setCellType(CellType.EXPRESSION);
        cellUpdate4.setExpression("=SUM(A1:A3)");
        
        CellUpdate cellUpdate5 = new CellUpdate();
        cellUpdate5.setRowNumber(5);
        cellUpdate5.setColumnNumber(1);
        cellUpdate5.setCellType(CellType.EXPRESSION);
        cellUpdate5.setExpression("=AVG(A1:A3)");
        
        UpdateSheetRequest updateRequest = new UpdateSheetRequest();
        updateRequest.setCells(Arrays.asList(cellUpdate1, cellUpdate2, cellUpdate3, cellUpdate4, cellUpdate5));

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
        CreateSheetRequest invalidRequest = new CreateSheetRequest();
        invalidRequest.setName("Test");
        invalidRequest.setUserId("");
        
        mockMvc.perform(post("/v1/sheet")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateNonExistentSheet() throws Exception {
        CellUpdate cellUpdate = new CellUpdate();
        cellUpdate.setRowNumber(1);
        cellUpdate.setColumnNumber(1);
        cellUpdate.setCellType(CellType.VALUE);
        cellUpdate.setValue("10");
        
        UpdateSheetRequest updateRequest = new UpdateSheetRequest();
        updateRequest.setCells(Arrays.asList(cellUpdate));

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
        CreateSheetRequest createRequest = new CreateSheetRequest();
        createRequest.setName("Test Sheet");
        createRequest.setUserId("testUser");
        
        MvcResult createResult = mockMvc.perform(post("/v1/sheet")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        Long sheetId = objectMapper.readTree(createResult.getResponse().getContentAsString())
                .get("data").asLong();

        // Test with invalid row number
        CellUpdate invalidCellUpdate = new CellUpdate();
        invalidCellUpdate.setRowNumber(0);
        invalidCellUpdate.setColumnNumber(1);
        invalidCellUpdate.setCellType(CellType.VALUE);
        invalidCellUpdate.setValue("10");
        
        UpdateSheetRequest updateRequest = new UpdateSheetRequest();
        updateRequest.setCells(Arrays.asList(invalidCellUpdate));

        mockMvc.perform(put("/v1/sheet/{id}", sheetId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest());
    }
}
