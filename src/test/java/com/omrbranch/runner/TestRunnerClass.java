package com.omrbranch.runner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.AfterClass;
import org.junit.runner.RunWith;

import com.omrbranch.report.Reporting;
import com.omrbranch.utility.BaseClass;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import io.cucumber.junit.CucumberOptions.SnippetType;

/**
 * Main runner class for executing Cucumber BDD test scenarios. Integrates: -
 * Feature files (.feature) - Step definitions (glue code) - Reporting
 * configuration
 *
 * Uses JUnit as the test execution framework.
 */
@RunWith(Cucumber.class)
@CucumberOptions(tags = "@Login", stepNotifications = true, snippets = SnippetType.CAMELCASE, dryRun = false, publish = true, monochrome = true,

		// NOTE: Annotation values must be constants. Use forward slashes here.
		plugin = { "pretty", "json:target/output.json", "html:target/cucumber-report.html",
				"rerun:target/failed_scenarios.txt" },

		glue = { "com.omrbranch.stepdefinition", "com.omrbranch.hooks" },

		// Feature path (forward slashes are portable)
		features = "src/test/resources/features")
public class TestRunnerClass extends BaseClass {

	private static final Logger logger = LogManager.getLogger(TestRunnerClass.class);

	/**
	 * Executes after all scenarios have finished. Generates a JVM report based on
	 * the Cucumber JSON output.
	 */
	@AfterClass
	public static void afterClass() {
		String jsonPath = getProjectPath() + "/target/output.json";
		logger.info("Cucumber execution completed. Generating JVM report from: {}", jsonPath);

		try {
			Reporting.generateJvmReport(jsonPath);
			logger.info("JVM report generated successfully.");
		} catch (Exception e) {
			logger.error("Failed to generate JVM report.", e);
			throw e;
		}
	}
}
