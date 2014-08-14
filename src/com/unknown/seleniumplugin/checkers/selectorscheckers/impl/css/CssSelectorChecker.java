package com.unknown.seleniumplugin.checkers.selectorscheckers.impl.css;

import com.unknown.seleniumplugin.checkers.selectorscheckers.CheckResult;
import com.unknown.seleniumplugin.checkers.selectorscheckers.ISelectorChecker;
import com.unknown.seleniumplugin.checkers.selectorscheckers.exceptions.EndOfSelector;
import com.unknown.seleniumplugin.checkers.selectorscheckers.exceptions.NotParsebleSelectorException;
import com.unknown.seleniumplugin.checkers.selectorscheckers.impl.Position;
import com.unknown.seleniumplugin.domain.SelectorSymbolConstants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by mike-sid on 30.04.14.
 * Checker of css selectors
 */
public class CssSelectorChecker implements ISelectorChecker {
    private static final String NTH_CHILD_FUNCTION_NAME = "nth-child";
    private static String functionNames = NTH_CHILD_FUNCTION_NAME + ",disabled,last-child,enabled,checked,first-child";
    private static char[] functionNamesElementsDictionary;
    private static List<String> functionsNamesList;

    static {
        functionNamesElementsDictionary = functionNames.replace(",", "").toCharArray();
        functionsNamesList = new ArrayList<String>();
        Collections.addAll(functionsNamesList, functionNames.split(","));
    }

    private static final String ATTRIBUTE_VALUE_NAME = "name";

    @Override
    public CheckResult checkSelectorValid(String selector) throws NotParsebleSelectorException {
        Position position = new Position();
        if (selector.isEmpty()) {
            return getCheckResultWithError("Selector can't be empty", position);
        }
        try {
            skipWhitespaces(selector, position);
        } catch (EndOfSelector e) {
            return getCheckResultWithError("Selector can't be empty", position);
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
            return getCheckResultWithError("Selector starts not with tag name or '.' or '#' or '[' or '*'", position);
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
                return getCheckResultWithError("There can't be * after *", position);
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
            return getCheckResultWithError("Function name can't contains only ':' without name of a function", position);
        }
        String functionName = parseFunctionName(selector, position);
        if (!functionsNamesList.contains(functionName)) {
            return getCheckResultWithError("Function name '" + functionName + "' is not valid. It should be one of : " + functionNames, position);
        }
        if (position.value() == selector.length()) {
            if (functionName.equals(NTH_CHILD_FUNCTION_NAME)) {
                return getCheckResultWithError("There must be an a child index in braces after " + NTH_CHILD_FUNCTION_NAME +
                        " function name", position);
            } else {
                return getSuccessCheckResult();
            }
        }
        char current = getCurrentChar(selector, position);
        if (functionName.equals(NTH_CHILD_FUNCTION_NAME)) {
            if (!isOpeningBracesElement(current)) {
                return getCheckResultWithError("There must be an '(' after " + NTH_CHILD_FUNCTION_NAME + " function", position);
            } else {
                try {
                    current = getNextChar(selector, position);
                } catch (EndOfSelector endOfSelector) {
                    return getCheckResultWithError("Selector can't ends with opening braces after'" + NTH_CHILD_FUNCTION_NAME +
                            "' function name.There must be an index", position);
                }

                if (!isChildValuePart(current)) {
                    return getCheckResultWithError("There must be an a digit after '(' in " + NTH_CHILD_FUNCTION_NAME + " function", position);
                }
                String indexValue = parseChildIndexValue(selector, position);
                if (indexValue.isEmpty()) {
                    return getCheckResultWithError("Index of child for function " + NTH_CHILD_FUNCTION_NAME + " can't be empty", position);
                }
                if (position.value() == selector.length()) {
                    return getCheckResultWithError("Selector can't ends with " + NTH_CHILD_FUNCTION_NAME +
                            " child index without closing braces", position);
                }
                current = getCurrentChar(selector, position);
                if (!isClosingBracesElement(current)) {
                    return getCheckResultWithError("There must be closing braces after " +
                            NTH_CHILD_FUNCTION_NAME + " function index value, no spaces and another digits", position);
                }
                try {
                    current = getNextChar(selector, position);
                } catch (EndOfSelector endOfSelector) {
                    return getSuccessCheckResult();
                }
            }
        }
        if (!isWhitespace(current)) {
            return getCheckResultWithError("There must be a space after function before another element", position);
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
            return getCheckResultWithError("Id can't contains only '#'. There should be an identifier", position);
        }
        char current = getCurrentChar(selector, position);
        if (isWhitespace(current)) {
            return getCheckResultWithError("There is a space after '#'", position);
        } else if (isClassStartCharacter(current)) {
            return getCheckResultWithError("There is a . after '#'", position);
        } else if (isOpeningElement(current)) {
            return getCheckResultWithError("There is a [ after '#'", position);
        } else if (isIdStartCharacter(current)) {
            return getCheckResultWithError("There is a # after '#'", position);
        } else if (isFunctionStartElement(current)) {
            return getCheckResultWithError("There is a : after '#'", position);
        } else if (isAnyElementDigit(current)) {
            return getCheckResultWithError("There is a * after '#'", position);
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
            return getCheckResultWithError("There can't be an id after id without spaces", position);
        } else if (isWhitespace(next)) {
            try {
                skipWhitespaces(selector, position);
                current = getCurrentChar(selector, position);
                if (isClassStartCharacter(current)) {
                    return parseClass(selector, position);
                } else if (isOpeningElement(current)) {
                    return parseAttributes(selector, position);
                    //TODO: check all elements after id
                } else if (isClosingElement(current) ) {
                    return getCheckResultWithError("There can't be an ']' without '[", position);
                } else if (isFunctionStartElement(current)) {
                    return getCheckResultWithError("There can't be an ':' without function name", position);
                } else if (isOpeningBracesElement(current) || isClosingBracesElement(current)) {
                    return getCheckResultWithError("There can't be an '(' or ')' without function name", position);
                } else if (isAttributeValueStrictlyEqualitySymbol(current)){
                    return getCheckResultWithError("There can't be an '='  without '[' and attribute name", position);
                } else if (isAttributeValueEndsWithSymbol(current)) {
                    return getCheckResultWithError("There can't be an '$'  without '[' and attribute name", position);
                } else if (isAttributeValueStartsWithSymbol(current)) {
                    return getCheckResultWithError("There can't be an '^'  without '[' and attribute name", position);
                } else if (isSingleQuotSymbol(current)) {
                    return getCheckResultWithError("There can't be an ' without '[' and attribute name", position);
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
            return getCheckResultWithError("Class can't contains only '.'. There should be a class name", position);
        }
        char current = getCurrentChar(selector, position);
        if (isWhitespace(current)) {
            return getCheckResultWithError("There is a space after '.'", position);
        } else if (isClassStartCharacter(current)) {
            return getCheckResultWithError("There is a . after '.'", position);
        } else if (isOpeningElement(current)) {
            return getCheckResultWithError("There is a [ after '.'", position);
        } else if (isIdStartCharacter(current)) {
            return getCheckResultWithError("There is a # after '.'", position);
        } else if (isFunctionStartElement(current)) {
            return getCheckResultWithError("There is a : after '.'", position);
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
                } if (isClosingElement(current) ) {
                    return getCheckResultWithError("There can't be an ']' without '[", position);
                } else if (isFunctionStartElement(current)) {
                    return getCheckResultWithError("There can't be an ':' without function name", position);
                } else if (isOpeningBracesElement(current) || isClosingBracesElement(current)) {
                    return getCheckResultWithError("There can't be an '(' or ')' without function name", position);
                } else if (isAttributeValueStrictlyEqualitySymbol(current)){
                    return getCheckResultWithError("There can't be an '='  without '[' and attribute name", position);
                } else if (isAttributeValueEndsWithSymbol(current)) {
                    return getCheckResultWithError("There can't be an '$'  without '[' and attribute name", position);
                } else if (isAttributeValueStartsWithSymbol(current)) {
                    return getCheckResultWithError("There can't be an '^'  without '[' and attribute name", position);
                } else if (isSingleQuotSymbol(current)) {
                    return getCheckResultWithError("There can't be an ' without '[' and attribute name", position);
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
        char current = getCurrentChar(selector, position);
        boolean hasWhitespace = false;
        if (isWhitespace(current)) {
            hasWhitespace = true;
            try {
                skipWhitespaces(selector, position);
            } catch (EndOfSelector endOfSelector) {
                return getSuccessCheckResult();
            }
        }
        current = getCurrentChar(selector, position);
        if (isClassStartCharacter(current)) {
            return parseClass(selector, position);
        } else if (isFunctionStartElement(current)) {
            if (hasWhitespace) {
                return getCheckResultWithError("There can't be space between tag name and ':' function start symbol", position);
            }
            return parseFunction(selector, position);
        } else if (isOpeningElement(current)) {
            return parseAttributes(selector, position);
        } else if (isIdStartCharacter(current)) {
            return parseId(selector, position);
        } else if (isAnyElementDigit(current)) {
            return parseAnyElementDigit(selector, position);
        } else if (isTagNameStartCharacter(current)) {
            return parseStartTag(selector, position);
        } if (isClosingElement(current) ) {
            return getCheckResultWithError("There can't be an ']' without '[", position);
        } else if (isFunctionStartElement(current)) {
            return getCheckResultWithError("There can't be an ':' without function name", position);
        } else if (isOpeningBracesElement(current) || isClosingBracesElement(current)) {
            return getCheckResultWithError("There can't be an '(' or ')' without function name", position);
        } else if (isAttributeValueStrictlyEqualitySymbol(current)){
            return getCheckResultWithError("There can't be an '='  without '[' and attribute name", position);
        } else if (isAttributeValueEndsWithSymbol(current)) {
            return getCheckResultWithError("There can't be an '$'  without '[' and attribute name", position);
        } else if (isAttributeValueStartsWithSymbol(current)) {
            return getCheckResultWithError("There can't be an '^'  without '[' and attribute name", position);
        } else if (isSingleQuotSymbol(current)) {
            return getCheckResultWithError("There can't be an ' without '[' and attribute name", position);
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
            return getCheckResultWithError("Selector can't contains only '['. There should be an attribute name", position);
        }

        current = getCurrentChar(selector, position);
        if (isClassStartCharacter(current)) {
            return getCheckResultWithError("There is a . after '['", position);
        } else if (isOpeningElement(current)) {
            return getCheckResultWithError("There is a [ after '['", position);
        } else if (isIdStartCharacter(current)) {
            return getCheckResultWithError("There is a # after '['", position);
        }
        String attributeName = parseSelectorAttributeName(selector, position);
        if (position.value() == selector.length()) {
            return getCheckResultWithError("Selector can't ends with attribute name. It should contain value and ']' symbol after.", position);
        }
        current = getCurrentChar(selector, position);
        try {
            if (isWhitespace(current)) {
                skipWhitespaces(selector, position);
            }
        } catch (EndOfSelector endOfSelector) {
            return getCheckResultWithError("Selector can't ends with attribute name. It should contain value and ']' symbol after.", position);
        }
        current = getCurrentChar(selector, position);
        if (!isAttributeValueStrictlyEqualitySymbol(current) && !isAttributeValueNotStrictlyEqualitySymbol(current)) {
            return getCheckResultWithError("Unexpected symbol after attribute name. There should be one of these after name :'=', '*', '^', '$'", position);
        }
        if (isAttributeValueNotStrictlyEqualitySymbol(current)) {
            try {
                current = getNextChar(selector, position);
            } catch (EndOfSelector endOfSelector) {
                return getCheckResultWithError("Selector can't ends with '*', '^' or '$'", position);
            }
        }
        if (!isAttributeValueStrictlyEqualitySymbol(current)) {
            return getCheckResultWithError("There must be an '=' after symbols '*', '^' or '$'", position);
        }

        try {
            current = getNextChar(selector, position);
            if (isWhitespace(current)) {
                skipWhitespaces(selector, position);
            }
        } catch (EndOfSelector endOfSelector) {
            return getCheckResultWithError("Selector can't ends with '=' after attribute name. There must be a value", position);
        }
        current = getCurrentChar(selector, position);
        if (!isSingleQuotSymbol(current)) {
            if (!attributeName.equals(ATTRIBUTE_VALUE_NAME)) {
                return getCheckResultWithError("There must be a single quot after '=' if attribute not a '" + ATTRIBUTE_VALUE_NAME + "'", position);
            }
        } else {
            try {
                getNextChar(selector, position);
            } catch (EndOfSelector endOfSelector) {
                return getCheckResultWithError("Selector can't ends with ' symbol after '='. There must be an attribute value", position);
            }
        }
        String attributeValue = parseSelectorAttributeValue(selector, position);
        if (attributeValue.isEmpty()) {
            return getCheckResultWithError("Attribute value can't be empty", position);
        }
        if (position.value() == selector.length()) {
            return getCheckResultWithError("Selector can't ends attribute value. It should contain  close ']'(if attribute is '" + ATTRIBUTE_VALUE_NAME + "') or ' '] ' symbol after.", position);
        }
        current = getCurrentChar(selector, position);
        if (!isSingleQuotSymbol(current)) {
            if (!attributeName.equals(ATTRIBUTE_VALUE_NAME)) {
                return getCheckResultWithError("There must be a single quot after attribute value if attribute not a '" + ATTRIBUTE_VALUE_NAME + "'", position);
            }

        } else {
            try {
                current = getNextChar(selector, position);
                if (isWhitespace(current)) {
                    skipWhitespaces(selector, position);
                }
            } catch (EndOfSelector endOfSelector) {
                return getCheckResultWithError("Selector can't ends with ' symbol after attribute value. It should have ']' symbol after", position);
            }
        }
        current = getCurrentChar(selector, position);
        if (!isClosingElement(current)) {
            return getCheckResultWithError("There must be an a ']' after attribute value", position);
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
            } else if (isFunctionStartElement(current)) {
                return getCheckResultWithError("There can't be : after whitespace. ", position);
            }
        } else {
            if (isClassStartCharacter(current)) {
                return getCheckResultWithError("There can't be . as next symbol after ']'", position);
            } else if (isIdStartCharacter(current)) {
                return getCheckResultWithError("There can't be # as next symbol after ']'", position);
            } else if (isTagNameStartCharacter(current)) {
                return getCheckResultWithError("There can't be tag name after symbol after ']' without space", position);
            } else if (isFunctionStartElement(current)) {
                return parseFunction(selector, position);
            }
        }
        return getSuccessCheckResult();
    }

    private boolean isSingleQuotSymbol(char ch) {
        return String.valueOf(ch).equals(SelectorSymbolConstants.ATTRIBUTE_VALUE_START_END_SYMBOL);
    }

    private boolean isAttributeValueNotStrictlyEqualitySymbol(char ch) {
        String value = String.valueOf(ch);
        return value.equals(SelectorSymbolConstants.ATTRIBUTE_VALUE_ENDS_WITH_SYMBOL) ||
                value.equals(SelectorSymbolConstants.ATTRIBUTE_VALUE_STARTS_WITH_SYMBOL) ||
                value.equals(SelectorSymbolConstants.ANY_CHARACTER_SYMBOL);
    }

    private boolean isAttributeValueStrictlyEqualitySymbol(char ch) {
        return String.valueOf(ch).equals(SelectorSymbolConstants.EQUAL_SYMBOL);
    }

    private boolean isAttributeValueEndsWithSymbol(char ch) {
        return String.valueOf(ch).equals(SelectorSymbolConstants.ATTRIBUTE_VALUE_ENDS_WITH_SYMBOL);
    }

    private boolean isAttributeValueStartsWithSymbol(char ch) {
        return String.valueOf(ch).equals(SelectorSymbolConstants.ATTRIBUTE_VALUE_STARTS_WITH_SYMBOL);
    }


    private CheckResult getSuccessCheckResult() {
        return new CheckResult(true, null);
    }

    private boolean isTagNameStartCharacter(char ch) {
        return Character.isLetter(ch);
    }

    private boolean isClassStartCharacter(char ch) {
        return String.valueOf(ch).equals(SelectorSymbolConstants.CLASS_SYMBOL);
    }

    private boolean isIdStartCharacter(char ch) {
        return String.valueOf(ch).equals(SelectorSymbolConstants.ID_SYMBOL);
    }

    private boolean isOpeningElement(char ch) {
        return String.valueOf(ch).equals(SelectorSymbolConstants.ATTRIBUTE_SELECTOR_PART_START_ELEMENT);
    }

    private boolean isClosingElement(char ch) {
        return String.valueOf(ch).equals(SelectorSymbolConstants.ATTRIBUTE_SELECTOR_PART_END_ELEMENT);
    }

    private boolean isAnyElementDigit(char ch) {
        return String.valueOf(ch).equals(SelectorSymbolConstants.ANY_CHARACTER_SYMBOL);
    }

    private boolean isFunctionStartElement(char ch) {
        return String.valueOf(ch).equals(SelectorSymbolConstants.FUNCTION_START_SYMBOL);
    }

    private boolean isStartElement(char ch) {
        return isTagNameStartCharacter(ch) || isClassStartCharacter(ch) || isIdStartCharacter(ch) ||
                isOpeningElement(ch) || isAnyElementDigit(ch);
    }


    private CheckResult getCheckResultWithError(String errorMessage, Position position) {
        CheckResult checkResult = new CheckResult(false, errorMessage);
        checkResult.setPosition(position.value());
        return checkResult;
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

    private String parseSelectorAttributeName(String selector, Position position) {
        char next = getCurrentChar(selector, position);
        int startTagPosition = position.value();
        boolean tagNameNotFinished = isAttributeNamePartPart(next);
        while (tagNameNotFinished) {
            try {
                next = getNextChar(selector, position);
                tagNameNotFinished = isAttributeNamePartPart(next);
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
        return isTagNamePart(ch) || ch == '-';
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


    private static boolean isAttributeNamePartPart(char ch) {
        return isTagNamePart(ch) || ch == '-';
    }

    private static boolean isAttributeValuePart(char ch) {
        return isTagNamePart(ch) || isWhitespace(ch) || ch == '-';
    }

    private static boolean isOpeningBracesElement(char ch) {
        return String.valueOf(ch).equals(SelectorSymbolConstants.FUNCTION_INDEX_OPENING_SYMBOL);
    }

    private static boolean isClosingBracesElement(char ch) {
        return String.valueOf(ch).equals(SelectorSymbolConstants.FUNCTION_INDEX_CLOSING_SYMBOL);
    }


}
