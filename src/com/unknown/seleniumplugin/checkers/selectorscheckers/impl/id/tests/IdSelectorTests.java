package com.unknown.seleniumplugin.checkers.selectorscheckers.impl.id.tests;

import com.unknown.seleniumplugin.checkers.selectorscheckers.CheckResult;
import com.unknown.seleniumplugin.checkers.selectorscheckers.ISelectorChecker;
import com.unknown.seleniumplugin.checkers.selectorscheckers.exceptions.NotParsebleSelectorException;
import com.unknown.seleniumplugin.checkers.selectorscheckers.impl.id.IDSelectorChecker;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

/**
 * Created by mike-sid on 17.08.15.
 */
public class IdSelectorTests {

    private ISelectorChecker selectorChecker;

    @BeforeTest
    public void beforeTest() {
        selectorChecker = new IDSelectorChecker();
    }

    @DataProvider
    public Object[][] badSelectors() {
        return new Object[][]{
                {"#mailbox__auth__button"},

        };
    }

    @DataProvider
    public Object[][] goodSelectors() {
        return new Object[][]{
                {"query-button"},



        };
    }

    @Test(dataProvider = "badSelectors")
    public void testErrorSelector(String selector) throws NotParsebleSelectorException {
        CheckResult checkResult = selectorChecker.checkSelectorValid(selector);
        System.out.println("selector : " + selector + " ; error :" + checkResult.getMessage());
        assertTrue(!checkResult.isResultSuccess(), "No error for selector :'" + selector + "'");
    }

    @Test(dataProvider = "goodSelectors")
    public void testGoodSelector(String selector) throws NotParsebleSelectorException {
        CheckResult checkResult = selectorChecker.checkSelectorValid(selector);
        assertTrue(checkResult.isResultSuccess(), "Error for selector :'" + selector + "' ; error : " + checkResult.getMessage());
    }
}
