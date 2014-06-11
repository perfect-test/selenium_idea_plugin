package com.unknown.seleniumplugin.checkers.selectorscheckers.impl.css.tests;

import com.unknown.seleniumplugin.checkers.selectorscheckers.CheckResult;
import com.unknown.seleniumplugin.checkers.selectorscheckers.ISelectorChecker;
import com.unknown.seleniumplugin.checkers.selectorscheckers.exceptions.NotParsebleSelectorException;
import com.unknown.seleniumplugin.checkers.selectorscheckers.impl.css.CssSelectorChecker;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

/**
 * Created by mike-sid on 06.05.14.
 */
public class CssSelectorTests {

    private ISelectorChecker selectorChecker;

    @BeforeTest
    public void beforeTest() {
        selectorChecker = new CssSelectorChecker();
    }

    @DataProvider
    public Object[][] badSelectors() {
        return new Object[][]{
                {"# mailbox__auth__button"},
                {"#mailbox__auth__button#tagName"},
                {"#.className"},
                {"#[className='black']"},
                {"#div[   "},
                {"#div[   class "},
                {"#div[   class ="},
                {"#div[   class =  "},
                {"#div[   class =  '"},
                {"#div[   class =  'mailbox_ _register"},
                {"#div[   class =  'mailbox__register'    "},
                {"#div[   class =  'mailbox__register]"},
                {"#div[.   class =  'mailbox__register]"},
                {"#div[#   class =  'mailbox__register]"},
                {"#div[   class =  'mailbox__register'].className"},
                {"#div[   class =  'mailbox__register']#dix"},
                {"div[   class =  '']#dix"},
                {"div[x"},
                {"div[x='']a"},
                {"div[x=''] a["},
                {"div[x=''] a[a"},
                {"div[x=''] a[a="},
                {"div[x=''] a[a'"},
                {"div[x=''] a[a='"},
                {"div[x=''] a[a=''"},
                {"div[x=''] a[a=']"},
        };
    }

    @DataProvider
    public Object[][] goodSelectors() {
        return new Object[][]{
                {"#mailbox.className"},
                {"#mailbox[class='aa']"},
                {"#mailbox .classBox"},
                {"#mailbox [class='className']"},
                {"#div[   class =  'mailbox__register'    ]"},
                {"#div[   name =  'mailbox__register'    ]"},
                {"#div[   name =  mailbox__register    ]"},
                {"#div[   attributename =  'mailbox_     _adf asf asdf adf adf register']         "},
                {"#mailbox tagName[class='']"},
                {"#mailbox tagName.className"},
                {"#mailbox__auth__button"},
                {"#mailbox #innerbox"},
                {"#div[   class =  'mailbox__register'][class='sasd']"},
                {"#div[   class =  'mailbox__register'] .className"},
                {"#div[   class =  'mailbox__register'] #dic"},
                {"#div[   class =  '']"},
                {"[class='adsfas '] [asdf='dsds']"},
                {"div[class*='b-review-auth b-island']"},
                {"div.b-dropdowna_action_placement span.b-form-button"},
                {".b-grid__total .b-grid__cell_type_cost .b-grid__cell-text:first-child"},
                {".b-grid__total .b-grid__cell_type_cost .b-grid__cell-text:last-child"},
                {".b-grid__total .b-grid__cell_type_show .b-grid__cell-text:first-child"},
                {".b-grid__total .b-grid__cell_type_show .b-grid__cell-text:nth-child(2)"},
                {".b-grid__total .b-grid__cell_type_show .b-grid__cell-text:last-child"},


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
