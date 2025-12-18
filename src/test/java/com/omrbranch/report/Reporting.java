package com.omrbranch.report;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.omrbranch.utility.BaseClass;

import net.masterthought.cucumber.Configuration;
import net.masterthought.cucumber.ReportBuilder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Generates JVM HTML reports using Masterthought Cucumber Reporting.
 */
public class Reporting extends BaseClass {

  private static final Logger logger = LogManager.getLogger(Reporting.class);

  /**
   * Generates a detailed JVM report from the JSON file configured in
   * Config.properties.
   *
   * @param jsonFileReport JSON report file path (optional, can pass null).
   */
  public static void generateJvmReport(String jsonFileReport) {

    logger.info("Starting JVM Report Generation...");

    // Read paths and metadata from Config.properties
    String defaultJson = getProjectPath() + getPropertyFileValue("jsonFilePath");
    if (jsonFileReport == null || jsonFileReport.trim().isEmpty()) {
      jsonFileReport = defaultJson;
      logger.info("Using default JSON report path: {}", jsonFileReport);
    }

    String jvmPath = getProjectPath() + getPropertyFileValue("jvmFilePath");
    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

    File reportOutputDir = new File(jvmPath + File.separator + "JVM_Report_" + timeStamp);

    // Ensure report directory exists
    if (!reportOutputDir.exists()) {
      reportOutputDir.mkdirs();
      logger.info("Created report directory: {}", reportOutputDir.getAbsolutePath());
    }

    String projectName = getPropertyFileValue("projectName");
    String author = getPropertyFileValue("reportAuthor");
    String environment = getPropertyFileValue("environment");

    // Create Configuration Object
    Configuration config = new Configuration(reportOutputDir, projectName);
    config.addClassifications("Project", projectName);
    config.addClassifications("Author", author);
    config.addClassifications("Browser", getPropertyFileValue("browserType"));
    config.addClassifications("Platform", System.getProperty("os.name"));
    config.addClassifications("Environment", environment);
    config.addClassifications("Execution Time", timeStamp);

    // Build Report
    List<String> jsonFiles = new ArrayList<>();
    jsonFiles.add(jsonFileReport.trim());

    logger.info("Generating JVM report for JSON file(s): {}", jsonFiles);

    ReportBuilder builder = new ReportBuilder(jsonFiles, config);
    builder.generateReports();

    // Logging summary
    logger.info("------------------------------------------------------------");
    logger.info("JVM Report Generated Successfully!");
    logger.info("Location : {}", reportOutputDir.getAbsolutePath());
    logger.info("Project  : {}", projectName);
    logger.info("Author   : {}", author);
    logger.info("Env      : {}", environment);
    logger.info("Time     : {}", timeStamp);
    logger.info("------------------------------------------------------------");
  }
}
