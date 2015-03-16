package com.unknown.seleniumplugin.checkers.selectorscheckers.impl.xpath;

import com.unknown.seleniumplugin.checkers.selectorscheckers.CheckResult;
import com.unknown.seleniumplugin.checkers.selectorscheckers.ISelectorChecker;
import com.unknown.seleniumplugin.checkers.selectorscheckers.exceptions.EndOfSelector;
import com.unknown.seleniumplugin.checkers.selectorscheckers.exceptions.NotParsebleSelectorException;
import com.unknown.seleniumplugin.checkers.selectorscheckers.impl.Position;
import com.unknown.seleniumplugin.codecomplete.properties.SeleniumPropertiesReader;
import com.unknown.seleniumplugin.domain.XPathSelectorSymbolConstants;

import java.util.List;

/**
 * Created by mike-sid on 18.08.14.
 */
public class XpathSelectorChecker implements ISelectorChecker {


    private static List<String> functionNames = SeleniumPropertiesReader.getXpathFunctions();
    private static List<String> equalityFunctionNames = SeleniumPropertiesReader.getXpathEqualityFunctions();
    private static List<String> axisFunctions = SeleniumPropertiesReader.getAxis();
    private static char[] functionNamesElementsDictionary;
    private static boolean isAxisNow = false;


    static {
        StringBuilder functionNamesString = new StringBuilder();
        for (String functionName : functionNames) {
            functionNamesString.append(functionName);
        }
        for (String functionName : axisFunctions) {
            functionNamesString.append(functionName);
        }
        functionNamesElementsDictionary = functionNamesString.toString().toCharArray();
    }

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
        if (isStartStep(next)) {
            try {
                next = getNextChar(selector, position);
                if (isStartStep(next)) {
                    return parseStep(selector, position);
                } else {
                    return getCheckResultWithError("Wrong symbol after first '/' in locator", position, "Delete wrong symbol or add second '/' to the start of locator");
                }
            } catch (EndOfSelector endOfSelector) {
                return getCheckResultWithError("Locator can't contains only '/' symbol.", position, "Add second '/' and locator value");
            }
        } else if (isTagNameStartCharacter(next)) {
            return parseTagNameElement(selector, position);
        } else if (isDot(next)) {
            try {
                next = getNextChar(selector, position);
                if (isStartStep(next)) {
                    return parseStep(selector, position);
                } else {
                    return getCheckResultWithError("Wrong symbol after first '.' in locator", position,
                            "Delete wrong symbol or add  '/' after '.'");
                }
            } catch (EndOfSelector endOfSelector) {
                return getCheckResultWithError("Locator can't contains only '/' symbol.", position, "Add second '/' and locator value");
            }
        } else if (isAnyElement(next)) {
            try {
                next = getNextChar(selector, position);
                if (isStartStep(next)) {
                    return parseStep(selector, position);
                } else {
                    return getCheckResultWithError("Wrong symbol after '*' ", position,
                            "Add '/' and next child description or '[' and predicate after '*'");
                }
            } catch (EndOfSelector endOfSelector) {
                return getSuccessCheckResult();
            }
        } else {
            return getCheckResultWithError("Locator starts not with '//' or '.' or '*' symbols.", position, "Add '//' or '.' or '*' to the locator start");
        }
    }

    private boolean isDot(char next) {
        return next == '.';
    }

    private CheckResult parseAxis(String selector, Position position) throws NotParsebleSelectorException {
        char current = getCurrentChar(selector, position);
        if (isAfterAxisElement(current)) {
            try {
                getNextChar(selector, position);
            } catch (EndOfSelector endOfSelector) {
                return getCheckResultWithError("Locator can't ends with single ':' after axis name.", position, "Add second ':' or remove first ':'");
            }
            try {
                current = getNextChar(selector, position);
            } catch (EndOfSelector endOfSelector) {
                return getCheckResultWithError("Locator can't ends with '::'. There must be a value after.", position, "Add value after ::");
            }
            if (isStartStep(current)) {
                return parseStep(selector, position);
            } else if (isTagNameStartCharacter(current)) {
                return parseTagNameElement(selector, position);
            } else if (isAnyElement(current)) {
                try {
                    current = getNextChar(selector, position);
                    if (isStartAttributeCheckSymbol(current)) {
                        return parseAttributes(selector, position);
                    } else if (isTagNameStartCharacter(current)) {
                        return parseTagNameElement(selector, position);
                    } else {
                        return getCheckResultWithError("Wrong symbol after '*' ", position,
                                "Add '/' and next child description or '[' and predicate after '*'");
                    }
                } catch (EndOfSelector endOfSelector) {
                    return getSuccessCheckResult();
                }
            } else {
                return getCheckResultWithError("There must be a a tag name or attributes or * or axis name after ':'", position, "Add tag name or axis name or * or '[' after ':'");
            }
        } else {
            return getCheckResultWithError("Wrong symbol after axis - there must be ':'", position, "Add :: after axis");
        }
    }

    private boolean isAfterAxisElement(char current) {
        return current == ':';
    }

    private boolean isAxisName(String name) {
        return axisFunctions.contains(name);
    }

    @Override
    public String getName() {
        return "xpath";
    }

    private CheckResult parseTagNameElement(String selector, Position position) throws NotParsebleSelectorException {
        String name = parseTagName(selector, position);
        if (position.value() == selector.length()) {
            return getSuccessCheckResult();
        }
        if (isAxisName(name)) {
            return parseAxis(selector, position);
        } else {
            char next = getCurrentChar(selector, position);
            if (isStartAttributeCheckSymbol(next)) {
                return parseAttributes(selector, position);
            } else if (isStartStep(next)) {
                return parseStep(selector, position);
            } else if (isEndAttributeCheckSymbol(next)) {
                try {
                    getNextChar(selector, position);
                } catch (EndOfSelector endOfSelector) {
                    return getSuccessCheckResult();
                }
                return getSuccessCheckResult();
            } else {
                return getCheckResultWithError("Wrong symbol after tag name ", position,
                        "Add '/' and next child description or '[' and predicate after tag name");
            }
        }
    }

    private CheckResult parseStep(String selector, Position position) throws NotParsebleSelectorException {
        char next;
        try {
            next = getNextChar(selector, position);
        } catch (EndOfSelector endOfSelector) {
            return getCheckResultWithError("Locator can't ends with '/'.", position, "Add tag name or '*' after '/'");
        }
        if (isAnyElement(next)) {
            try {
                next = getNextChar(selector, position);
                if (isStartAttributeCheckSymbol(next)) {
                    return parseAttributes(selector, position);
                } else if (isTagNameStartCharacter(next)) {
                    return parseTagNameElement(selector, position);
                } else {
                    return getCheckResultWithError("Wrong symbol after '*' ", position,
                            "Add '/' and next child description or '[' and predicate after '*'");
                }
            } catch (EndOfSelector endOfSelector) {
                return getSuccessCheckResult();
            }
        } else if (isTagNameStartCharacter(next)) {
            return parseTagNameElement(selector, position);
        } else if (isStartStep(next)) {
            return parseStep(selector, position);
        } else {
            throw new NotParsebleSelectorException("Selector not parsed");
        }
    }

    private CheckResult parseAttributes(String selector, Position position) throws NotParsebleSelectorException {
        char next;
        try {
            next = getNextChar(selector, position);
        } catch (EndOfSelector endOfSelector) {
            return getCheckResultWithError("Locator can't ends with '[' symbol. There must be '@' function", position,
                    "Add '@' and tag name or function name after '[' symbol");
        }
        if (isTagPredicateSymbol(next)) {
            try {
                next = getNextChar(selector, position);
            } catch (EndOfSelector endOfSelector) {
                return getCheckResultWithError("Locator can't ends with @. There must be tag name", position,
                        "Add attribute name or '*' and ']' symbols after '@'");
            }
            if (isAttributeNameStartSymbol(next)) {
                String attributeName = parseAttributeName(selector, position);
                if (attributeName.isEmpty()) {
                    return getCheckResultWithError("Attribute name can't be empty", position,
                            "Add attribute name after '@'");
                }
                if (position.value() == selector.length()) {
                    return getCheckResultWithError("Locator can't ends with attribute name. There must be '=' and value after", position,
                            "Add '=' and attribute value after attribute name");
                }
                try {
                    skipWhitespaces(selector, position);
                } catch (EndOfSelector endOfSelector) {
                    return getCheckResultWithError("Locator can't ends with attribute name. There must be '=' and value after", position,
                            "Add '=' and attribute value after attribute name");

                }
                next = getCurrentChar(selector, position);
                if (isEqualsSymbol(next)) {
                    try {
                        next = getNextChar(selector, position);
                        skipWhitespaces(selector, position);
                    } catch (EndOfSelector endOfSelector) {
                        return getCheckResultWithError("Locator can't ends with '=' symbol. There must be an a value", position,
                                "Add ' and attribute value for check after '=' symbol");
                    }
                    next = getCurrentChar(selector, position);

                    if (isSingleQuot(next)) {
                        try {
                            getNextChar(selector, position);
                        } catch (EndOfSelector endOfSelector) {
                            return getCheckResultWithError("Locator can't ends with single quot without attribute value", position,
                                    "Add attribute value after opening quot. ");
                        }
                        String attributeValue = parseAttributeValue(selector, position);
                        if (attributeValue.isEmpty()) {
                            return getCheckResultWithError("Attribute value can't be empty", position, "Add attribute value");
                        }
                        if (position.value() == selector.length()) {
                            return getCheckResultWithError("There must be single quot after attribute value", position,
                                    "Add single quot and ']' after attribute value");
                        }
                        next = getCurrentChar(selector, position);
                        if (!isSingleQuot(next)) {
                            return getCheckResultWithError("There must be single quot after attribute value", position,
                                    "Add closing single quot after attribute value");
                        }
                        try {
                            next = getNextChar(selector, position);
                            skipWhitespaces(selector, position);
                        } catch (EndOfSelector endOfSelector) {
                            return getCheckResultWithError("Locator can't ends with closing attribute value single quot. There must be ']' after", position,
                                    "Add ']' after closing single quot");
                        }
                        next = getCurrentChar(selector, position);
                        if (!isEndAttributeCheckSymbol(next)) {
                            return getCheckResultWithError("There must be a ']' after attribute value", position,
                                    "Add closing ']' after attribute value");
                        }
                        try {
                            getNextChar(selector, position);
                        } catch (EndOfSelector endOfSelector) {
                            return getSuccessCheckResult();
                        }
                    } else {
                        return getCheckResultWithError("After '=' must be a single quot", position,
                                "Add single quot and attribute value after '='");
                    }
                } else if (isEndAttributeCheckSymbol(next)) {
                    try {
                        getNextChar(selector, position);
                    } catch (EndOfSelector endOfSelector) {
                        return getSuccessCheckResult();
                    }
                } else {
                    return getCheckResultWithError("There must be an '=' or ']' after attribute name in locator", position,
                            "Add '=' or ']' after attribute name");
                }
            }
        } else if (isFunctionStartSymbol(next)) {
            String functionName = parseFunctionName(selector, position);
            if (isAxisName(functionName)) {
                CheckResult checkResult = parseAxis(selector, position);
                if (!checkResult.isResultSuccess()) {
                    return checkResult;
                }
                if (position.value() == selector.length()) {
                    return getSuccessCheckResult();
                }
                next = getCurrentChar(selector, position);
                if (isEndAttributeCheckSymbol(next)) {
                    try {
                        getNextChar(selector, position);
                    } catch (EndOfSelector endOfSelector) {
                        return getSuccessCheckResult();
                    }
                } else if (isAfterAxisElement(next)) {
                    return parseAxis(selector, position);
                }
            } else {
                if (!functionNames.contains(functionName)) {
                    return getCheckResultWithError("Function name '" + functionName + "' is not valid. ", position,
                            "Change the function name. It could be one of (" + functionNames + ")");
                }
                if (position.value() == selector.length()) {
                    return getCheckResultWithError("Locator can't ends with function name. There must be an a parameters in braces", position,
                            "Add braces and parameters after function name");
                }
                next = getCurrentChar(selector, position);
                if (!isOpeningBracesSymbol(next)) {
                    return getCheckResultWithError("Wrong symbol after function name. There must be opening braces", position,
                            "Add '(' and parameters list after function name");
                }
                try {
                    parseFunctionParameters(selector, position);
                } catch (EndOfSelector endOfSelector) {
                    return getCheckResultWithError("Locator parameters list has not closed braces", position,
                            "Check parameters list for function. Not all braces closes");
                }
                try {
                    next = getNextChar(selector, position);
                } catch (EndOfSelector endOfSelector) {
                    return getCheckResultWithError("Locator can't ends with closing brace of parameters list. There must be ']' or attribute value", position,
                            "Add '] or '=' with attribute value after ')");
                }
                try {
                    skipWhitespaces(selector, position);
                } catch (EndOfSelector endOfSelector) {
                    return getCheckResultWithError("Locator can't ends with closing brace of parameters list. There must be ']' or attribute value", position,
                            "Add '] or '=' with attribute value after ')");
                }
                next = getCurrentChar(selector, position);
                if (equalityFunctionNames.contains(functionName)) {
                    if (isNotStrongEqualitySymbol(next)) {
                        try {
                            next = getNextChar(selector, position);
                        } catch (EndOfSelector endOfSelector) {
                            return getCheckResultWithError("Locator can't ends with '>' or '<'. There must be '=' after", position, "Add '=' after ");
                        }
                        if (!isEqualsSymbol(next)) {
                            return getCheckResultWithError("There must be an '=' after '>' or '<'. There must be '=' after", position, "Add '=' after ");
                        }
                        try {
                            skipWhitespaces(selector, position);
                        } catch (EndOfSelector endOfSelector) {
                            return getCheckResultWithError("Locator can't ends with '=' symbol. There must be value after ", position, "Add parameter value in  after '='");
                        }
                        try {
                            getNextChar(selector, position);
                        } catch (EndOfSelector endOfSelector) {
                            return getCheckResultWithError("Locator can't ends with '=' symbol. There must be value after ", position, "Add parameter value in  after '='");
                        }
                    } else if (isEqualsSymbol(next)) {
                        try {
                            skipWhitespaces(selector, position);
                        } catch (EndOfSelector endOfSelector) {
                            return getCheckResultWithError("Locator can't ends with '=' symbol.  There must be value after", position, "Add parameter value in  after '='");
                        }
                        try {
                            getNextChar(selector, position);
                        } catch (EndOfSelector endOfSelector) {
                            return getCheckResultWithError("Locator can't ends with '=' symbol.  There must be value after", position, "Add parameter value in  after '='");
                        }
                    } else {
                        return getCheckResultWithError("There must be '=' or '>' or '<' after function parameters list(if function one of " +
                                        equalityFunctionNames + "", position,
                                "Add '=' or '>' or '<' after function parameters list");
                    }
                    next = getCurrentChar(selector, position);
                    boolean inQuotes = false;
                    if (isSingleQuot(next)) {
                        inQuotes = true;
                        try {
                            getNextChar(selector, position);
                        } catch (EndOfSelector endOfSelector) {
                            return getCheckResultWithError("Locator can't ends with single quot. There must be ']' after value in quotes", position,
                                    "Add value after single quot");
                        }
                    }
                    String attributeValue = parseFunctionAttributeValue(selector, position, inQuotes);
                    if (attributeValue.isEmpty()) {
                        return getCheckResultWithError("Attribute value can't be empty", position, "Add attribute value");
                    }
                    if (position.value() == selector.length()) {
                        return getCheckResultWithError("Locator can't ends with attribute value. There must be single quot or ']' ", position,
                                "Add single quot(if starting single quot exist) or ']' after attribute value");
                    }
                    try {
                        next = getNextChar(selector, position);
                    } catch (EndOfSelector endOfSelector) {
                        return getCheckResultWithError("Locator can't ends with attribute value. There must be single quot or ']' ", position,
                                "Add single quot(if starting single quot exist) or ']' after attribute value");
                    }
                    if (inQuotes) {
                        if (!isSingleQuot(next)) {
                            return getCheckResultWithError("There must be closing single quot after attribute value", position,
                                    "Add closing single quot after attribute value");
                        }
                        try {
                            next = getNextChar(selector, position);
                        } catch (EndOfSelector endOfSelector) {
                            return getCheckResultWithError("Locator can't ends with closing single quot without ']' symbol", position,
                                    "Add ']' after closing single quot");
                        }
                    }
                    if (!isEndAttributeCheckSymbol(next)) {
                        return getCheckResultWithError("Wrong element after attribute value. There must be ']' after ", position,
                                "After attribute value should be an ']' symbol");
                    }
                    try {
                        getNextChar(selector, position);
                    } catch (EndOfSelector endOfSelector) {
                        return getSuccessCheckResult();
                    }
                } else if (isEndAttributeCheckSymbol(next)) {
                    return getSuccessCheckResult();
                }
            }
        } else if (isIndexSymbol(next)) {
            parseIndex(selector, position);
            if (position.value() == selector.length()) {
                return getCheckResultWithError("Locator can't ends with index value. There must be ']' after", position,
                        "Add ']' after index value");
            }
            next = getCurrentChar(selector, position);
            if (!isEndAttributeCheckSymbol(next)) {
                return getCheckResultWithError("Wrong symbol after index end.  There must be ']' after ", position, "After index must be an a ']' symbol. Delete wrong");
            }
            try {
                getNextChar(selector, position);
            } catch (EndOfSelector endOfSelector) {
                return getSuccessCheckResult();
            }
        } else if (isEndAttributeCheckSymbol(next)) {
            return getCheckResultWithError("There must be an @ or function name or index after '['", position, "Add @ or tag name or index after '['");
        }
        char current = getCurrentChar(selector, position);

        if (isStartStep(current)) {
            return parseStep(selector, position);
        } else if (isStartAttributeCheckSymbol(current)) {
            return parseAttributes(selector, position);
        } else if (isEndAttributeCheckSymbol(current)) {
            return getSuccessCheckResult();
        } else {
            return getCheckResultWithError("Wrong symbol after attributes", position,
                    "After attributes there can be only '[' with new attribute value specifying or '/' for new step");
        }
    }


    private boolean isNotStrongEqualitySymbol(char ch) {
        return ch == '<' || ch == '>';
    }

    private void parseFunctionParameters(String selector, Position position) throws EndOfSelector {
        int openingBracesCount = 1;
        int closingBracesCount = 0;
        while (openingBracesCount != closingBracesCount) {
            char next = getNextChar(selector, position);
            if (isOpeningBracesSymbol(next)) {
                openingBracesCount++;
            } else if (isClosingBracesSymbol(next)) {
                closingBracesCount++;
            }
        }
    }

    private String parseIndex(String selector, Position position) {
        char next = getCurrentChar(selector, position);
        int startValuePosition = position.value();
        boolean valueNotFinished = isIndexSymbol(next);
        while (valueNotFinished) {
            try {
                next = getNextChar(selector, position);
                valueNotFinished = isIndexSymbol(next);
            } catch (EndOfSelector e) {
                valueNotFinished = false;
            }
        }
        int endValuePosition = position.value();
        return selector.substring(startValuePosition, endValuePosition);


    }

    private boolean isIndexSymbol(char ch) {
        return Character.isDigit(ch);
    }


    private boolean isSingleQuot(char ch) {
        return ch == '\'';
    }

    private boolean isEqualsSymbol(char ch) {
        return String.valueOf(ch).equals(XPathSelectorSymbolConstants.EQUAL_SYMBOL);
    }

    private boolean isFunctionStartSymbol(char ch) {
        for (String functionName : functionNames) {
            if (functionName.startsWith(String.valueOf(ch))) {
                return true;
            }
        }
        for (String functionName : axisFunctions) {
            if (functionName.startsWith(String.valueOf(ch))) {
                return true;
            }
        }
        return false;
    }

    private boolean isTagPredicateSymbol(char ch) {
        return String.valueOf(ch).equals(XPathSelectorSymbolConstants.ATTRIBUTE_NAME_START_SYMBOL);
    }

    private boolean isStartAttributeCheckSymbol(char ch) {
        return String.valueOf(ch).equals(XPathSelectorSymbolConstants.ATTRIBUTE_SELECTOR_PART_START_ELEMENT);
    }

    private boolean isEndAttributeCheckSymbol(char ch) {
        return String.valueOf(ch).equals(XPathSelectorSymbolConstants.ATTRIBUTE_SELECTOR_PART_END_ELEMENT);
    }

    private boolean isOpeningBracesSymbol(char ch) {
        return String.valueOf(ch).equals(XPathSelectorSymbolConstants.OPENING_BRACE_ELEMENT);
    }

    private boolean isClosingBracesSymbol(char ch) {
        return String.valueOf(ch).equals(XPathSelectorSymbolConstants.CLOSING_BRACE_ELEMENT);
    }

    private boolean isTagNameStartCharacter(char ch) {
        return Character.isLetter(ch);
    }

    private boolean isAnyElement(char ch) {
        return ch == '*';
    }

    private boolean isStartStep(char ch) {
        return String.valueOf(ch).equals(XPathSelectorSymbolConstants.START_STEP_SYMBOL);
    }

    private CheckResult getSuccessCheckResult() {
        return new CheckResult(true, null);
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

    private String parseTagName(String selector, Position position) {
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

    private String parseAttributeName(String selector, Position position) {
        char next = getCurrentChar(selector, position);
        int startNamePosition = position.value();
        boolean attributeNameNotFinished = isAttributeNamePartPart(next);
        while (attributeNameNotFinished) {
            try {
                next = getNextChar(selector, position);
                attributeNameNotFinished = isAttributeNamePartPart(next);
            } catch (EndOfSelector e) {
                attributeNameNotFinished = false;
            }
        }
        int endNamePosition = position.value();
        return selector.substring(startNamePosition, endNamePosition);
    }


    private String parseAttributeValue(String selector, Position position) {
        char next = getCurrentChar(selector, position);
        int startValuePosition = position.value();
        boolean attributeValueNotFinished = !isSingleQuot(next);
        while (attributeValueNotFinished) {
            try {
                next = getNextChar(selector, position);
                attributeValueNotFinished = !isSingleQuot(next);
            } catch (EndOfSelector e) {
                attributeValueNotFinished = false;
            }
        }
        int endValuePosition = position.value();
        return selector.substring(startValuePosition, endValuePosition);
    }

    private String parseFunctionAttributeValue(String selector, Position position, boolean inQuotes) {
        char next = getCurrentChar(selector, position);
        int startValuePosition = position.value();
        boolean attributeValueNotFinished;
        if (inQuotes) {
            attributeValueNotFinished = !isSingleQuot(next);
        } else {
            attributeValueNotFinished = !isEndAttributeCheckSymbol(next);
        }
        while (attributeValueNotFinished) {
            try {
                next = getNextChar(selector, position);
                if (inQuotes) {
                    attributeValueNotFinished = !isSingleQuot(next);
                } else {
                    attributeValueNotFinished = !isEndAttributeCheckSymbol(next);
                }
            } catch (EndOfSelector e) {
                attributeValueNotFinished = false;
            }
        }
        int endValuePosition = position.value();
        position.decrement();
        return selector.substring(startValuePosition, endValuePosition);
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

    private boolean isFunctionNamePart(char ch) {
        for (char functionNameLetter : functionNamesElementsDictionary) {
            if (ch == functionNameLetter) {
                return true;
            }
        }
        return false;
    }


    private static boolean isAttributeNamePartPart(char ch) {
        return isTagNamePart(ch) || ch == '-';
    }

    private static boolean isAttributeNameStartSymbol(char ch) {
        return Character.isLetter(ch);
    }

    private static boolean isTagNamePart(char ch) {
        return Character.isLetter(ch) || ch == '_' || Character.isDigit(ch) || ch == '-';
    }


}
