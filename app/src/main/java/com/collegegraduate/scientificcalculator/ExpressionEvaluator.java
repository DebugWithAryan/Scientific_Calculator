package com.collegegraduate.scientificcalculator;


import java.util.Stack;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A more robust expression evaluator for scientific calculator
 */
public class ExpressionEvaluator {

    private boolean isRadianMode;
    private Map<String, Double> variables;

    public ExpressionEvaluator(boolean isRadianMode) {
        this.isRadianMode = isRadianMode;
        this.variables = new HashMap<>();
        // Initialize constants
        variables.put("pi", Math.PI);
        variables.put("e", Math.E);
    }

    public void setRadianMode(boolean isRadianMode) {
        this.isRadianMode = isRadianMode;
    }

    public void setVariable(String name, double value) {
        variables.put(name.toLowerCase(), value);
    }

    public double evaluate(String expression) {
        // Remove all whitespace
        expression = expression.replaceAll("\\s+", "");

        // First pass: handle functions and variables
        expression = preprocessExpression(expression);

        // Second pass: Evaluate the algebraic expression
        return evaluateExpression(expression);
    }

    private String preprocessExpression(String expression) {
        // Process all functions
        String functionPattern = "(sin|cos|tan|log|ln|sqrt|asin|acos|atan)\\(([^\\(\\)]+)\\)";
        Pattern pattern = Pattern.compile(functionPattern);
        Matcher matcher = pattern.matcher(expression);

        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String funcName = matcher.group(1);
            String argument = matcher.group(2);

            // Recursively evaluate the argument
            double argValue = evaluate(argument);
            double result = applyFunction(funcName, argValue);

            matcher.appendReplacement(sb, Double.toString(result));
        }
        matcher.appendTail(sb);
        expression = sb.toString();

        // Replace all variables
        for (Map.Entry<String, Double> entry : variables.entrySet()) {
            String varName = entry.getKey();
            double varValue = entry.getValue();
            // Use word boundary to match whole tokens only
            expression = expression.replaceAll("\\b" + varName + "\\b", Double.toString(varValue));
        }

        // Handle factorial notation
        pattern = Pattern.compile("(\\d+)!");
        matcher = pattern.matcher(expression);
        sb = new StringBuffer();

        while (matcher.find()) {
            int number = Integer.parseInt(matcher.group(1));
            double factorial = factorial(number);
            matcher.appendReplacement(sb, Double.toString(factorial));
        }
        matcher.appendTail(sb);

        return sb.toString();
    }

    private double applyFunction(String funcName, double argument) {
        switch (funcName) {
            case "sin":
                if (!isRadianMode) {
                    argument = Math.toRadians(argument);
                }
                return Math.sin(argument);
            case "cos":
                if (!isRadianMode) {
                    argument = Math.toRadians(argument);
                }
                return Math.cos(argument);
            case "tan":
                if (!isRadianMode) {
                    argument = Math.toRadians(argument);
                }
                return Math.tan(argument);
            case "asin":
                double result = Math.asin(argument);
                if (!isRadianMode) {
                    result = Math.toDegrees(result);
                }
                return result;
            case "acos":
                result = Math.acos(argument);
                if (!isRadianMode) {
                    result = Math.toDegrees(result);
                }
                return result;
            case "atan":
                result = Math.atan(argument);
                if (!isRadianMode) {
                    result = Math.toDegrees(result);
                }
                return result;
            case "log":
                return Math.log10(argument);
            case "ln":
                return Math.log(argument);
            case "sqrt":
                return Math.sqrt(argument);
            default:
                throw new IllegalArgumentException("Unknown function: " + funcName);
        }
    }

    private double factorial(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Factorial not defined for negative numbers");
        }
        if (n > 170) {
            throw new ArithmeticException("Factorial too large to represent as double");
        }

        double result = 1.0;
        for (int i = 2; i <= n; i++) {
            result *= i;
        }
        return result;
    }

    private double evaluateExpression(String expression) {
        // Add parsing support for exponents (^)
        expression = expression.replace("^", "#");

        // Handle unary minus at the beginning of expression or after an operator
        expression = expression.replace("(-", "(0-");
        expression = expression.replace("(+", "(0+");
        if (expression.startsWith("-")) {
            expression = "0" + expression;
        }
        if (expression.startsWith("+")) {
            expression = "0" + expression;
        }

        // Replace unary minus after other operators
        expression = expression.replace("+-", "+0-");
        expression = expression.replace("--", "-0-");
        expression = expression.replace("*-", "*0-");
        expression = expression.replace("/-", "/0-");
        expression = expression.replace("#-", "#0-");

        // Parse the expression
        return parseExpression(expression);
    }

    private double parseExpression(String expression) {
        Stack<Double> values = new Stack<>();
        Stack<Character> ops = new Stack<>();

        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);

            if (Character.isDigit(c) || c == '.') {
                StringBuilder sb = new StringBuilder();

                while (i < expression.length() &&
                        (Character.isDigit(expression.charAt(i)) || expression.charAt(i) == '.')) {
                    sb.append(expression.charAt(i++));
                }
                i--; // Move back one position
                values.push(Double.parseDouble(sb.toString()));
            } else if (c == '(') {
                ops.push(c);
            } else if (c == ')') {
                while (ops.peek() != '(') {
                    values.push(applyOperator(ops.pop(), values));
                }
                ops.pop(); // Remove the '('
            } else if (c == '+' || c == '-' || c == '*' || c == '/' || c == '#') {
                while (!ops.isEmpty() && hasPrecedence(c, ops.peek())) {
                    values.push(applyOperator(ops.pop(), values));
                }
                ops.push(c);
            }
        }

        while (!ops.isEmpty()) {
            values.push(applyOperator(ops.pop(), values));
        }

        return values.pop();
    }

    private boolean hasPrecedence(char op1, char op2) {
        if (op2 == '(' || op2 == ')') {
            return false;
        }
        if ((op1 == '*' || op1 == '/') && (op2 == '+' || op2 == '-')) {
            return false;
        }
        if (op1 == '#' && (op2 == '+' || op2 == '-' || op2 == '*' || op2 == '/')) {
            return false;
        }
        return true;
    }

    private double applyOperator(char op, Stack<Double> values) {
        double b = values.pop();
        double a = values.pop();

        switch (op) {
            case '+': return a + b;
            case '-': return a - b;
            case '*': return a * b;
            case '/':
                if (b == 0) throw new ArithmeticException("Division by zero");
                return a / b;
            case '#': return Math.pow(a, b);
        }
        throw new IllegalArgumentException("Unknown operator: " + op);
    }
}
