package com.unknown.seleniumplugin.elementscheckers.existancechecker.backend;

import com.unknown.seleniumplugin.domain.SelectorMethodValue;
import com.unknown.seleniumplugin.utils.HttpUtils;
import org.apache.http.HttpResponse;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by mike-sid on 07.08.14.
 */
public class WebDriverChecker {

    public static CheckElementExistenceResult checkElementExist(String urlTextFieldText, String locatorValueTextFieldText,
                                                                String locatorMethodTextFieldText, String phantomJsFieldValue) {
        WebDriver driver = null;
        try {
            SelectorMethodValue selectorMethodValue = SelectorMethodValue.getByText(locatorMethodTextFieldText);
            if (selectorMethodValue == null) {
                return new CheckElementExistenceResult("Selector value not valid", false);
            } else {
                DesiredCapabilities capabilities = new DesiredCapabilities();
                capabilities.setCapability("phantomjs.binary.path", phantomJsFieldValue);
                driver = new PhantomJSDriver(capabilities);
                driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
                driver.get(urlTextFieldText);
                System.out.println(driver.getPageSource());
                By by = null;
                switch (selectorMethodValue) {
                    case CSS:
                        by = By.cssSelector(locatorValueTextFieldText);
                        break;
                    case XPATH:
                        by = By.xpath(locatorValueTextFieldText);
                        break;
                    case ID:
                        by = By.id(locatorValueTextFieldText);
                        break;
                    case CLASS_NAME:
                        by = By.className(locatorValueTextFieldText);
                        break;
                    case NAME:
                        by = By.name(locatorValueTextFieldText);
                        break;
                    default:
                        by = By.cssSelector(locatorValueTextFieldText);
                        break;
                }
                List<WebElement> webElements = driver.findElements(by);
                System.out.println("Size of elements : " + webElements.size());
                CheckElementExistenceResult result = new CheckElementExistenceResult();
                if (!webElements.isEmpty()) {
                    result.setFound(true);
                    result.setElementsCount(webElements.size());
                }
                driver.quit();
                return result;
            }
        } catch (Exception e) {
            System.out.println("Exception occurred");
            e.printStackTrace();
            if (driver != null) {
                driver.quit();
            }
            return new CheckElementExistenceResult(e.getMessage(), false);
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
//        return new CheckElementExistenceResult(null, false);
    }

    public static void main(String[] args){
        phantomJsInstalled();
    }

    private static boolean phantomJsInstalled() {

        return true;
    }
}
