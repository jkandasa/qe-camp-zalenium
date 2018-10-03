package org.qecamp.zalenium.tests;

import org.openqa.selenium.By;
import org.qecamp.zalenium.TestAbstract;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestDevelpoersMicroservicesPage extends TestAbstract {

    @BeforeClass
    public void loadInitialPage() {
        if (webDriver.getCurrentUrl().contains("https://www.redhat.com")) {
            click(By.linkText("DEVELOPERS"));
        }
    }

    @Test
    public void testMicroservicesPage() {
        click(By.linkText("Microservices"));
        _logger.debug("Title: {}", text(By.className("field--name-title")));
        sleep(WAIT_TIME);
    }
}
