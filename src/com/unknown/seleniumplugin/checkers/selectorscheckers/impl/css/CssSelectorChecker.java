package com.unknown.seleniumplugin.checkers.selectorscheckers.impl.css;

import com.unknown.seleniumplugin.checkers.selectorscheckers.CheckResult;
import com.unknown.seleniumplugin.checkers.selectorscheckers.ISelectorChecker;
import com.unknown.seleniumplugin.checkers.selectorscheckers.exceptions.NotParsebleSelectorException;

import java.util.ArrayList;

/**
 * Created by mike-sid on 30.04.14.
 * Checker of css selectors
 */
public class CssSelectorChecker implements ISelectorChecker {
    private static final String NTH_CHILD_FUNCTION_NAME = "nth-child";
    private static String functionNames = NTH_CHILD_FUNCTION_NAME + ",disabled,last-child,enabled,checked,first-child";
    private static char[] functionNamesElementsDictionary;

    static {
        functionNamesElementsDictionary = functionNames.replace(",", "").toCharArray();
    }

    private static final String ATTRIBUTE_VALUE_NAME = "name";

    static class Position {
        int position = 0;

        int increment() {
            return ++position;
        }

        void decrement() {
            position--;
        }

        int value() {
            return position;
        }
    }

    static class EndOfSelector extends Exception {
        EndOfSelector(Exception e) {
            super(e);
        }
    }


    @Override
    public CheckResult checkSelectorValid(String selector) throws NotParsebleSelectorException {

        Position position = new Position();
        if (selector.isEmpty()) {
            return getCheckResultWithError("Selector can't be empty");
        }
        try {
            skipWhitespaces(selector, position);
        } catch (EndOfSelector e) {
            return getCheckResultWithError("Selector can't be empty");
        }
        char next = getCurrentChar(selector, position);
        if (isIdStartCharacter(next)) {
            return parseId(selector, position);
        } else if (isClassStartCharacter(next)) {
            return parseClass(selector, position);
        } else if (isTagNameStartCharacter(next)) {
            return parseStartTag(selector, position);
        } else if (isAnyElementDigit(next)) {
            return parseAnyElementDigit(selector, position);
        } else if (isOpeningElement(next)) {
            return parseAttributes(selector, position);
        } else {
            return getCheckResultWithError("Selector starts not with tag name or '.' or '#' or '[' or '*'");
        }


    }

    private CheckResult parseAnyElementDigit(String selector, Position position) throws NotParsebleSelectorException {
        try {
            char current = getNextChar(selector, position);
            if (isIdStartCharacter(current)) {
                return parseId(selector, position);
            } else if (isClassStartCharacter(current)) {
                return parseClass(selector, position);
            } else if (isTagNameStartCharacter(current)) {
                return parseStartTag(selector, position);
            } else if (isAnyElementDigit(current)) {
                return getCheckResultWithError("There can't be * after *");
            } else if (isOpeningElement(current)) {
                return parseAttributes(selector, position);
            } else if (isFunctionStartElement(current)) {
                return parseFunction(selector, position);
            } else {
                throw new NotParsebleSelectorException("Not parseble exception");
            }
        } catch (EndOfSelector e) {
            return getSuccessCheckResult();
        }
    }

    private CheckResult parseFunction(String selector, Position position) throws NotParsebleSelectorException {
        try {
            getNextChar(selector, position);
        } catch (EndOfSelector endOfSelector) {
            return getCheckResultWithError("Function name can't contains only ':' without name of a function");
        }
        String functionName = parseFunctionName(selector, position);
        if (position.value() == selector.length()) {
            if (functionName.equals(NTH_CHILD_FUNCTION_NAME)) {
                getCheckResultWithError("There must be an a child index in braces after " + NTH_CHILD_FUNCTION_NAME +
                        " function name");
            } else {
                return getSuccessCheckResult();
            }
        }
        char current = getCurrentChar(selector, position);
        if (functionName.equals(NTH_CHILD_FUNCTION_NAME)) {
            if (!isOpeningBracesElement(current)) {
                return getCheckResultWithError("There must be an '(' after " + NTH_CHILD_FUNCTION_NAME + " function");
            } else {
                try {
                   current = getNextChar(selector, position);
                } catch (EndOfSelector endOfSelector) {
                    return getCheckResultWithError("Selector can't ends with '" + NTH_CHILD_FUNCTION_NAME +
                            "' function name without braces");
                }

                if(!isChildValuePart(current)) {
                    return getCheckResultWithError("There must be an a digit after '(' in " + NTH_CHILD_FUNCTION_NAME + " function");
                }
                String indexValue = parseChildIndexValue(selector, position);
                if(indexValue.isEmpty()) {
                    return getCheckResultWithError("Index of child for function " + NTH_CHILD_FUNCTION_NAME + " can't be empty");
                }
                if (position.value() == selector.length()) {
                    return getCheckResultWithError("Selector can't ends with " + NTH_CHILD_FUNCTION_NAME +
                            " child index without closing braces");
                }
                current = getCurrentChar(selector, position);
                if (!isClosingBracesElement(current)) {
                    return getCheckResultWithError("There must be closing braces after " +
                            NTH_CHILD_FUNCTION_NAME + " function index value, no spaces and another digits");
                }
                try {
                    current = getNextChar(selector, position);
                } catch (EndOfSelector endOfSelector) {
                    return getSuccessCheckResult();
                }
            }
        }
        if (!isWhitespace(current)) {
            return getCheckResultWithError("There must be a space after function before another element");
        }
        try {
            skipWhitespaces(selector, position);
        } catch (EndOfSelector endOfSelector) {
            return getSuccessCheckResult();
        }
        try {
            current = getNextChar(selector, position);
        } catch (EndOfSelector endOfSelector) {
            return getSuccessCheckResult();
        }
        if (isIdStartCharacter(current)) {
            return parseId(selector, position);
        } else if (isClassStartCharacter(current)) {
            return parseClass(selector, position);
        } else if (isTagNameStartCharacter(current)) {
            return parseStartTag(selector, position);
        } else if (isAnyElementDigit(current)) {
            return parseAnyElementDigit(selector, position);
        } else if (isOpeningElement(current)) {
            return parseAttributes(selector, position);
        } else if (isFunctionStartElement(current)) {
            return parseFunction(selector, position);
        } else {
            throw new NotParsebleSelectorException("Not parseble exception");
        }
    }


    private CheckResult parseId(String selector, Position position) throws NotParsebleSelectorException {
        try {
            getNextChar(selector, position);
        } catch (EndOfSelector e) {
            return getCheckResultWithError("Id can't contains only '#'. There should be a tag name");
        }
        char current = getCurrentChar(selector, position);
        if (isWhitespace(current)) {
            return getCheckResultWithError("There is a space after '#'");
        } else if (isClassStartCharacter(current)) {
            return getCheckResultWithError("There is a . after '#'");
        } else if (isOpeningElement(current)) {
            return getCheckResultWithError("There is a [ after '#'");
        } else if (isIdStartCharacter(current)) {
            return getCheckResultWithError("There is a # after '#'");
        } else if (isFunctionStartElement(current)) {
            return getCheckResultWithError("There is a : after '#'");
        } else if (isAnyElementDigit(current)) {
            return getCheckResultWithError("There is a * after '#'");
        }
        parseSelectorStringPart(selector, position);
        if (position.value() == selector.length()) {
            return getSuccessCheckResult();
        }
        char next = getCurrentChar(selector, position);
        if (isClassStartCharacter(next)) {
            return parseClass(selector, position);
        } else if (isFunctionStartElement(next)) {
            return parseFunction(selector, position);
        } else if (isOpeningElement(next)) {
            return parseAttributes(selector, position);
        } else if (isIdStartCharacter(next)) {
            return getCheckResultWithError("There can't be an id after id without spaces");
        } else if (isWhitespace(next)) {
            try {
                skipWhitespaces(selector, position);
                current = getCurrentChar(selector, position);
                if (isClassStartCharacter(current)) {
                    return parseClass(selector, position);
                } else if (isOpeningElement(current)) {
                    return parseAttributes(selector, position);
                }
            } catch (EndOfSelector endOfSelector) {
                getSuccessCheckResult();
            }
        }
        return getSuccessCheckResult();
    }

    private CheckResult parseClass(String selector, Position position) throws NotParsebleSelectorException {
        try {
            getNextChar(selector, position);
        } catch (EndOfSelector e) {
            return getCheckResultWithError("Class can't contains only '.'. There should be a class name");
        }
        char current = getCurrentChar(selector, position);
        if (isWhitespace(current)) {
            return getCheckResultWithError("There is a space after '.'");
        } else if (isClassStartCharacter(current)) {
            return getCheckResultWithError("There is a . after '.'");
        } else if (isOpeningElement(current)) {
            return getCheckResultWithError("There is a [ after '.'");
        } else if (isIdStartCharacter(current)) {
            return getCheckResultWithError("There is a # after '.'");
        } else if (isFunctionStartElement(current)) {
            return getCheckResultWithError("There is a : after '.'");
        }
        parseClassValue(selector, position);
        if (position.value() == selector.length()) {
            return getSuccessCheckResult();
        }
        char next = getCurrentChar(selector, position);
        if (isClassStartCharacter(next)) {
            return parseClass(selector, position);
        } else if (isFunctionStartElement(next)) {
            return parseFunction(selector, position);
        } else if (isOpeningElement(next)) {
            return parseAttributes(selector, position);
        } else if (isWhitespace(next)) {
            try {
                skipWhitespaces(selector, position);
                current = getCurrentChar(selector, position);
                if (isClassStartCharacter(current)) {
                    return parseClass(selector, position);
                } else if (isOpeningElement(current)) {
                    return parseAttributes(selector, position);
                }
            } catch (EndOfSelector endOfSelector) {
                getSuccessCheckResult();
            }
        }
        return getSuccessCheckResult();
    }


    private CheckResult parseStartTag(String selector, Position position) throws NotParsebleSelectorException {
        parseSelectorStringPart(selector, position);
        if (position.value() == selector.length()) {
            return getSuccessCheckResult();
        }
        char next = getCurrentChar(selector, position);
        if (isClassStartCharacter(next)) {
            return parseClass(selector, position);
        } else if (isFunctionStartElement(next)) {
            return parseFunction(selector, position);
        } else if (isOpeningElement(next)) {
            return parseAttributes(selector, position);
        } else if (isIdStartCharacter(next)) {
            return parseId(selector, position);
        } else if (isAnyElementDigit(next)) {
            return parseAnyElementDigit(selector, position);
        } else {
            throw new NotParsebleSelectorException("Not parseble exception");
        }
    }


    private CheckResult parseAttributes(String selector, Position position) throws NotParsebleSelectorException {
        char current;
        try {
            current = getNextChar(selector, position);
            if (isWhitespace(current)) {
                skipWhitespaces(selector, position);
            }
        } catch (EndOfSelector e) {
            return getCheckResultWithError("Selector can't contains only '['. There should be an attribute name");
        }

        current = getCurrentChar(selector, position);
        if (isClassStartCharacter(current)) {
            return getCheckResultWithError("There is a . after '['");
        } else if (isOpeningElement(current)) {
            return getCheckResultWithError("There is a [ after '['");
        } else if (isIdStartCharacter(current)) {
            return getCheckResultWithError("There is a # after '['");
        }
        String attributeName = parseSelectorStringPart(selector, position);
        if (position.value() == selector.length()) {
            return getCheckResultWithError("Selector can't ends with attribute name. It should contain value and ']' symbol after.");
        }
        current = getCurrentChar(selector, position);
        try {
            if (isWhitespace(current)) {
                skipWhitespaces(selector, position);
            }
        } catch (EndOfSelector endOfSelector) {
            return getCheckResultWithError("Selector can't ends with attribute name. It should contain value and ']' symbol after.");
        }
        current = getCurrentChar(selector, position);
        if (!isAttributeValueStrictlyEqualitySymbol(current) && !isAttributeValueNotStrictlyEqualitySymbol(current)) {
            return getCheckResultWithError("Unexpected symbol after attribute name. There should be one of these after name :'=', '*', '^', '$'");
        }
        if (isAttributeValueNotStrictlyEqualitySymbol(current)) {
            try {
                current = getNextChar(selector, position);
            } catch (EndOfSelector endOfSelector) {
                return getCheckResultWithError("Selector can't ends with '*', '^' or '$'");
            }
        }
        if (!isAttributeValueStrictlyEqualitySymbol(current)) {
            return getCheckResultWithError("There must be an '=' after symbols '*', '^' or '$'");
        }

        try {
            current = getNextChar(selector, position);
            if (isWhitespace(current)) {
                skipWhitespaces(selector, position);
            }
        } catch (EndOfSelector endOfSelector) {
            return getCheckResultWithError("Selector can't ends with '=' after attribute name. There must be a value");
        }
        current = getCurrentChar(selector, position);
        if (!isSingleQuotSymbol(current)) {
            if (!attributeName.equals(ATTRIBUTE_VALUE_NAME)) {
                return getCheckResultWithError("There must be a single quot after '=' if attribute not a '" + ATTRIBUTE_VALUE_NAME + "'");
            }
        } else {
            try {
                getNextChar(selector, position);
            } catch (EndOfSelector endOfSelector) {
                return getCheckResultWithError("Selector can't ends with ' symbol after '='. There must be an attribute value");
            }
        }
        parseSelectorAttributeValue(selector, position);
        if (position.value() == selector.length()) {
            return getCheckResultWithError("Selector can't ends attribute value. It should contain  close ']'(if attribute is '" + ATTRIBUTE_VALUE_NAME + "') or ' '] ' symbol after.");
        }
        current = getCurrentChar(selector, position);
        if (!isSingleQuotSymbol(current)) {
            if (!attributeName.equals(ATTRIBUTE_VALUE_NAME)) {
                return getCheckResultWithError("There must be a single quot after attribute value if attribute not a '" + ATTRIBUTE_VALUE_NAME + "'");
            }

        } else {
            try {
                current = getNextChar(selector, position);
                if (isWhitespace(current)) {
                    skipWhitespaces(selector, position);
                }
            } catch (EndOfSelector endOfSelector) {
                return getCheckResultWithError("Selector can't ends with ' symbol after attribute value. It should have ']' symbol after");
            }
        }
        current = getCurrentChar(selector, position);
        if (!isClosingElement(current)) {
            return getCheckResultWithError("There must be an a ']' after attribute value");
        }
        boolean hasWhiteSpace = false;
        try {
            current = getNextChar(selector, position);
            if (isWhitespace(current)) {
                hasWhiteSpace = true;
                skipWhitespaces(selector, position);
            }
        } catch (EndOfSelector endOfSelector) {
            return getSuccessCheckResult();
        }
        current = getCurrentChar(selector, position);
        if (isOpeningElement(current)) {
            return parseAttributes(selector, position);
        }
        if (hasWhiteSpace) {
            if (isIdStartCharacter(current)) {
                return parseId(selector, position);
            } else if (isClassStartCharacter(current)) {
                return parseClass(selector, position);
            } else if (isTagNameStartCharacter(current)) {
                return parseStartTag(selector, position);
            } else if (isOpeningElement(current)) {
                return parseAttributes(selector, position);
            } else if(isFunctionStartElement(current)) {
                return getCheckResultWithError("There can't be : after whitespace. ");
            }
        } else {
            if (isClassStartCharacter(current)) {
                return getCheckResultWithError("There can't be . as next symbol after ']'");
            } else if (isIdStartCharacter(current)) {
                return getCheckResultWithError("There can't be # as next symbol after ']'");
            }
            else if (isTagNameStartCharacter(current)) {
                return getCheckResultWithError("There can't be tag name after symbol after ']' without space");
            } else if(isFunctionStartElement(current)) {
                return parseFunction(selector, position);
            }
        }
        return getSuccessCheckResult();
    }

    private boolean isSingleQuotSymbol(char ch) {
        return ch == '\'';
    }

    private boolean isAttributeValueNotStrictlyEqualitySymbol(char ch) {
        return ch == '*' || ch == '^' || ch == '$';
    }

    private boolean isAttributeValueStrictlyEqualitySymbol(char ch) {
        return ch == '=';
    }


    private CheckResult getSuccessCheckResult() {
        return new CheckResult(true, null);
    }

    private boolean isTagNameStartCharacter(char ch) {
        return Character.isLetter(ch);
    }

    private boolean isClassStartCharacter(char ch) {
        return ch == '.';
    }

    private boolean isIdStartCharacter(char ch) {
        return ch == '#';
    }

    private boolean isOpeningElement(char ch) {
        return ch == '[';
    }

    private boolean isClosingElement(char ch) {
        return ch == ']';
    }

    private boolean isAnyElementDigit(char ch) {
        return ch == '*';
    }

    private boolean isFunctionStartElement(char ch) {
        return ch == ':';
    }

    private boolean isStartElement(char ch) {
        return isTagNameStartCharacter(ch) || isClassStartCharacter(ch) || isIdStartCharacter(ch) ||
                isOpeningElement(ch) || isAnyElementDigit(ch);
    }


    private CheckResult getCheckResultWithError(String errorMessage) {
        return new CheckResult(false, errorMessage);
    }

    private char getCurrentChar(String selector, Position position) {
        return selector.charAt(position.value());
    }

    private char getNextChar(String selector, Position position) throws EndOfSelector {
        try {
            return selector.charAt(position.increment());
        } catch (Exception e) {
            throw new EndOfSelector(e);
        }
    }

    private void skipWhitespaces(String selector, Position position) throws EndOfSelector {
        while (isWhitespace(getCurrentChar(selector, position))) {
            getNextChar(selector, position);
        }
    }

    private static boolean isWhitespace(char ch) {
        return ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r';
    }

    private String parseSelectorStringPart(String selector, Position position) {
        char next = getCurrentChar(selector, position);
        int startTagPosition = position.value();
        boolean tagNameNotFinished = isTagNamePart(next);
        while (tagNameNotFinished) {
            try {
                next = getNextChar(selector, position);
                tagNameNotFinished = isTagNamePart(next);
            } catch (EndOfSelector e) {
                tagNameNotFinished = false;
            }
        }
        int endTagPosition = position.value();
        return selector.substring(startTagPosition, endTagPosition);
    }

    private String parseSelectorAttributeValue(String selector, Position position) {
        char next = getCurrentChar(selector, position);
        int startTagPosition = position.value();
        boolean tagNameNotFinished = isAttributeValuePart(next);
        while (tagNameNotFinished) {
            try {
                next = getNextChar(selector, position);
                tagNameNotFinished = isAttributeValuePart(next);
            } catch (EndOfSelector e) {
                tagNameNotFinished = false;
            }
        }
        int endTagPosition = position.value();
        return selector.substring(startTagPosition, endTagPosition);
    }

    private String parseFunctionName(String selector, Position position) {
        char next = getCurrentChar(selector, position);
        int startNamePosition = position.value();
        boolean functionNameNotFinished = isFunctionNamePart(next);
        while (functionNameNotFinished) {
            try {
                next = getNextChar(selector, position);
                functionNameNotFinished = isFunctionNamePart(next);
            } catch (EndOfSelector e) {
                functionNameNotFinished = false;
            }
        }
        int endNamePosition = position.value();
        return selector.substring(startNamePosition, endNamePosition);
    }

    private String parseChildIndexValue(String selector, Position position) {
        char next = getCurrentChar(selector, position);
        int startValuePosition = position.value();
        boolean valueNotFinished = isChildValuePart(next);
        while (valueNotFinished) {
            try {
                next = getNextChar(selector, position);
                valueNotFinished = isChildValuePart(next);
            } catch (EndOfSelector e) {
                valueNotFinished = false;
            }
        }
        int endValuePosition = position.value();
        return selector.substring(startValuePosition, endValuePosition);

    }

    private String parseClassValue(String selector, Position position) {
        char next = getCurrentChar(selector, position);
        int startClassPosition = position.value();
        boolean valueNotFinished = isClassValuePart(next);
        while (valueNotFinished) {
            try {
                next = getNextChar(selector, position);
                valueNotFinished = isClassValuePart(next);
            } catch (EndOfSelector e) {
                valueNotFinished = false;
            }
        }
        int endClassPosition = position.value();
        return selector.substring(startClassPosition, endClassPosition);
    }

    private static boolean isClassValuePart(char ch) {
        return isTagNamePart(ch)  || ch == '-';
    }

    private static boolean isChildValuePart(char ch) {
        return Character.isDigit(ch);
    }


    private boolean isFunctionNamePart(char ch) {
        for (char functionNameLetter : functionNamesElementsDictionary) {
            if (ch == functionNameLetter) {
                return true;
            }
        }
        return false;
    }


    private static boolean isTagNamePart(char ch) {
        return Character.isLetter(ch) || ch == '_' || Character.isDigit(ch);
    }

    private static boolean isAttributeValuePart(char ch) {
        return isTagNamePart(ch) || isWhitespace(ch) || ch == '-';
    }

    private static boolean isOpeningBracesElement(char ch) {
        return ch == '(';
    }

    private static boolean isClosingBracesElement(char ch) {
        return ch == ')';
    }


}
