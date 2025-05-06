package com.collegegraduate.scientificcalculator;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.text.DecimalFormat;
import java.util.Stack;

//updated

public class MainActivity extends AppCompatActivity {
    private TextView displayText, historyText;
    private String currentInput = "";
    private String currentExpression = "";
    private double memoryValue = 0;
    private boolean isRadianMode = true;
    private DecimalFormat formatter = new DecimalFormat("#.##########");
    private boolean isSecondMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        displayText = findViewById(R.id.display_text);
        historyText = findViewById(R.id.history_text);

        // Initialize display
        displayText.setText("0");

        setupNumberButtons();
        setupOperationButtons();
        setupFunctionButtons();
    }

    private void setupNumberButtons() {
        int[] numberIds = {
                R.id.btn_0, R.id.btn_1, R.id.btn_2, R.id.btn_3, R.id.btn_4,
                R.id.btn_5, R.id.btn_6, R.id.btn_7, R.id.btn_8, R.id.btn_9,
                R.id.btn_decimal
        };

        View.OnClickListener numberListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button button = (Button) v;
                String digit = button.getText().toString();

                // Handle decimal point special case
                if (digit.equals(".") && currentInput.contains(".")) {
                    return;
                }

                appendToInput(digit);
            }
        };

        for (int id : numberIds) {
            findViewById(id).setOnClickListener(numberListener);
        }
    }

    private void setupOperationButtons() {
        int[] opIds = {
                R.id.btn_add, R.id.btn_subtract, R.id.btn_multiply, R.id.btn_divide,
                R.id.btn_left_paren, R.id.btn_right_paren, R.id.btn_equals
        };

        View.OnClickListener opListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button button = (Button) v;
                String operation = button.getText().toString();

                switch (operation) {
                    case "=":
                        calculateResult();
                        break;
                    case "(":
                    case ")":
                        appendToExpression(operation);
                        break;
                    default:
                        if (!currentInput.isEmpty()) {
                            appendToExpression(currentInput);
                            currentInput = "";
                        }
                        appendToExpression(operation);
                        break;
                }
            }
        };

        for (int id : opIds) {
            findViewById(id).setOnClickListener(opListener);
        }
    }

    private void setupFunctionButtons() {
        // Clear button
        findViewById(R.id.btn_clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentInput = "";
                currentExpression = "";
                displayText.setText("0");
            }
        });

        // Delete button
        findViewById(R.id.btn_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!currentInput.isEmpty()) {
                    currentInput = currentInput.substring(0, currentInput.length() - 1);
                    updateDisplay();
                } else if (!currentExpression.isEmpty()) {
                    currentExpression = currentExpression.substring(0, currentExpression.length() - 1);
                    updateDisplay();
                }
            }
        });

        // Scientific functions
        findViewById(R.id.btn_sin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyTrigFunction("sin");
            }
        });

        findViewById(R.id.btn_cos).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyTrigFunction("cos");
            }
        });

        findViewById(R.id.btn_tan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyTrigFunction("tan");
            }
        });

        findViewById(R.id.btn_ln).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyFunction("ln");
            }
        });

        findViewById(R.id.btn_log).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyFunction("log");
            }
        });

        findViewById(R.id.btn_sqrt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyFunction("sqrt");
            }
        });

        findViewById(R.id.btn_power).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!currentInput.isEmpty()) {
                    appendToExpression(currentInput);
                    currentInput = "";
                }
                appendToExpression("^");
            }
        });

        findViewById(R.id.btn_pi).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentInput = String.valueOf(Math.PI);
                updateDisplay();
            }
        });

        findViewById(R.id.btn_e).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentInput = String.valueOf(Math.E);
                updateDisplay();
            }
        });

        findViewById(R.id.btn_rad_deg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRadianMode = !isRadianMode;
                Button btn = (Button) v;
                btn.setText(isRadianMode ? "RAD" : "DEG");
                Toast.makeText(MainActivity.this,
                        isRadianMode ? "Radian mode" : "Degree mode",
                        Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.btn_second).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSecondMode = !isSecondMode;
                updateFunctionButtons();
            }
        });

        findViewById(R.id.btn_factorial).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!currentInput.isEmpty()) {
                    try {
                        int num = Integer.parseInt(currentInput);
                        if (num < 0) {
                            Toast.makeText(MainActivity.this, "Cannot calculate factorial of negative number", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        currentInput = String.valueOf(factorial(num));
                        updateDisplay();
                    } catch (NumberFormatException e) {
                        Toast.makeText(MainActivity.this, "Invalid input for factorial", Toast.LENGTH_SHORT).show();
                    } catch (ArithmeticException e) {
                        Toast.makeText(MainActivity.this, "Factorial too large", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // Memory functions
        findViewById(R.id.btn_mc).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                memoryValue = 0;
                Toast.makeText(MainActivity.this, "Memory cleared", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.btn_mr).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentInput = String.valueOf(memoryValue);
                updateDisplay();
            }
        });

        findViewById(R.id.btn_m_plus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!currentInput.isEmpty()) {
                    try {
                        double value = Double.parseDouble(currentInput);
                        memoryValue += value;
                        Toast.makeText(MainActivity.this, "Added to memory", Toast.LENGTH_SHORT).show();
                    } catch (NumberFormatException e) {
                        Toast.makeText(MainActivity.this, "Invalid input", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        findViewById(R.id.btn_m_minus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!currentInput.isEmpty()) {
                    try {
                        double value = Double.parseDouble(currentInput);
                        memoryValue -= value;
                        Toast.makeText(MainActivity.this, "Subtracted from memory", Toast.LENGTH_SHORT).show();
                    } catch (NumberFormatException e) {
                        Toast.makeText(MainActivity.this, "Invalid input", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void updateFunctionButtons() {
        Button sinButton = findViewById(R.id.btn_sin);
        Button cosButton = findViewById(R.id.btn_cos);
        Button tanButton = findViewById(R.id.btn_tan);
        Button lnButton = findViewById(R.id.btn_ln);
        Button logButton = findViewById(R.id.btn_log);

        if (isSecondMode) {
            sinButton.setText("asin");
            cosButton.setText("acos");
            tanButton.setText("atan");
            lnButton.setText("eˣ");
            logButton.setText("10ˣ");
        } else {
            sinButton.setText("sin");
            cosButton.setText("cos");
            tanButton.setText("tan");
            lnButton.setText("ln");
            logButton.setText("log");
        }
    }

    private void applyTrigFunction(String func) {
        if (!currentInput.isEmpty()) {
            try {
                double value = Double.parseDouble(currentInput);
                double result = 0;

                // Convert to radians if in degree mode
                if (!isRadianMode && !isSecondMode) {
                    value = Math.toRadians(value);
                }

                if (isSecondMode) {
                    // Inverse trig functions
                    switch (func) {
                        case "sin":
                            result = Math.asin(value);
                            break;
                        case "cos":
                            result = Math.acos(value);
                            break;
                        case "tan":
                            result = Math.atan(value);
                            break;
                    }

                    // Convert to degrees if in degree mode
                    if (!isRadianMode) {
                        result = Math.toDegrees(result);
                    }
                } else {
                    // Regular trig functions
                    switch (func) {
                        case "sin":
                            result = Math.sin(value);
                            break;
                        case "cos":
                            result = Math.cos(value);
                            break;
                        case "tan":
                            result = Math.tan(value);
                            break;
                    }
                }

                currentInput = formatter.format(result);
                updateDisplay();
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid input", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void applyFunction(String func) {
        if (!currentInput.isEmpty()) {
            try {
                double value = Double.parseDouble(currentInput);
                double result = 0;

                if (isSecondMode) {
                    switch (func) {
                        case "ln":
                            result = Math.exp(value);
                            break;
                        case "log":
                            result = Math.pow(10, value);
                            break;
                    }
                } else {
                    switch (func) {
                        case "ln":
                            if (value <= 0) {
                                Toast.makeText(this, "Cannot calculate ln of non-positive number", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            result = Math.log(value);
                            break;
                        case "log":
                            if (value <= 0) {
                                Toast.makeText(this, "Cannot calculate log of non-positive number", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            result = Math.log10(value);
                            break;
                        case "sqrt":
                            if (value < 0) {
                                Toast.makeText(this, "Cannot calculate square root of negative number", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            result = Math.sqrt(value);
                            break;
                    }
                }

                currentInput = formatter.format(result);
                updateDisplay();
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid input", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private long factorial(int n) {
        if (n > 20) {
            throw new ArithmeticException("Factorial too large");
        }
        long result = 1;
        for (int i = 2; i <= n; i++) {
            result *= i;
        }
        return result;
    }

    private void appendToInput(String value) {
        currentInput += value;
        updateDisplay();
    }

    private void appendToExpression(String value) {
        currentExpression += value;
        updateDisplay();
    }

    private void updateDisplay() {
        String displayValue = currentInput.isEmpty() ? currentExpression : currentExpression + currentInput;
        if (displayValue.isEmpty()) {
            displayValue = "0";
        }
        displayText.setText(displayValue);
    }

    private void calculateResult() {
        if (!currentInput.isEmpty()) {
            currentExpression += currentInput;
            currentInput = "";
        }

        if (currentExpression.isEmpty()) {
            return;
        }

        try {
            // Save the expression to history
            String expressionToEvaluate = currentExpression;
            historyText.setText(expressionToEvaluate);

            // Simple expression evaluator
            double result = evaluateExpression(expressionToEvaluate);
            currentInput = formatter.format(result);
            currentExpression = "";
            updateDisplay();
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // Basic expression evaluator
    private double evaluateExpression(String expression) {
        // Replace ^ with Math.pow function
        expression = expression.replace("^", "#");

        // Process operators with precedence
        return parseExpression(expression);
    }

    private int findMatchingParenthesis(String expression, int start) {
        int count = 1;
        for (int i = start + 1; i < expression.length(); i++) {
            if (expression.charAt(i) == '(') {
                count++;
            } else if (expression.charAt(i) == ')') {
                count--;
                if (count == 0) {
                    return i;
                }
            }
        }
        throw new IllegalArgumentException("Mismatched parentheses");
    }

    private double parseExpression(String expression) {
        Stack<Double> values = new Stack<>();
        Stack<Character> operators = new Stack<>();

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
                int closingIndex = findMatchingParenthesis(expression, i);
                double result = parseExpression(expression.substring(i + 1, closingIndex));
                values.push(result);
                i = closingIndex;
            } else if (c == '+' || c == '-' || c == '*' || c == '/' || c == '#') {
                while (!operators.empty() && hasPrecedence(c, operators.peek())) {
                    values.push(applyOperation(operators.pop(), values.pop(), values.pop()));
                }
                operators.push(c);
            }
        }

        while (!operators.empty()) {
            values.push(applyOperation(operators.pop(), values.pop(), values.pop()));
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

    private double applyOperation(char op, double b, double a) {
        switch (op) {
            case '+': return a + b;
            case '-': return a - b;
            case '*': return a * b;
            case '/':
                if (b == 0) throw new ArithmeticException("Division by zero");
                return a / b;
            case '#': return Math.pow(a, b);
        }
        return 0;
    }
}