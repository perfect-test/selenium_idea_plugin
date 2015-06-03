package com.unknown.seleniumplugin.checkers.selectorscheckers.impl.xpath.tests;

import com.unknown.seleniumplugin.checkers.selectorscheckers.CheckResult;
import com.unknown.seleniumplugin.checkers.selectorscheckers.ISelectorChecker;
import com.unknown.seleniumplugin.checkers.selectorscheckers.exceptions.NotParsebleSelectorException;
import com.unknown.seleniumplugin.checkers.selectorscheckers.impl.css.CssSelectorChecker;
import com.unknown.seleniumplugin.checkers.selectorscheckers.impl.xpath.XpathSelectorChecker;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import static org.testng.Assert.assertTrue;

/**
 * Created by mike-sid on 20.08.14.
 */
public class XpathSelectorTests {

    private ISelectorChecker selectorChecker = new XpathSelectorChecker();

    @DataProvider
    public Object[][] badSelectors() {
        return new Object[][]{
//                {"*:nth-child"},
//                {"//acronym[@accept-charset='acronym"}
                {"//class[accept"}
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
                {"//BBB[23]"},
                {"//acronym[not(d)]"},
                {"following-sibling::label"},
                {"//form[@style][child::div[@class='page__b-offers__guru']]"},
                {"//preceding::span[text()='все'][preceding-sibling::*[text()='Все производители']]"},
                {"//*[@class='b-gurufilters__filter'][descendant::*[text()='Цена']]//input[1]"},
                {".//a"},
                {"*"},
                {".//table[@class='object_table']//tr[child::*/text()='Улица:']/td[2]"},
                {".//ul[contains(@class,'ui-multiselect-checkboxes') and parent::*[contains(@style, 'display: block')]]"},
                {".//*[@id='sideRight']/div[count(descendant::*[contains(text(),'Брокер онлайн')]) > 0]"},
                {".//*[@id='content']//input | .//*[@id='content']//select"},
                {".//*[@class='ad_div']//table[child::*//*[contains(text(),'Отменить')]]"},
                {".//div[@class='phone_number'][text()!='']"}

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
