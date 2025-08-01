package com.spreadsheet.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CellEvaluatorTest {

    private CellEvaluator cellEvaluator;

    @BeforeEach
    void setUp() {
        cellEvaluator = new CellEvaluator();
    }

    @Test
    void testEvaluateSimpleArithmetic() {
        Map<String, String> sheetData = new HashMap<>();
        
        assertEquals("10", cellEvaluator.evaluateExpression("=5+5", sheetData));
        assertEquals("15", cellEvaluator.evaluateExpression("=3*5", sheetData));
        assertEquals("2", cellEvaluator.evaluateExpression("=10/5", sheetData));
        assertEquals("3", cellEvaluator.evaluateExpression("=8-5", sheetData));
    }

    @Test
    void testEvaluateComplexArithmetic() {
        Map<String, String> sheetData = new HashMap<>();
        
        assertEquals("14", cellEvaluator.evaluateExpression("=2+3*4", sheetData));
        assertEquals("20", cellEvaluator.evaluateExpression("=(2+3)*4", sheetData));
        assertEquals("7", cellEvaluator.evaluateExpression("=2+10/2", sheetData));
    }

    @Test
    void testEvaluateCellReferences() {
        Map<String, String> sheetData = new HashMap<>();
        sheetData.put("A1", "10");
        sheetData.put("B1", "20");
        
        assertEquals("30", cellEvaluator.evaluateExpression("=A1+B1", sheetData));
        assertEquals("200", cellEvaluator.evaluateExpression("=A1*B1", sheetData));
        assertEquals("2", cellEvaluator.evaluateExpression("=B1/A1", sheetData));
    }

    @Test
    void testEvaluateSumFunction() {
        Map<String, String> sheetData = new HashMap<>();
        sheetData.put("A1", "10");
        sheetData.put("A2", "20");
        sheetData.put("A3", "30");
        sheetData.put("A4", "40");
        sheetData.put("A5", "50");
        
        assertEquals("150.0", cellEvaluator.evaluateExpression("=SUM(A1:A5)", sheetData));
        assertEquals("60.0", cellEvaluator.evaluateExpression("=SUM(A1,A2,A3)", sheetData));
    }

    @Test
    void testEvaluateAverageFunction() {
        Map<String, String> sheetData = new HashMap<>();
        sheetData.put("A1", "10");
        sheetData.put("A2", "20");
        sheetData.put("A3", "30");
        
        assertEquals("20.0", cellEvaluator.evaluateExpression("=AVG(A1:A3)", sheetData));
    }

    @Test
    void testEvaluateCountFunction() {
        Map<String, String> sheetData = new HashMap<>();
        sheetData.put("A1", "10");
        sheetData.put("A2", "20");
        sheetData.put("A3", "");
        sheetData.put("A4", "40");
        
        assertEquals("3", cellEvaluator.evaluateExpression("=COUNT(A1:A4)", sheetData));
    }

    @Test
    void testEvaluateWithMissingCellReferences() {
        Map<String, String> sheetData = new HashMap<>();
        sheetData.put("A1", "10");
        
        // Missing cell B1 should be treated as 0
        assertEquals("10", cellEvaluator.evaluateExpression("=A1+B1", sheetData));
    }

    @Test
    void testEvaluateWithNonNumericCellReferences() {
        Map<String, String> sheetData = new HashMap<>();
        sheetData.put("A1", "10");
        sheetData.put("B1", "text");
        
        // Non-numeric cell B1 should be treated as 0
        assertEquals("10", cellEvaluator.evaluateExpression("=A1+B1", sheetData));
    }

    @Test
    void testEvaluateSimpleValue() {
        Map<String, String> sheetData = new HashMap<>();
        
        assertEquals("42", cellEvaluator.evaluateExpression("42", sheetData));
        assertEquals("hello", cellEvaluator.evaluateExpression("hello", sheetData));
    }

    @Test
    void testEvaluateEmptyExpression() {
        Map<String, String> sheetData = new HashMap<>();
        
        assertEquals("", cellEvaluator.evaluateExpression("", sheetData));
        assertEquals("", cellEvaluator.evaluateExpression(null, sheetData));
    }

    @Test
    void testEvaluateInvalidExpression() {
        Map<String, String> sheetData = new HashMap<>();
        
        assertEquals("#ERROR", cellEvaluator.evaluateExpression("=1/0", sheetData));
        assertEquals("#ERROR", cellEvaluator.evaluateExpression("=SUM(INVALID)", sheetData));
    }

    @Test
    void testEvaluateWithWhitespace() {
        Map<String, String> sheetData = new HashMap<>();
        
        assertEquals("10", cellEvaluator.evaluateExpression("= 5 + 5 ", sheetData));
        assertEquals("15", cellEvaluator.evaluateExpression("=  3 * 5  ", sheetData));
    }

    @Test
    void testEvaluateNestedExpressions() {
        Map<String, String> sheetData = new HashMap<>();
        sheetData.put("A1", "5");
        sheetData.put("B1", "3");
        sheetData.put("C1", "2");
        
        assertEquals("11", cellEvaluator.evaluateExpression("=A1+B1*C1", sheetData));
        assertEquals("16", cellEvaluator.evaluateExpression("=(A1+B1)*C1", sheetData));
    }

    @Test
    void testEvaluateSumWithEmptyCells() {
        Map<String, String> sheetData = new HashMap<>();
        sheetData.put("A1", "10");
        sheetData.put("A2", "");
        sheetData.put("A3", "20");
        
        assertEquals("30.0", cellEvaluator.evaluateExpression("=SUM(A1:A3)", sheetData));
    }

    @Test
    void testEvaluateCountWithEmptyCells() {
        Map<String, String> sheetData = new HashMap<>();
        sheetData.put("A1", "10");
        sheetData.put("A2", "");
        sheetData.put("A3", "20");
        
        assertEquals("2", cellEvaluator.evaluateExpression("=COUNT(A1:A3)", sheetData));
    }
}
