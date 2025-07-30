package com.spreadsheet.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
public class CellEvaluator {
    
    private static final Pattern CELL_REFERENCE_PATTERN = Pattern.compile("([A-Z]+)(\\d+)");
    private static final Pattern ARITHMETIC_PATTERN = Pattern.compile("^[0-9+\\-*/().\\s]+$");
    
    /**
     * Evaluates a cell expression and returns the result
     * @param expression The expression to evaluate
     * @param sheetData Map of cell references to their values
     * @return The evaluated result as a string
     */
    public String evaluateExpression(String expression, Map<String, String> sheetData) {
        if (expression == null || expression.trim().isEmpty()) {
            return "";
        }
        
        try {
            log.debug("Evaluating expression: {}", expression);
            
            // Remove leading = if present
            String cleanExpression = expression.trim();
            if (cleanExpression.startsWith("=")) {
                cleanExpression = cleanExpression.substring(1);
            }
            
            // Replace cell references with their values
            String resolvedExpression = resolveCellReferences(cleanExpression, sheetData);
            log.debug("Resolved expression: {}", resolvedExpression);
            
            // Handle built-in functions
            if (resolvedExpression.toUpperCase().startsWith("SUM(")) {
                return evaluateSum(resolvedExpression, sheetData);
            } else if (resolvedExpression.toUpperCase().startsWith("AVG(")) {
                return evaluateAverage(resolvedExpression, sheetData);
            } else if (resolvedExpression.toUpperCase().startsWith("COUNT(")) {
                return evaluateCount(resolvedExpression, sheetData);
            }
            
            // Evaluate arithmetic expression
            if (ARITHMETIC_PATTERN.matcher(resolvedExpression).matches()) {
                return String.valueOf(evaluateArithmetic(resolvedExpression));
            }
            
            // If it's just a number, return it
            try {
                Double.parseDouble(resolvedExpression);
                return resolvedExpression;
            } catch (NumberFormatException e) {
                // Not a number, return as string
                return resolvedExpression;
            }
            
        } catch (Exception e) {
            log.error("Error evaluating expression: {}", expression, e);
            return "#ERROR";
        }
    }
    
    private String resolveCellReferences(String expression, Map<String, String> sheetData) {
        Matcher matcher = CELL_REFERENCE_PATTERN.matcher(expression);
        StringBuffer result = new StringBuffer();
        
        while (matcher.find()) {
            String cellRef = matcher.group();
            String cellValue = sheetData.getOrDefault(cellRef, "0");
            
            // If cell value is not a number, treat as 0 for calculations
            try {
                Double.parseDouble(cellValue);
                matcher.appendReplacement(result, cellValue);
            } catch (NumberFormatException e) {
                matcher.appendReplacement(result, "0");
            }
        }
        matcher.appendTail(result);
        
        return result.toString();
    }
    
    private String evaluateSum(String expression, Map<String, String> sheetData) {
        // Extract range from SUM(A1:A5) or SUM(A1,B1,C1)
        String content = expression.substring(4, expression.length() - 1); // Remove SUM( and )
        
        if (content.contains(":")) {
            // Range like A1:A5
            return evaluateRangeSum(content, sheetData);
        } else {
            // Individual cells like A1,B1,C1
            return evaluateIndividualSum(content, sheetData);
        }
    }
    
    private String evaluateRangeSum(String range, Map<String, String> sheetData) {
        String[] parts = range.split(":");
        if (parts.length != 2) {
            return "#ERROR";
        }
        
        // For simplicity, assume same column range like A1:A5
        Matcher startMatcher = CELL_REFERENCE_PATTERN.matcher(parts[0].trim());
        Matcher endMatcher = CELL_REFERENCE_PATTERN.matcher(parts[1].trim());
        
        if (!startMatcher.matches() || !endMatcher.matches()) {
            return "#ERROR";
        }
        
        String column = startMatcher.group(1);
        int startRow = Integer.parseInt(startMatcher.group(2));
        int endRow = Integer.parseInt(endMatcher.group(2));
        
        double sum = 0;
        for (int row = startRow; row <= endRow; row++) {
            String cellRef = column + row;
            String cellValue = sheetData.getOrDefault(cellRef, "0");
            try {
                sum += Double.parseDouble(cellValue);
            } catch (NumberFormatException e) {
                // Skip non-numeric values
            }
        }
        
        return String.valueOf(sum);
    }
    
    private String evaluateIndividualSum(String cells, Map<String, String> sheetData) {
        String[] cellRefs = cells.split(",");
        double sum = 0;
        
        for (String cellRef : cellRefs) {
            String cellValue = sheetData.getOrDefault(cellRef.trim(), "0");
            try {
                sum += Double.parseDouble(cellValue);
            } catch (NumberFormatException e) {
                // Skip non-numeric values
            }
        }
        
        return String.valueOf(sum);
    }
    
    private String evaluateAverage(String expression, Map<String, String> sheetData) {
        String sumResult = evaluateSum(expression.replace("AVG", "SUM"), sheetData);
        if ("#ERROR".equals(sumResult)) {
            return "#ERROR";
        }
        
        // Count non-empty cells for average calculation
        String content = expression.substring(4, expression.length() - 1);
        int count = countNonEmptyCells(content, sheetData);
        
        if (count == 0) {
            return "0";
        }
        
        double sum = Double.parseDouble(sumResult);
        return String.valueOf(sum / count);
    }
    
    private String evaluateCount(String expression, Map<String, String> sheetData) {
        String content = expression.substring(6, expression.length() - 1); // Remove COUNT( and )
        int count = countNonEmptyCells(content, sheetData);
        return String.valueOf(count);
    }
    
    private int countNonEmptyCells(String content, Map<String, String> sheetData) {
        if (content.contains(":")) {
            // Range like A1:A5
            String[] parts = content.split(":");
            if (parts.length != 2) {
                return 0;
            }
            
            Matcher startMatcher = CELL_REFERENCE_PATTERN.matcher(parts[0].trim());
            Matcher endMatcher = CELL_REFERENCE_PATTERN.matcher(parts[1].trim());
            
            if (!startMatcher.matches() || !endMatcher.matches()) {
                return 0;
            }
            
            String column = startMatcher.group(1);
            int startRow = Integer.parseInt(startMatcher.group(2));
            int endRow = Integer.parseInt(endMatcher.group(2));
            
            int count = 0;
            for (int row = startRow; row <= endRow; row++) {
                String cellRef = column + row;
                String cellValue = sheetData.getOrDefault(cellRef, "");
                if (!cellValue.trim().isEmpty()) {
                    count++;
                }
            }
            return count;
        } else {
            // Individual cells
            String[] cellRefs = content.split(",");
            int count = 0;
            for (String cellRef : cellRefs) {
                String cellValue = sheetData.getOrDefault(cellRef.trim(), "");
                if (!cellValue.trim().isEmpty()) {
                    count++;
                }
            }
            return count;
        }
    }
    
    private double evaluateArithmetic(String expression) {
        // Simple arithmetic evaluation using recursive descent parser
        return new ArithmeticEvaluator(expression).evaluate();
    }
    
    private static class ArithmeticEvaluator {
        private final String expression;
        private int pos = 0;
        
        public ArithmeticEvaluator(String expression) {
            this.expression = expression.replaceAll("\\s", "");
        }
        
        public double evaluate() {
            double result = parseExpression();
            if (pos < expression.length()) {
                throw new RuntimeException("Unexpected character: " + expression.charAt(pos));
            }
            return result;
        }
        
        private double parseExpression() {
            double result = parseTerm();
            
            while (pos < expression.length()) {
                char op = expression.charAt(pos);
                if (op == '+' || op == '-') {
                    pos++;
                    double term = parseTerm();
                    result = (op == '+') ? result + term : result - term;
                } else {
                    break;
                }
            }
            
            return result;
        }
        
        private double parseTerm() {
            double result = parseFactor();
            
            while (pos < expression.length()) {
                char op = expression.charAt(pos);
                if (op == '*' || op == '/') {
                    pos++;
                    double factor = parseFactor();
                    result = (op == '*') ? result * factor : result / factor;
                } else {
                    break;
                }
            }
            
            return result;
        }
        
        private double parseFactor() {
            if (pos < expression.length() && expression.charAt(pos) == '(') {
                pos++; // Skip '('
                double result = parseExpression();
                if (pos >= expression.length() || expression.charAt(pos) != ')') {
                    throw new RuntimeException("Missing closing parenthesis");
                }
                pos++; // Skip ')'
                return result;
            }
            
            return parseNumber();
        }
        
        private double parseNumber() {
            int start = pos;
            if (pos < expression.length() && (expression.charAt(pos) == '+' || expression.charAt(pos) == '-')) {
                pos++;
            }
            
            while (pos < expression.length() && (Character.isDigit(expression.charAt(pos)) || expression.charAt(pos) == '.')) {
                pos++;
            }
            
            if (start == pos) {
                throw new RuntimeException("Expected number at position " + pos);
            }
            
            return Double.parseDouble(expression.substring(start, pos));
        }
    }
}
