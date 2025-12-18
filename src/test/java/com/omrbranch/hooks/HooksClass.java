package com.omrbranch.hooks;

import com.omrbranch.utility.BaseClass;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;

public class HooksClass extends BaseClass {

  private static final Logger logger = LogManager.getLogger(HooksClass.class);

  @Before
  public void beforeScenario(Scenario scenario) {
    logger.info("==============================================");
    logger.info("Scenario Started : {}", scenario.getName());
    logger.info("==============================================");

    logger.info("Launching browser");
    browserLaunch();

    logger.info("Entering application URL");
    enterApplnUrl();
  }

  @After
  public void afterScenario(Scenario scenario) {

    if (scenario.isFailed()) {
      logger.error("Scenario Failed : {}", scenario.getName());
    } else {
      logger.info("Scenario Passed : {}", scenario.getName());
    }

    logger.info("Capturing screenshot");
    scenario.attach(getScreenshotAsBytes(), "image/png", "screenshot");

    logger.info("Closing browser");
    quitBrowser();

    logger.info("==============================================");
    logger.info("Scenario Ended : {}", scenario.getName());
    logger.info("==============================================");
  }
}
