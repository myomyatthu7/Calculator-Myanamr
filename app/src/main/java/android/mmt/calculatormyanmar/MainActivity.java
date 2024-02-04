package android.mmt.calculatormyanmar;


import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.util.EmptyStackException;
import java.util.Stack;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText editText;
    private final StringBuilder currentInput = new StringBuilder();
    private boolean isNewInput = true;
    private String text;
    private TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.editText);
        tvResult = findViewById(R.id.tvResult);
        setButtonClickListeners();

        // AC button
        Button acButton = findViewById(R.id.btnAC);
        acButton.setOnClickListener(v -> onACClick());

        // DEL button
        Button delButton = findViewById(R.id.btnDEL);
        delButton.setOnClickListener(v -> onDELClick());

        // Equals button
        Button equalsButton = findViewById(R.id.btnEquals);
        equalsButton.setOnClickListener(v -> {
            text = editText.getText().toString();
            if (!text.isEmpty()) {
                onEqualsClick();
            } else {
                Toast.makeText(MainActivity.this, "Enter Number", Toast.LENGTH_SHORT).show();
            }
        });

        editText.setFocusable(false);
        editText.setFocusableInTouchMode(false);
        editText.setCursorVisible(false);
    }

    private void onACClick() {
        currentInput.setLength(0);
        updateEditText();
    }

    private void onDELClick() {
        removeLastWordOrCharacter();
    }
    public void removeLastWordOrCharacter() {
        int length = currentInput.length();
        if (length > 0) {
            if (Character.isWhitespace(currentInput.charAt(length - 1))) {
                // Remove the entire word (including spaces)
                int startIndex = length - 1;
                while (startIndex >= 0 && Character.isWhitespace(currentInput.charAt(startIndex))) {
                    startIndex--;
                }
                currentInput.delete(startIndex - 1, length);
            } else {
                // Remove the last character
                currentInput.deleteCharAt(length - 1);
            }
            updateEditText();
        }
    }

    private void setButtonClickListeners() {
        int[] buttonIds = {
                R.id.btnZero, R.id.btnOne, R.id.btnTwo, R.id.btnThree,
                R.id.btnFour, R.id.btnFive, R.id.btnSix, R.id.btnSeven,
                R.id.btnEight, R.id.btnNine, R.id.btnDecimal, R.id.btnZeroZero
        };

        for (int buttonId : buttonIds) {
            Button button = findViewById(buttonId);
            button.setOnClickListener(v -> onDigitClick(((Button) v).getText().toString()));
        }

        int[] operatorButtonIds = {
                R.id.btnAdd, R.id.btnSubtract, R.id.btnMultiply, R.id.btnDivide
        };

        for (int buttonId : operatorButtonIds) {
            Button button = findViewById(buttonId);
            button.setOnClickListener(v -> onOperatorClick(((Button) v).getText().toString()));
        }
    }

    private void onDigitClick(String digit) {
        currentInput.append(digit);
        updateEditText();
        isNewInput = false;
        //Toast.makeText(this, "onDigitClick", Toast.LENGTH_SHORT).show();
    }

    private void onOperatorClick(String selectedOperator) {
        if (!isNewInput) {
            currentInput.append(" ").append(selectedOperator).append(" ");
            updateEditText();
            isNewInput = true;
            //Toast.makeText(this, "onOperatorClick", Toast.LENGTH_SHORT).show();
        }
    }

    private void onEqualsClick() {
        text = editText.getText().toString();
//        boolean lastCharacter = text.endsWith("+") || text.endsWith("-") || text.endsWith("*")
//                || text.endsWith("/") || text.endsWith(" ");

                //Toast.makeText(this, String.valueOf(lastCharacter), Toast.LENGTH_SHORT).show();
            try {
                if (text.endsWith(" ")) {
                //if (Character.isWhitespace(currentInput.charAt(currentInput.length()-1))) {
                    currentInput.delete(currentInput.length()-2,currentInput.length());
                    //Toast.makeText(this, "space", Toast.LENGTH_SHORT).show();
                }

//                // Check if the last character is an operator
//                String lastCharacter = currentInput.toString().replaceAll("\\s+", "");
//                if (!lastCharacter.matches("[+-]?\\d*\\.?\\d+")) {
//                    // Remove the last character if it's an operator
//                    currentInput.deleteCharAt(currentInput.length() - 1);
//                }

                //Toast.makeText(this, editText.getText().toString(), Toast.LENGTH_SHORT).show();
                double result = evaluateExpression(currentInput.toString());
                //String expression = currentInput.toString() + "\n = " + result;
                //editText.setText(String.valueOf(expression));

                int numberOfDecimalPlaces = 4;
                java.text.DecimalFormat decimalFormat = new java.text.DecimalFormat();
                decimalFormat.setMaximumFractionDigits(numberOfDecimalPlaces);
                String updateResult = decimalFormat.format(result);
                tvResult.setText(updateResult);
                isNewInput = true;
                currentInput.setLength(0);
            } catch (EmptyStackException e) {
                // Handle invalid input (e.g., if the input cannot be parsed to a double)
                //editText.setText("EmptyStack");
                //Toast.makeText(this, editText.getText().toString(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            } catch (NumberFormatException e) {
                // Handle invalid input (e.g., if the input cannot be parsed to a double)
                //editText.setText("Error");
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            } catch (ArithmeticException e) {
                // Handle division by zero
                //editText.setText("Error: Division by zero");
                Toast.makeText(this, "Error: Division by zero", Toast.LENGTH_SHORT).show();
            }
    }

//    private void onEqualsClick() {
//        if (!isNewInput) {
//            try {
//                // Check if the last character is an operator
//                String lastCharacter = currentInput.toString().replaceAll("\\s+", "");
//                if (!lastCharacter.matches("[+-]?\\d*\\.?\\d+")) {
//                    // Remove the last character if it's an operator
//                    currentInput.deleteCharAt(currentInput.length() - 1);
//                }
//
//                double result = evaluateExpression(currentInput.toString());
//                String expression = currentInput.toString() + " = " + result;
//                editText.setText(String.valueOf(expression));
//                isNewInput = true;
//                currentInput.setLength(0);
//            } catch (NumberFormatException e) {
//                // Handle invalid input (e.g., if the input cannot be parsed to a double)
//                editText.setText("Error: Invalid input");
//            } catch (ArithmeticException e) {
//                // Handle division by zero
//                editText.setText("Error: Division by zero");
//            }
//        }
//    }

    private double evaluateExpression(String expression) {
        String[] tokens = expression.split(" ");
        Stack<Double> values = new Stack<>();
        Stack<String> operators = new Stack<>();

        for (String token : tokens) {
            if (token.matches("[+-]?\\d*\\.?\\d+")) {
                values.push(Double.parseDouble(token));
            } else if (token.equals("+") || token.equals("-") || token.equals("*") || token.equals("/")) {
                while (!operators.isEmpty() && hasPrecedence(token, operators.peek())) {
                    applyOperation(values, operators.pop());
                }
                operators.push(token);
            }
        }

        while (!operators.isEmpty()) {
            applyOperation(values, operators.pop());
        }
        return values.pop();
    }

    private void applyOperation(Stack<Double> values, String operator) {
        double b = values.pop();
        double a = values.pop();

        switch (operator) {
            case "+":
                values.push(a + b);
                break;
            case "-":
                values.push(a - b);
                break;
            case "*":
                values.push(a * b);
                break;
            case "/":
                if (b != 0) {
                    values.push(a / b);
                } else {
                    // Handle division by zero
                    throw new ArithmeticException("Division by zero");
                }
                break;
        }
    }

    private void updateEditText() {
        editText.setText(currentInput.toString());
    }

    private boolean hasPrecedence(String op1, String op2) {
        return (op2.equals("+") || op2.equals("-")) && (op1.equals("*") || op1.equals("/"));
    }
}
