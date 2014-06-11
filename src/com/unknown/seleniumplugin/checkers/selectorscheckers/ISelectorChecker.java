package com.unknown.seleniumplugin.checkers.selectorscheckers;

import com.unknown.seleniumplugin.checkers.selectorscheckers.exceptions.NotParsebleSelectorException;

/**
 * Created by mike-sid on 30.04.14.
 */
public interface ISelectorChecker {

    /**
     * checks, is selector valid or not
     * @param selector selector string
     * @return {@link com.unknown.seleniumplugin.checkers.selectorscheckers.CheckResult} object.
     */
    CheckResult checkSelectorValid(String selector) throws NotParsebleSelectorException;
}
