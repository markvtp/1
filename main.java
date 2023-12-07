package com.example.complier;

import com.example.complier.LexicalException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class Main {

    private static final Map<String, Integer> keyWords = Map.of(
            "program", 1,
            "var", 2,
            "real", 3,
            "begin", 4,
            "end.", 5,
            "read", 6,
            "write", 7,
            "repeat", 8,
            "until", 9,
            "if", 10
    );

  public static final Map<String, Integer> separators = Map.of(
            "*", 29,
            "(", 15,
            ")", 16,
            ",", 25,
            "\n",24
    );

    private static final Map<String, Integer> operator = Map.of(
            "||", 12,
            "&&", 13,
            "!", 14
    );

    private static final Map<String, Integer> relation = Map.of(
            "==", 17,
            "!=", 18,
            ">", 19,
            "≥", 20,
            "<", 21,
            "≤", 22
    );

    private static final int INITIAL = 1;

    public static List<Token> outputTable = new ArrayList<>();
    private static List<DynamicLexeme> identifiers = new ArrayList<>();
    private static List<DynamicLexeme> constants = new ArrayList<>();

    public static void main(String[] args) {
        stateMachine("factorial:=+1.+1.+()123");
        System.out.printf("Output table: \n%s\n", outputTable);
    }

    static void stateMachine(String input) {

        String token = "";
        int state = INITIAL;
        int index = 0;
        char current;

        while (index < input.length()) {
            current = input.charAt(index++);

            switch (state) {
                case 1:
                    if (Character.isWhitespace(current)) {
                        // Skip white spaces
                        continue;
                    } else if (Character.isLetter(current)) {
                        token += current;
                        state = 2;
                    } else if (Character.isDigit(current)) {
                        token += current;
                        state = 3;
                    } else if (current == '+') {
                        token += current;
                        extractToken(token, 27);
                        state = 6;
                    } else if (current == '.') {
                        token += current;
                        extractToken(token, 5);
                        state = 5;
                    } else if (current == ':') {
                        token += current;
                        state = 7;
                    } else if (separators.containsKey(String.valueOf(current))) {
                        Integer code = separators.get(String.valueOf(current));
                        extractToken(String.valueOf(current), code);
                        token = "";
                        state = INITIAL;
                    } else if (operator.containsKey(String.valueOf(current))) {
                        Integer code = operator.get(String.valueOf(current));
                        extractToken(String.valueOf(current), code);
                        token = "";
                        state = INITIAL;
                    } else if (relation.containsKey(String.valueOf(current))) {
                        Integer code = relation.get(String.valueOf(current));
                        extractToken(String.valueOf(current), code);
                        token = "";
                        state = INITIAL;
                    } else {
                        System.out.println("Error: Invalid character");
                    }
                    break;


                case 2: {
                    if (Character.isLetterOrDigit(current)) {
                        token += current;
                        index++;
                        state = 2;
                    } else if (current == '.') {
                        if (token.equals("end.")) {
                            extractToken(token, 5);
                            state = INITIAL;
                        } else {
                            throw new LexicalException("Error in position %d%n".formatted(index));
                        }
                    } else {
                        Integer code = keyWords.get(token);
                        if (code != null) {
                            extractToken(token, code);
                        } else {
                            extractToken(token, IDENTIFIER);
                        }
                        state = INITIAL;
                        index--;
                    }
                }
                break;


                case 3:
                    if (Character.isDigit(current)) {
                        token += current;
                        state = 3;
                    } else if (current == '.') {
                        token += current;
                        state = 4;
                    } else {
                        extractToken(token, CONSTANT);
                        state = INITIAL;
                        index--;
                    }
                    break;

                case 4:
                    if (Character.isDigit(current)) {
                        token += current;
                        state = 4;
                    } else {
                        extractToken(token, CONSTANT);
                        state = INITIAL;
                        index--;
                    }
                    break;

                case 5:
                    if (Character.isDigit(current)) {
                        token += current;
                        state = 4;
                    }else {
                        System.out.println("Error: Invalid character");
                    }
                    break;

                case 6:
                    if (Character.isDigit(current)) {
                        token += current;
                        state = 3;
                    }else {
                        extractToken(token, 27);
                        state = INITIAL;
                        index--;
                    }
                    break;

                case 7:
                    if (current == '=') {
                        token += current;
                        extractToken(token, 26);
                    } else {
                        extractToken(token, 23);
                        token = "";
                        state = INITIAL;
                        index--;
                    }
                    break;
                case 8:   //The Separator
                    if (current == ',') {
                        token += current;
                        extractToken(token, 25);
                    }else if (current == '(') {
                        token += current;
                        extractToken(token, 15);
                    }else if (current == ')') {
                        token += current;
                        extractToken(token, 16);
                    }else if (current == '\n') {
                        extractToken(token, 24);
                        token = "";
                        state = INITIAL;
                        index--;
                    }
                    break;
                case 9:   //The Logical Operator  like || &&  !
                    if (String.valueOf(current).equals("||")){
                        token += current;
                        extractToken(token, 12);
                    }else if (String.valueOf(current).equals("&&")) {
                        token += current;
                        extractToken(token, 13);
                    }else if (String.valueOf(current).equals("!")) {
                        extractToken(token, 14);
                        token = "";
                        state = INITIAL;
                        index--;
                    }
                    break;

                case 10:  //The Realtion like == != > ≥ < ≤
                    if (String.valueOf(current).equals("==")){
                        token += current;
                        extractToken(token, 17);
                    }else if (String.valueOf(current).equals("!=")) {
                        token += current;
                        extractToken(token, 18);
                    }else if (String.valueOf(current).equals(">")) {
                        token += current;
                        extractToken(token, 19);
                    }else if (String.valueOf(current).equals("≥")) {
                        token += current;
                        extractToken(token, 20);
                    }else if (String.valueOf(current).equals("<")) {
                        token += current;
                        extractToken(token, 21);
                    }else if (String.valueOf(current).equals("≤")) {
                        extractToken(token, 22);
                        token = "";
                        state = INITIAL;
                        index--;
                    }
                    break;
                default:
                    System.out.println("Error: Invalid state");
                    break;
            }
        }
    }


    public static void extractToken(String token, Integer code) {
        System.out.printf("Lexeme: %s, Code: %d%n", token, code);
        if (code == IDENTIFIER) {
            identifiers.add(new DynamicLexeme(token, identifiers.size()));
        }
        outputTable.add(new Token(null, token, code, null));
    }

    public static boolean isDigit(char current) {
        return current >= '0' && current <= '9';
    }

    public static boolean isLetter(char current) {
        return (current >= 'a' && current <= 'z') || (current >= 'A' && current <= 'Z');
    }

    public static boolean isSeparator(char current) {
        return "*(),;".contains(""+ current);

    }

    public static final int IDENTIFIER = 100;
    public static final int CONSTANT = 12;

    static class Token {
        Integer Line;
        String text;
        Integer code;
        Integer idCode;

        public Token(Integer line, String text, Integer code, Integer idCode) {
            Line = line;
            this.text = text;
            this.code = code;
            this.idCode = idCode;
        }

        @Override
        public String toString() {
            return "Token{" +
                    "Line=" + Line +
                    ", text='" + text + '\'' +
                    ", code=" + code +
                    ", idCode=" + idCode +
                    '}';
        }
    }

    static class DynamicLexeme {
        String text;
        Integer code;

        public DynamicLexeme(String text, Integer code) {
            this.text = text;
            this.code = code;
        }

        @Override
        public String toString() {
            return "DynamicLexeme{" +
                    "text='" + text + '\'' +
                    ", code=" + code +
                    '}';
        }
    }
}