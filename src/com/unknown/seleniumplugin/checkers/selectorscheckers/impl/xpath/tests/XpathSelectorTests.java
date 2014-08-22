package com.unknown.seleniumplugin.checkers.selectorscheckers.impl.xpath.tests;

import com.unknown.seleniumplugin.checkers.selectorscheckers.CheckResult;
import com.unknown.seleniumplugin.checkers.selectorscheckers.ISelectorChecker;
import com.unknown.seleniumplugin.checkers.selectorscheckers.exceptions.NotParsebleSelectorException;
import com.unknown.seleniumplugin.checkers.selectorscheckers.impl.css.CssSelectorChecker;
import com.unknown.seleniumplugin.checkers.selectorscheckers.impl.xpath.XpathSelectorChecker;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

/**
 * Created by mike-sid on 20.08.14.
 */
public class XpathSelectorTests {

    private ISelectorChecker selectorChecker = new XpathSelectorChecker();

    @DataProvider
    public Object[][] badSelectors() {
        return new Object[][]{
//                {"*:nth-child"}
        };
    }

    @DataProvider
    public Object[][] goodSelectors() {
        return new Object[][]{
                {"//home"},
                {"//home/dic"},
                {"//home/dic"},
                {"//BBB[@id='b1']"},
                {"//BBB[@name ='bbb']"},
                {"//BBB[normalize-space(@name) = 'bbb']"},
                {"//*[count(*) = 3]"},
                {"//*[count(BBB)= 2]"},
                {"//BBB[@id]"},
                {"//BBB[23]"}
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
        assertTrue(checkResult.isResultSuccess(), "No error for selector :'" + selector + "' ; error : " + checkResult.getMessage());
    }


}
