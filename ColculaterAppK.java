 
package com.mycompany.colculaterappk;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ColculaterAppK  extends JFrame implements ActionListener {

    private JTextField display;
    private boolean isFinished = false;

    public ColculaterAppK () {
        setTitle("Simple Calculator");
        setSize(400, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(30, 30, 30));

        display = new JTextField("");
        display.setFont(new Font("Monospaced", Font.BOLD, 32));
        display.setHorizontalAlignment(JTextField.RIGHT);
        display.setEditable(false);
        display.setBackground(Color.BLACK);
        display.setForeground(Color.CYAN);
        display.setBorder(BorderFactory.createEmptyBorder(25, 10, 25, 10));
        add(display, BorderLayout.NORTH);

        JPanel panel = new JPanel(new GridLayout(0, 4, 10, 10));
        panel.setBackground(new Color(30, 30, 30));

        // Button set: x², CE, DEL, /, ... (-), √ , =
        String[] buttons = {
                "x²", "CE", "DEL", "/",
                "7", "8", "9", "*",
                "4", "5", "6", "-",
                "1", "2", "3", "+",
                "(", "0", ")", ".",
                "(-)", "√", "="
        };

        for (String text : buttons) {
            JButton btn = new JButton(text);
            btn.setFont(new Font("Arial", Font.BOLD, 18));
            btn.setFocusPainted(false);
            btn.addActionListener(this);
            styleButton(btn, text);
            panel.add(btn);
        }

        add(panel, BorderLayout.CENTER);
        ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setLocationRelativeTo(null);
    }

    private void styleButton(JButton btn, String text) {
        // Include "(-)" as an operator-style button
        if ("/*-+".contains(text) || text.equals("x²") || text.equals("√") || text.equals("(-)")) {
            btn.setBackground(new Color(33, 150, 243));
            btn.setForeground(Color.WHITE);
        } else if (text.equals("=")) {
            btn.setBackground(new Color(76, 175, 80));
            btn.setForeground(Color.WHITE);
        } else if (text.equals("CE") || text.equals("DEL")) {
            btn.setBackground(new Color(244, 67, 54));
            btn.setForeground(Color.WHITE);
        } else {
            btn.setBackground(Color.WHITE);
            btn.setForeground(Color.BLACK);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();

        if (display.getText().equals("Invalid")) {
            display.setText("");
        }

        switch (cmd) {
            case "=":
                solveExpression();
                isFinished = true;
                break;

            case "x²":
                squareValue();
                isFinished = true;
                break;

            case "CE":
                display.setText("");
                isFinished = false;
                break;

            case "DEL":
                String str = display.getText();
                if (str.length() > 0) {
                    display.setText(str.substring(0, str.length() - 1));
                }
                break;

            case "(-)":   // Replaced +/- with (-), same functionality
                toggleSign();
                isFinished = true;
                break;

            case "√":
                squareRoot();
                isFinished = true;
                break;

            default:
                String text = display.getText();

                if ("+-*/".contains(cmd)) {
                    if (text.isEmpty() || "+-*/".contains("" + text.charAt(text.length() - 1))) {
                        return;
                    }
                }

                if (isFinished && "0123456789(".contains(cmd)) {
                    display.setText("");
                }

                display.setText(display.getText() + cmd);
                isFinished = false;
                break;
        }
    }

    // ----- Toggle sign of the last number (normal +/- behavior) -----
    private void toggleSign() {
        String text = display.getText();
        if (text.isEmpty()) {
            display.setText("-");
            return;
        }

        int i = text.length() - 1;
        while (i >= 0 && (Character.isDigit(text.charAt(i)) || text.charAt(i) == '.')) {
            i--;
        }
        if (i >= 0 && text.charAt(i) == '-') {
            if (i == 0 || "+-*/(".contains("" + text.charAt(i - 1))) {
                i--;
                while (i >= 0 && (Character.isDigit(text.charAt(i)) || text.charAt(i) == '.')) {
                    i--;
                }
            }
        }

        int start = i + 1;
        int end = text.length();
        if (start >= end) {
            display.setText(text + "-");
            return;
        }

        String lastNumber = text.substring(start, end);
        String toggled;
        if (lastNumber.startsWith("-")) {
            toggled = lastNumber.substring(1);
            if (toggled.isEmpty()) toggled = "0";
        } else {
            toggled = "-" + lastNumber;
        }

        String newText = text.substring(0, start) + toggled;
        display.setText(newText);
    }

    // ----- Square the current value (x²) -----
    private void squareValue() {
        try {
            double val = evaluateCurrent();
            val = val * val;
            display.setText(formatResult(val));
        } catch (Exception ex) {
            display.setText("Invalid");
        }
    }

    // ----- Square root (√) -----
    private void squareRoot() {
        try {
            double val = evaluateCurrent();
            if (val < 0) {
                display.setText("Invalid");
                return;
            }
            val = Math.sqrt(val);
            display.setText(formatResult(val));
        } catch (Exception ex) {
            display.setText("Invalid");
        }
    }

    private double evaluateCurrent() throws Exception {
        String expr = display.getText();
        if (expr.isEmpty()) return 0;
        return eval(expr);
    }

    private void solveExpression() {
        String expression = display.getText();
        if (expression.isEmpty()) return;

        try {
            double result = eval(expression);
            if (Double.isInfinite(result) || Double.isNaN(result)) {
                display.setText("Invalid");
                return;
            }
            display.setText(formatResult(result));
        } catch (Exception ex) {
            display.setText("Invalid");
        }
    }

    private String formatResult(double result) {
        if (result == (long) result) {
            return String.format("%d", (long) result);
        } else {
            return String.valueOf(result);
        }
    }

    // ---------- Expression evaluator (unchanged) ----------
    public static double eval(final String str) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < str.length()) ? str.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char) ch);
                return x;
            }

            double parseExpression() {
                double x = parseTerm();
                for (; ; ) {
                    if (eat('+')) x += parseTerm();
                    else if (eat('-')) x -= parseTerm();
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (; ; ) {
                    if (eat('*')) x *= parseFactor();
                    else if (eat('/')) {
                        double divisor = parseFactor();
                        if (divisor == 0) throw new ArithmeticException("Division by zero");
                        x /= divisor;
                    } else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return parseFactor();
                if (eat('-')) return -parseFactor();

                double x;
                int startPos = this.pos;

                if (eat('(')) {
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') {
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(str.substring(startPos, this.pos));
                } else {
                    throw new RuntimeException("Unexpected: " + (char) ch);
                }
                return x;
            }
        }.parse();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new  ColculaterAppK().setVisible(true));
    }
} 