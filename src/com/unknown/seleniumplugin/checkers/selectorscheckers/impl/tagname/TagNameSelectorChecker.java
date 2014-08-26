package com.unknown.seleniumplugin.checkers.selectorscheckers.impl.tagname;

import com.unknown.seleniumplugin.checkers.selectorscheckers.CheckResult;
import com.unknown.seleniumplugin.checkers.selectorscheckers.ISelectorChecker;
import com.unknown.seleniumplugin.checkers.selectorscheckers.exceptions.EndOfSelector;
import com.unknown.seleniumplugin.checkers.selectorscheckers.exceptions.NotParsebleSelectorException;
import com.unknown.seleniumplugin.checkers.selectorscheckers.impl.Position;

/**
 * Created by mike-sid on 14.08.14.
 */
public class TagNameSelectorChecker implements ISelectorChecker {


    @Override
    public CheckResult checkSelectorValid(String selector) throws NotParsebleSelectorException {
        Position position = new Position();
        if (selector.isEmpty()) {
            return getCheckResultWithError("Selector can't be empty", position, "Add an tag name value");
        }
        try {
            skipWhitespaces(selector, position);
        } catch (EndOfSelector e) {
            return getCheckResultWithError("Selector can't be empty", position, "Add an tag name value");
        }
        char current = getCurrentChar(selector, position);
        if(isTagNamePart(current)) {
            parseTagName(selector, position);
            if (position.value() == selector.length()) {
                return getSuccessCheckResult();
            }
            current = getCurrentChar(selector, position);
            if (isWhitespace(current)) {
                try {
                    skipWhitespaces(selector, position);
                    return getCheckResultWithError("There can't be any symbol after tag name and spaces", position,
                            "Delete all symbols after tag name");
                } catch (EndOfSelector endOfSelector) {
                    return getSuccessCheckResult();
                }
            }

        } else {
            return getCheckResultWithError("Tag name value not starts with valid symbol", position,
                    "Tag name can start only with number or letter");
        }

        return null;
    }

    @Override
    public String getName() {
        return "tagName";
    }

    private String parseTagName(String selector, Position position) {
        char next = getCurrentChar(selector, position);
        int startTagNamePosition = position.value();
        boolean idNameNotFinished = isTagNamePart(next);
        while (idNameNotFinished) {
            try {
                next = getNextChar(selector, position);
                idNameNotFinished = isTagNamePart(next);
            } catch (EndOfSelector e) {
                idNameNotFinished = false;
            }
        }
        int endTagNamePosition = position.value();
        return selector.substring(startTagNamePosition, endTagNamePosition);

    }

    private boolean isTagNamePart(char ch) {
        return Character.isJavaIdentifierPart(ch);
    }


    private void skipWhitespaces(String selector, Position position) throws EndOfSelector {
        while (isWhitespace(getCurrentChar(selector, position))) {
            getNextChar(selector, position);
        }
    }

    private static boolean isWhitespace(char ch) {
        return ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r';
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


    private CheckResult getCheckResultWithError(String errorMessage, Position position, String fixVariant) {
        CheckResult checkResult = new CheckResult(false, errorMessage);
        checkResult.setPosition(position.value());
        return checkResult;
    }

    private CheckResult getSuccessCheckResult() {
        return new CheckResult(true, null);
    }


}
