package org.qecamp.zalenium;

import java.net.MalformedURLException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.ITestContext;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Jeeva Kandasamy (jkandasa)
 */
@Slf4j
public abstract class TestAbstract {
    protected RemoteWebDriver webDriver = null;
    protected static final long WAIT_TIME = 1000L * 5;

    @BeforeTest
    @BeforeClass
    public void setup(final ITestContext testContext) throws MalformedURLException {
        webDriver = WebDriverFactory.getDriver(testContext);
    }

    @AfterSuite
    public void tearDownTasks() {
        // Selenium driver close will be handled by Listener
    }

    // utils

    public void sleep(long duration) {
        try {
            TimeUnit.MILLISECONDS.sleep(duration);
        } catch (InterruptedException ex) {
            _logger.error("Exception,", ex);
        }
    }

    // selenium utils

    public void click(By identifier) {
        List<WebElement> elements = webDriver.findElements(identifier);
        if (!elements.isEmpty()) {
            elements.get(0).click();
        }
    }

    public String text(By identifier) {
        List<WebElement> elements = webDriver.findElements(identifier);
        if (!elements.isEmpty()) {
            return elements.get(0).getText();
        }
        return null;
    }

}