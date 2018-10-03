package org.qecamp.zalenium;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariOptions;
import org.testng.ITestContext;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Jeeva Kandasamy (jkandasa)
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WebDriverFactory {
    private static AtomicBoolean tearDown = new AtomicBoolean(false);
    private static ConcurrentHashMap<String, RemoteWebDriver> driversMap = new ConcurrentHashMap<String, RemoteWebDriver>();

    public static RemoteWebDriver getDriver(final ITestContext testContext) throws MalformedURLException {
        return getDriver(getDriverName(testContext));
    }

    public static RemoteWebDriver getDriver(String key) throws MalformedURLException {
        if (driversMap.get(key) == null) {
            _logger.debug("Initializing drivers with the key:{{}}", key);
            driversMap.put(key, initialize(key));
        }
        return driversMap.get(key);
    }

    // this method will be called after suite
    public static void tearDownAll() {
        if (!tearDown.get()) {
            for (String key : driversMap.keySet()) {
                RemoteWebDriver webDriver = driversMap.get(key);
                if (webDriver != null) {
                    webDriver.quit();
                }
            }
        }
    }

    public static void removeDriver(String driverName) {
        driversMap.remove(driverName);
    }

    public static List<String> getKeysAllDriver() {
        ArrayList<String> keys = new ArrayList<String>();
        for (String key : driversMap.keySet()) {
            keys.add(key);
        }
        return keys;
    }

    public static String getDriverName(final ITestContext testContext) {
        String suiteName = testContext.getSuite().getName();
        boolean isParallel = testContext.getSuite().getXmlSuite().getParallel().isParallel();
        int threadCount = testContext.getSuite().getXmlSuite().getThreadCount();
        if (threadCount > 1 && testContext.getSuite().getXmlSuite().getParallel().toString().equalsIgnoreCase("tests")) {
            isParallel = true;
        }
        String testName = testContext.getName();
        // if we have thread count more than 1(Parallel), create driver for each test,
        // otherwise single drivers for entire suite
        if (isParallel && threadCount > 1) {
            return suiteName + "[" + testName + "]";
        } else {
            return suiteName;
        }
    }

    private static RemoteWebDriver initialize(String suiteName) throws MalformedURLException {
        String baseUrl = System.getProperty("baseUrl", "https://redhat.com");

        String remoteDriver = System.getProperty("seleniumGrid", "http://localhost:4444/wd/hub");

        _logger.debug("baseUrl:[{}]", baseUrl);
        _logger.debug("Selenium grid:[{}]", remoteDriver);

        // load UI driver
        MutableCapabilities caps = null;
        String browser = System.getProperty("browser", "chrome");
        String platform = System.getProperty("platform");
        String version = null;

        switch (browser) {
            case "chrome":
                caps = new ChromeOptions();
                browser = BrowserType.CHROME;
                if (platform == null) {
                    platform = "Linux";
                }
                break;
            case "firefox":
                caps = new FirefoxOptions();
                browser = BrowserType.FIREFOX;
                if (platform == null) {
                    platform = "Linux";
                }
                break;
            case "ie":
                caps = new InternetExplorerOptions();
                browser = BrowserType.IE;
                platform = "Windows 10";
                version = "11.103";
                break;
            case "edge":
                caps = new EdgeOptions();
                browser = BrowserType.EDGE;
                platform = "Windows 10";
                version = "17.17134";
                break;
            case "safari":
                caps = new SafariOptions();
                browser = BrowserType.SAFARI;
                platform = "macOS 10.13";
                version = "11.1";
                break;
        }

        caps.setCapability("platform", platform);
        if (version != null) {
            caps.setCapability("version", version);
        }

        // saucelabs configurations
        //caps.setCapability("platform", "Windows 7");
        caps.setCapability("name", suiteName);
        //caps.setCapability("tunnelIdentifier", "zalenium");

        caps.setCapability("zal:name", suiteName);
        caps.setCapability("zal:tz", "Asia/Kolkata");
        caps.setCapability("zal:screenResolution", "1920x1080");
        caps.setCapability("zal:idleTimeout", "60");
        caps.setCapability("zal:recordVideo", "true");
        caps.setCapability("zal:build", "1.0.0-SNAPSHOT");

        _logger.debug("{}", caps.toString());

        RemoteWebDriver webDriver = new RemoteWebDriver(new URL(remoteDriver), caps);

        // launch the application and maximize the screen
        webDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        webDriver.get(baseUrl);
        webDriver.manage().window().maximize();
        _logger.debug("Selenium webdriver created. SessionId:[{}]", webDriver.getSessionId());

        return webDriver;
    }
}