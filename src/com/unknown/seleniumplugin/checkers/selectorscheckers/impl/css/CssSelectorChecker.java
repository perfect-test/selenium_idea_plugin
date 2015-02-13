package com.unknown.seleniumplugin.checkers.selectorscheckers.impl.css;

import com.unknown.seleniumplugin.checkers.selectorscheckers.CheckResult;
import com.unknown.seleniumplugin.checkers.selectorscheckers.ISelectorChecker;
import com.unknown.seleniumplugin.checkers.selectorscheckers.exceptions.EndOfSelector;
import com.unknown.seleniumplugin.checkers.selectorscheckers.exceptions.NotParsebleSelectorException;
import com.unknown.seleniumplugin.checkers.selectorscheckers.impl.Position;
import com.unknown.seleniumplugin.domain.CssSelectorSymbolConstants;

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
            return getCheckResultWithError("Selector can't be empty", position, "Add some value");
        }
        try {
            skipWhitespaces(selector, position);
        } catch (EndOfSelector e) {
            return getCheckResultWithError("Selector can't be empty", position, "Add some value");
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
            return getCheckResultWithError("Selector starts not with tag name or '.' or '#' or '[' or '*'", position,
                    "Fix selector start. There can be: tag name, '.'(class name)," +
                            "'#' (id symbol),'[','*'(any element)");
        }


    }

    @Override
    public String getName() {
        return "css";
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
                return getCheckResultWithError("There can't be * after *", position, "Replace second *");
            } else if (isOpeningElement(current)) {
                return parseAttributes(selector, position);
            } else if (isFunctionStartElement(current)) {
                return parseFunction(selector, position);
            } else {
                throw new NotParsebleSelectorException("Selector not parsed");
            }
        } catch (EndOfSelector e) {
            return getSuccessCheckResult();
        }
    }

    private CheckResult parseFunction(String selector, Position position) throws NotParsebleSelectorException {
        try {
            getNextChar(selector, position);
        } catch (EndOfSelector endOfSelector) {
            return getCheckResultWithError("Function name can't contains only ':' without name of a function", position,
                    "Add one of (" + functionNames + ") functions after ':' symbol");
        }
        String functionName = parseFunctionName(selector, position);
        if (!functionsNamesList.contains(functionName)) {
            return getCheckResultWithError("Function name '" + functionName + "' is not valid. ", position,
                    "Change the function name. It could be one of (" + functionNames + ")");
        }
        if (position.value() == selector.length()) {
            if (functionName.equals(NTH_CHILD_FUNCTION_NAME)) {
                return getCheckResultWithError("There must be an a child index in braces after " + NTH_CHILD_FUNCTION_NAME +
                        " function name", position, "Add braces and index of child you want to find");
            } else {
                return getSuccessCheckResult();
            }
        }
        char current = getCurrentChar(selector, position);
        if (functionName.equals(NTH_CHILD_FUNCTION_NAME)) {
            if (!isOpeningBracesElement(current)) {
                return getCheckResultWithError("There must be an '(' after " + NTH_CHILD_FUNCTION_NAME + " function", position,
                        "Add braces and index of child you want to find");
            } else {
                try {
                    current = getNextChar(selector, position);
                } catch (EndOfSelector endOfSelector) {
                    return getCheckResultWithError("Selector can't ends with opening braces after'" + NTH_CHILD_FUNCTION_NAME +
                            "' function name.There must be an index", position, "Add index of child and closing braces to your locator");
                }

                if (!isChildValuePart(current)) {
                    return getCheckResultWithError("There must be an a number after '(' in " + NTH_CHILD_FUNCTION_NAME + " function", position,
                            "Change the value in braces to number value or remove space between number and opening braces");
                }
                String indexValue = parseChildIndexValue(selector, position);
                if (indexValue.isEmpty()) {
                    return getCheckResultWithError("Index of child for function " + NTH_CHILD_FUNCTION_NAME + " can't be empty", position,
                            "Add an index - number value - to the braces");
                }
                if (position.value() == selector.length()) {
                    return getCheckResultWithError("Selector can't ends with " + NTH_CHILD_FUNCTION_NAME +
                            " child index without closing braces", position, "Add closing braces after child index");
                }
                current = getCurrentChar(selector, position);
                if (!isClosingBracesElement(current)) {
                    return getCheckResultWithError("There must be closing braces after " +
                            NTH_CHILD_FUNCTION_NAME + " function index value, no spaces and another digits", position,
                            "Remove spaces or unexpected symbols between child index and closing braces.");
                }
                try {
                    current = getNextChar(selector, position);
                } catch (EndOfSelector endOfSelector) {
                    return getSuccessCheckResult();
                }
            }
        }
        if (!isWhitespace(current)) {
            return getCheckResultWithError("There must be a space after function before another element", position,
                    "Add a space after function name and before another element");
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
            throw new NotParsebleSelectorException("Selector not parsed");
        }
    }


    private CheckResult parseId(String selector, Position position) throws NotParsebleSelectorException {
        try {
            getNextChar(selector, position);
        } catch (EndOfSelector e) {
            return getCheckResultWithError("Id can't contains only '#'. There should be an identifier", position,
                    "Add an identifier name after '#' symbol");
        }
        char current = getCurrentChar(selector, position);
        if (isWhitespace(current)) {
            return getCheckResultWithError("There is a space after '#'", position, "Remove space after '#' symbol");
        } else if (isClassStartCharacter(current)) {
            return getCheckResultWithError("There is a . after '#'", position, "Remove '.' after '#' symbol");
        } else if (isOpeningElement(current)) {
            return getCheckResultWithError("There is a [ after '#'", position, "Remove '[' after '#' symbol");
        } else if (isClosingElement(current)) {
            return getCheckResultWithError("There is a ] after '#'", position, "Remove ']' after '#' symbol");
        } else if (isIdStartCharacter(current)) {
            return getCheckResultWithError("There is a # after '#'", position, "Remove '#' after '#' symbol");
        } else if (isFunctionStartElement(current)) {
            return getCheckResultWithError("There is a : after '#'", position, "Remove ':' after '#' symbol");
        } else if (isAnyElementDigit(current)) {
            return getCheckResultWithError("There is a * after '#'", position, "Remove '*' after '#' symbol");
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
            return getCheckResultWithError("There can't be an id after id without spaces", position,
                    "Add space after first identifier name and before '#' symbol");
        } else if (isWhitespace(next)) {
            try {
                skipWhitespaces(selector, position);
                current = getCurrentChar(selector, position);
                if (isClassStartCharacter(current)) {
                    return parseClass(selector, position);
                } else if (isOpeningElement(current)) {
                    return parseAttributes(selector, position);
                } else if (isIdStartCharacter(current)) {
                    return parseId(selector, position);
                } else if (isClosingElement(current)) {
                    return getCheckResultWithError("There can't be an ']' without '[", position,
                            "Remove ']' after identifier or add to locator search by attribute name and value");
                } else if (isFunctionStartElement(current)) {
                    return getCheckResultWithError("There can't be an ':' without function name", position,
                            "Remove ':' after identifier or add to locator search with function name");
                } else if (isOpeningBracesElement(current) || isClosingBracesElement(current)) {
                    return getCheckResultWithError("There can't be an '(' or ')' without function name", position,
                            "Remove '(' or ')' after identifier or add to locator search with function name(nth-child)");
                } else if (isAttributeValueStrictlyEqualitySymbol(current)) {
                    return getCheckResultWithError("There can't be an '='  without '[' and attribute name", position,
                            "Remove '='  after identifier. This symbol can be only when searching by attribute name or value(in [ braces)");
                } else if (isAttributeValueEndsWithSymbol(current)) {
                    return getCheckResultWithError("There can't be an '$'  without '[' and attribute name", position,
                            "Remove '$'  after identifier. This symbol can be only when searching by attribute name or value(in [ braces)");
                } else if (isAttributeValueStartsWithSymbol(current)) {
                    return getCheckResultWithError("There can't be an '^'  without '[' and attribute name", position,
                            "Remove '^'  after identifier. This symbol can be only when searching by attribute name or value(in [ braces)");
                } else if (isSingleQuotSymbol(current)) {
                    return getCheckResultWithError("There can't be an ' without '[' and attribute name", position,
                            "Remove '[' after identifier of add searching by attribute name of value");
                } else if(isTagNameStartCharacter(current)){
                    return parseStartTag(selector, position);
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
            return getCheckResultWithError("Class can't contains only '.'. There should be a class name", position,
                    "Add a class name after '.' symbol");
        }
        char current = getCurrentChar(selector, position);
        if (isWhitespace(current)) {
            return getCheckResultWithError("There is a space after '.'", position, "Remove space after '.' symbol");
        } else if (isClassStartCharacter(current)) {
            return getCheckResultWithError("There is a . after '.'", position, "Remove '.' after '.' symbol");
        } else if (isOpeningElement(current)) {
            return getCheckResultWithError("There is a [ after '.'", position, "Remove '[' after '.' symbol");
        } else if (isClosingElement(current)) {
            return getCheckResultWithError("There is a [ after '.'", position, "Remove ']' after '.' symbol");
        } else if (isIdStartCharacter(current)) {
            return getCheckResultWithError("There is a # after '.'", position, "Remove '#' after '.' symbol");
        } else if (isFunctionStartElement(current)) {
            return getCheckResultWithError("There is a : after '.'", position, "Remove ':' after '.' symbol");
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
                } else if (isIdStartCharacter(current)) {
                    return parseId(selector, position);
                }
                if (isClosingElement(current)) {
                    return getCheckResultWithError("There can't be an ']' without '[", position,
                            "Remove ']' after class name or add to locator search by attribute name and value");
                } else if (isFunctionStartElement(current)) {
                    return getCheckResultWithError("There can't be an ':' without function name", position,
                            "Remove ':' after class name or add to locator search with function name");
                } else if (isOpeningBracesElement(current) || isClosingBracesElement(current)) {
                    return getCheckResultWithError("There can't be an '(' or ')' without function name", position,
                            "Remove '(' or ')' after class name or add to locator search with function name(nth-child)");
                } else if (isAttributeValueStrictlyEqualitySymbol(current)) {
                    return getCheckResultWithError("There can't be an '='  without '[' and attribute name", position,
                            "Remove '='  after class name. This symbol can be only when searching by attribute name or value(in [ braces)");
                } else if (isAttributeValueEndsWithSymbol(current)) {
                    return getCheckResultWithError("There can't be an '$'  without '[' and attribute name", position,
                            "Remove '$'  after class name. This symbol can be only when searching by attribute name or value(in [ braces)");
                } else if (isAttributeValueStartsWithSymbol(current)) {
                    return getCheckResultWithError("There can't be an '^'  without '[' and attribute name", position,
                            "Remove '^'  after class name. This symbol can be only when searching by attribute name or value(in [ braces)");
                } else if (isSingleQuotSymbol(current)) {
                    return getCheckResultWithError("There can't be an ' without '[' and attribute name", position,
                            "Remove '[' after class name of add searching by attribute name of value");
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
                return getCheckResultWithError("There can't be space between tag name and ':' function start symbol", position,
                        "Remove space between tag name and ':' symbol");
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
        }

        if (isClosingElement(current)) {
            return getCheckResultWithError("There can't be an ']' without '[", position,
                    "Remove ']' after tag name or add to locator search by attribute name and value");
        } else if (isFunctionStartElement(current)) {
            return getCheckResultWithError("There can't be an ':' without function name", position,
                    "Remove ':' after tag name or add to locator search with function name");
        } else if (isOpeningBracesElement(current) || isClosingBracesElement(current)) {
            return getCheckResultWithError("There can't be an '(' or ')' without function name", position,
                    "Remove '(' or ')' tag class name or add to locator search with function name(nth-child)");
        } else if (isAttributeValueStrictlyEqualitySymbol(current)) {
            return getCheckResultWithError("There can't be an '='  without '[' and attribute name", position,
                    "Remove '='  after tag name. This symbol can be only when searching by attribute name or value(in [ braces)");
        } else if (isAttributeValueEndsWithSymbol(current)) {
            return getCheckResultWithError("There can't be an '$'  without '[' and attribute name", position,
                    "Remove '$'  after tag name. This symbol can be only when searching by attribute name or value(in [ braces)");
        } else if (isAttributeValueStartsWithSymbol(current)) {
            return getCheckResultWithError("There can't be an '^'  without '[' and attribute name", position,
                    "Remove '^'  after tag name. This symbol can be only when searching by attribute name or value(in [ braces)");
        } else if (isSingleQuotSymbol(current)) {
            return getCheckResultWithError("There can't be an ' without '[' and attribute name", position,
                    "Remove '[' after tag name of add searching by attribute name of value");
        } else {
            throw new NotParsebleSelectorException("Selector not parsed");
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
            return getCheckResultWithError("Selector can't contains only '['. There should be an attribute name", position,
                    "Add an attribute name after '[' symbol");
        }

        current = getCurrentChar(selector, position);
        if (isClassStartCharacter(current)) {
            return getCheckResultWithError("There is a . after '['", position, "Remove '.' after '[' symbol");
        } else if (isOpeningElement(current)) {
            return getCheckResultWithError("There is a [ after '['", position, "Remove '[' after '[' symbol");
        } else if (isIdStartCharacter(current)) {
            return getCheckResultWithError("There is a # after '['", position, "Remove '#' after '[' symbol");
        } else if (isOpeningBracesElement(current) || isClosingBracesElement(current)) {
            return getCheckResultWithError("There is a braces after '['", position, "Remove braces after '[' symbol");
        }
        String attributeName = parseSelectorAttributeName(selector, position);
        if (position.value() == selector.length()) {
            return getCheckResultWithError("Selector can't ends with attribute name. It should contain value and ']' symbol after.", position,
                    "Add an equality symbol(=,$=,^=,*=) and attribute value after attribute name");
        }
        current = getCurrentChar(selector, position);
        try {
            if (isWhitespace(current)) {
                skipWhitespaces(selector, position);
            }
        } catch (EndOfSelector endOfSelector) {
            return getCheckResultWithError("Selector can't ends with attribute name. It should contain value and ']' symbol after.", position,
                    "Add an equality symbol(=,$=,^=,*=) and attribute value after attribute name");
        }
        current = getCurrentChar(selector, position);
        if (!isAttributeValueStrictlyEqualitySymbol(current) && !isAttributeValueNotStrictlyEqualitySymbol(current)) {
            return getCheckResultWithError("Unexpected symbol after attribute name. There should be one of these after name :'=', '*', '^', '$'", position,
                    "Remove unexpected symbol and insert one of these symbols:\n1)'=' - attribute value equals\n2)'*=' - attribute value contains\n" +
                            "3)'^=' - attribute value starts with\n4)'$=' - attribute value ends with");
        }
        if (isAttributeValueNotStrictlyEqualitySymbol(current)) {
            try {
                current = getNextChar(selector, position);
            } catch (EndOfSelector endOfSelector) {
                return getCheckResultWithError("Selector can't ends with '*', '^' or '$'", position,
                        "Add '=', attribute value and '] after *,^ or $ symbol");
            }
        }
        if (!isAttributeValueStrictlyEqualitySymbol(current)) {
            return getCheckResultWithError("There must be an '=' after symbols '*', '^' or '$'", position,
                    "Add '=' after *,^ or $ symbol");
        }

        try {
            current = getNextChar(selector, position);
            if (isWhitespace(current)) {
                skipWhitespaces(selector, position);
            }
        } catch (EndOfSelector endOfSelector) {
            return getCheckResultWithError("Selector can't ends with '=' after attribute name. There must be a value", position,
                    "Add attribute value it single quotes after '=' symbol(if attribute is not 'name')");
        }
        current = getCurrentChar(selector, position);
        if (!isSingleQuotSymbol(current)) {
            if (!attributeName.equals(ATTRIBUTE_VALUE_NAME)) {
                return getCheckResultWithError("There must be a single quot after '=' if attribute not a '" + ATTRIBUTE_VALUE_NAME + "'", position,
                        "Add single quot after '=' symbol(if attribute is not 'name')");
            }
        } else {
            try {
                getNextChar(selector, position);
            } catch (EndOfSelector endOfSelector) {
                return getCheckResultWithError("Selector can't ends with ' symbol after '='. There must be an attribute value", position,
                        "Add an attribute value with closing single quot(if attribute is not 'name') and ']' symbol after");
            }
        }
        String attributeValue = parseSelectorAttributeValue(selector, position);
        if (attributeValue.isEmpty()) {
            return getCheckResultWithError("Attribute value can't be empty", position, "Add a value to attribute (between single quotes)");
        }
        if (position.value() == selector.length()) {
            return getCheckResultWithError("Selector can't ends attribute value. It should contain  close ']'(if attribute is '" + ATTRIBUTE_VALUE_NAME + "') or ' '] ' symbol after.", position,
                    "Add single closing quot(if attribute is not 'name') and closing ']' symbol after attribute value");
        }
        current = getCurrentChar(selector, position);
        if (!isSingleQuotSymbol(current)) {
            if (!attributeName.equals(ATTRIBUTE_VALUE_NAME)) {
                return getCheckResultWithError("There must be a single quot after attribute value if attribute not a '" + ATTRIBUTE_VALUE_NAME + "'", position,
                        "Add single quot after attribute value(if attribute is not 'name')");
            }

        } else {
            try {
                current = getNextChar(selector, position);
                if (isWhitespace(current)) {
                    skipWhitespaces(selector, position);
                }
            } catch (EndOfSelector endOfSelector) {
                return getCheckResultWithError("Selector can't ends with ' symbol after attribute value. It should have ']' symbol after", position,
                        "Add ']' after closing attribute value quot");
            }
        }
        current = getCurrentChar(selector, position);
        if (!isClosingElement(current)) {
            return getCheckResultWithError("There must be an a ']' after attribute value", position,
                    "Add ']' after closing attribute value ");
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
                return getCheckResultWithError("There can't be : after whitespace after ]. ", position,
                        "Remove ':' after ']' symbol or add locator for function after ':'");
            } else if (isClosingElement(current)) {
                return getCheckResultWithError("There can't be ] after whitespace after ]. ", position,
                        "Remove ']' after ']' symbol ");
            }
        } else {
            if (isClassStartCharacter(current)) {
                return getCheckResultWithError("There can't be . as next symbol after ']'", position, "Add space between ']' and '.'");
            } else if (isIdStartCharacter(current)) {
                return getCheckResultWithError("There can't be # as next symbol after ']'", position, "Add space between '#' and '.'");
            } else if (isTagNameStartCharacter(current)) {
                return getCheckResultWithError("There can't be tag name after symbol after ']' without space", position, "Add space between ']' and '.'");
            } else if (isFunctionStartElement(current)) {
                return parseFunction(selector, position);
            } else if (isClosingElement(current)) {
                return getCheckResultWithError("There can't be ] after ]. ", position,
                        "Remove ']' after ']' symbol ");
            }
        }
        return getSuccessCheckResult();
    }

    private boolean isSingleQuotSymbol(char ch) {
        return String.valueOf(ch).equals(CssSelectorSymbolConstants.ATTRIBUTE_VALUE_START_END_SYMBOL);
    }

    private boolean isAttributeValueNotStrictlyEqualitySymbol(char ch) {
        String value = String.valueOf(ch);
        return value.equals(CssSelectorSymbolConstants.ATTRIBUTE_VALUE_ENDS_WITH_SYMBOL) ||
                value.equals(CssSelectorSymbolConstants.ATTRIBUTE_VALUE_STARTS_WITH_SYMBOL) ||
                value.equals(CssSelectorSymbolConstants.ANY_CHARACTER_SYMBOL);
    }

    private boolean isAttributeValueStrictlyEqualitySymbol(char ch) {
        return String.valueOf(ch).equals(CssSelectorSymbolConstants.EQUAL_SYMBOL);
    }

    private boolean isAttributeValueEndsWithSymbol(char ch) {
        return String.valueOf(ch).equals(CssSelectorSymbolConstants.ATTRIBUTE_VALUE_ENDS_WITH_SYMBOL);
    }

    private boolean isAttributeValueStartsWithSymbol(char ch) {
        return String.valueOf(ch).equals(CssSelectorSymbolConstants.ATTRIBUTE_VALUE_STARTS_WITH_SYMBOL);
    }


    private CheckResult getSuccessCheckResult() {
        return new CheckResult(true, null);
    }

    private boolean isTagNameStartCharacter(char ch) {
        return Character.isLetter(ch);
    }

    private boolean isClassStartCharacter(char ch) {
        return String.valueOf(ch).equals(CssSelectorSymbolConstants.CLASS_SYMBOL);
    }

    private boolean isIdStartCharacter(char ch) {
        return String.valueOf(ch).equals(CssSelectorSymbolConstants.ID_SYMBOL);
    }

    private boolean isOpeningElement(char ch) {
        return String.valueOf(ch).equals(CssSelectorSymbolConstants.ATTRIBUTE_SELECTOR_PART_START_ELEMENT);
    }

    private boolean isClosingElement(char ch) {
        return String.valueOf(ch).equals(CssSelectorSymbolConstants.ATTRIBUTE_SELECTOR_PART_END_ELEMENT);
    }

    private boolean isAnyElementDigit(char ch) {
        return String.valueOf(ch).equals(CssSelectorSymbolConstants.ANY_CHARACTER_SYMBOL);
    }

    private boolean isFunctionStartElement(char ch) {
        return String.valueOf(ch).equals(CssSelectorSymbolConstants.FUNCTION_START_SYMBOL);
    }

    private boolean isStartElement(char ch) {
        return isTagNameStartCharacter(ch) || isClassStartCharacter(ch) || isIdStartCharacter(ch) ||
                isOpeningElement(ch) || isAnyElementDigit(ch);
    }


    private CheckResult getCheckResultWithError(String errorMessage, Position position, String fixVariant) {
        CheckResult checkResult = new CheckResult(false, errorMessage);
        checkResult.setPosition(position.value());
        checkResult.setFixVariant(fixVariant);
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
        return Character.isLetter(ch) || ch == '_' || Character.isDigit(ch) || ch == '-';
    }


    private static boolean isAttributeNamePartPart(char ch) {
        return isTagNamePart(ch) || ch == '-';
    }

    private static boolean isAttributeValuePart(char ch) {
        return isTagNamePart(ch) || isWhitespace(ch) || ch == '-';
    }

    private static boolean isOpeningBracesElement(char ch) {
        return String.valueOf(ch).equals(CssSelectorSymbolConstants.FUNCTION_INDEX_OPENING_SYMBOL);
    }

    private static boolean isClosingBracesElement(char ch) {
        return String.valueOf(ch).equals(CssSelectorSymbolConstants.FUNCTION_INDEX_CLOSING_SYMBOL);
    }


}
