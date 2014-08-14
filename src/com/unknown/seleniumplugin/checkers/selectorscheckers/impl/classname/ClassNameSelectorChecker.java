package com.unknown.seleniumplugin.checkers.selectorscheckers.impl.classname;

import com.unknown.seleniumplugin.checkers.selectorscheckers.CheckResult;
import com.unknown.seleniumplugin.checkers.selectorscheckers.ISelectorChecker;
import com.unknown.seleniumplugin.checkers.selectorscheckers.exceptions.EndOfSelector;
import com.unknown.seleniumplugin.checkers.selectorscheckers.exceptions.NotParsebleSelectorException;
import com.unknown.seleniumplugin.checkers.selectorscheckers.impl.Position;

/**
 * Created by mike-sid on 14.08.14.
 */
public class ClassNameSelectorChecker implements ISelectorChecker {

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
        char current = getCurrentChar(selector, position);
        if(isClassNamePart(current)) {
            parseClassName(selector, position);
            if (position.value() == selector.length()) {
                return getSuccessCheckResult();
            }
            current = getCurrentChar(selector, position);
            if (isWhitespace(current)) {
                try {
                    skipWhitespaces(selector, position);
                    return getCheckResultWithError("There can't be any symbol after class name and spaces", position);
                } catch (EndOfSelector endOfSelector) {
                    return getSuccessCheckResult();
                }
            }

        } else {
            return getCheckResultWithError("Class name value not starts with valid symbol", position);
        }

        return null;
    }

    private String parseClassName(String selector, Position position) {
        char next = getCurrentChar(selector, position);
        int startClassNamePosition = position.value();
        boolean idNameNotFinished = isClassNamePart(next);
        while (idNameNotFinished) {
            try {
                next = getNextChar(selector, position);
                idNameNotFinished = isClassNamePart(next);
            } catch (EndOfSelector e) {
                idNameNotFinished = false;
            }
        }
        int endClassNamePosition = position.value();
        return selector.substring(startClassNamePosition, endClassNamePosition);

    }

    private boolean isClassNamePart(char ch) {
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


    private CheckResult getCheckResultWithError(String errorMessage, Position position) {
        CheckResult checkResult = new CheckResult(false, errorMessage);
        checkResult.setPosition(position.value());
        return checkResult;
    }

    private CheckResult getSuccessCheckResult() {
        return new CheckResult(true, null);
    }



}
