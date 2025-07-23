package com.spark.calculator;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.TextView;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;


import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {

    TextView textViewExpression, textViewResult;
    StringBuilder expression = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewExpression = findViewById(R.id.textViewExpression);
        textViewResult = findViewById(R.id.textViewResult);



        int[] numberBtnIds = {
                R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
                R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9,
                R.id.btn00, R.id.btnDot
        };

        int[] operatorBtnIds = {
                R.id.btnAdd, R.id.btnSubtract, R.id.btnMultiply,
                R.id.btnDivide, R.id.btnPercent
        };

        View.OnClickListener numberClickListener = v -> {
            Button btn = (Button) v;
            expression.append(btn.getText());
            textViewExpression.setText(expression.toString());
        };

        View.OnClickListener operatorClickListener = v -> {
            Button btn = (Button) v;
            expression.append(" " + btn.getText() + " ");
            textViewExpression.setText(expression.toString());
        };

        for (int id : numberBtnIds) {
            findViewById(id).setOnClickListener(numberClickListener);
        }

        for (int id : operatorBtnIds) {
            findViewById(id).setOnClickListener(operatorClickListener);
        }

        findViewById(R.id.btnAC).setOnClickListener(v -> {
            expression.setLength(0);
            textViewExpression.setText("");
            textViewResult.setText("");
        });

        findViewById(R.id.btnDel).setOnClickListener(v -> {
            if (expression.length() > 0) {
                expression.deleteCharAt(expression.length() - 1);
                textViewExpression.setText(expression.toString());
            }
        });

        findViewById(R.id.btnEquals).setOnClickListener(v -> evaluateExpression());
    }
    private void applyButtonClickAnimation(Button button) {
        ScaleAnimation scaleAnimation = new ScaleAnimation(
                1.0f, 0.95f, 1.0f, 0.95f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(100);
        scaleAnimation.setRepeatCount(1);
        scaleAnimation.setRepeatMode(Animation.REVERSE);
        button.startAnimation(scaleAnimation);
    }


    private void evaluateExpression() {
        try {
            String exp = expression.toString()
                    .replace("×", "*")
                    .replace("÷", "/");

            // Handle percentage manually
            exp = handlePercentage(exp);

            Expression e = new ExpressionBuilder(exp).build();
            double result = e.evaluate();

            if (result == (long) result) {
                textViewResult.setText(String.valueOf((long) result));
            } else {
                textViewResult.setText(String.valueOf(result));
            }

        } catch (Exception ex) {
            textViewResult.setText("Error");
        }
    }

    private String handlePercentage(String exp) {
        String[] tokens = exp.trim().split(" ");
        StringBuilder newExp = new StringBuilder();

        for (int i = 0; i < tokens.length; i++) {
            String token = tokens[i];

            if (token.equals("%") && i > 0) {
                String prev = tokens[i - 1];
                String operator = (i > 1) ? tokens[i - 2] : "";

                // Remove the last added part
                int prevLength = prev.length() + 1;
                newExp.setLength(newExp.length() - prevLength);

                if (operator.equals("+") || operator.equals("-")) {
                    // A + B% → A + (A * B / 100)
                    // Need to find A
                    StringBuilder leftVal = new StringBuilder();
                    int j = newExp.length() - 2; // Skip space
                    while (j >= 0 && (Character.isDigit(newExp.charAt(j)) || newExp.charAt(j) == '.')) {
                        leftVal.insert(0, newExp.charAt(j));
                        j--;
                    }

                    String A = leftVal.toString();
                    newExp.append("( ").append(A).append(" * ").append(prev).append(" / 100 ) ");

                } else {
                    // For × and ÷: Just convert B% → (B / 100)
                    newExp.append("( ").append(prev).append(" / 100 ) ");
                }

            } else {
                newExp.append(token).append(" ");
            }
        }

        return newExp.toString().trim();
    }
}