package com.omrbranch.utility;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * BaseClass ========= Common Selenium utilities for:
 * <ul>
 * <li>Browser setup and teardown</li>
 * <li>Config reader</li>
 * <li>Wait utilities</li>
 * <li>Element interactions</li>
 * <li>Dropdown, Alert, Window, Frame handling</li>
 * <li>Mouse / Keyboard / JavaScript actions</li>
 * <li>Screenshot capture</li>
 * </ul>
 *
 * <p>
 * Configurable via <b>Config.properties</b>: browserType, timeout, environment,
 * qaUrl, uatUrl, prodUrl, screenshotPath
 * </p>
 */
public class BaseClass {

	private static final Logger logger = LogManager.getLogger(BaseClass.class);

	/** Global WebDriver instance used across tests. */
	public static WebDriver driver;

	private JavascriptExecutor js;
	private Actions actions;
	private Robot robot;

	// ==========================================================
	// PROJECT & CONFIGURATION
	// ==========================================================

	/** Cached Properties for Config.properties */
	private static final Properties CONFIG = new Properties();

	/** Absolute config file path (cross-platform safe). */
	private static final String CONFIG_PATH = getProjectPath()
			+ File.separator + "src" 
			+ File.separator + "test"
			+ File.separator + "resources" 
			+ File.separator + "config" 
			+ File.separator + "Config.properties";

	static {
		loadConfigOnce();
	}

	/**
	 * Returns the current project directory path (user.dir).
	 *
	 * @return absolute path of the project root folder.
	 */
	public static String getProjectPath() {
		return System.getProperty("user.dir");
	}

	/**
	 * Loads Config.properties into memory (called once by static block).
	 *
	 * @throws RuntimeException if the file cannot be loaded.
	 */
	private static void loadConfigOnce() {
		try (FileInputStream fis = new FileInputStream(CONFIG_PATH)) {
			CONFIG.clear();
			CONFIG.load(fis);
			logger.info("Config.properties loaded successfully from: {}", CONFIG_PATH);
		} catch (IOException e) {
			logger.error("Unable to load Config.properties from: {}", CONFIG_PATH, e);
			throw new RuntimeException("Unable to load Config.properties from: " + CONFIG_PATH, e);
		}
	}

	/**
	 * Reloads Config.properties at runtime (useful when you edit Config without
	 * restarting).
	 *
	 * @throws RuntimeException if the file cannot be loaded.
	 */
	public static void reloadConfig() {
		logger.info("Reloading Config.properties...");
		loadConfigOnce();
	}

	/**
	 * Reads a value from {@code Config.properties} for the given key.
	 *
	 * <p>
	 * <b>Behavior:</b>
	 * </p>
	 * <ul>
	 * <li>Config is loaded once and cached in memory.</li>
	 * <li>Returns trimmed value.</li>
	 * <li>Throws clear exception if key is missing/empty.</li>
	 * </ul>
	 *
	 * @param key property key to look up (example: browserType, timeout, qaUrl).
	 * @return trimmed value for the given key.
	 * @throws IllegalArgumentException if key is null/blank.
	 * @throws RuntimeException         if key is missing/empty in properties.
	 */
	public static String getPropertyFileValue(String key) {
		if (key == null || key.trim().isEmpty()) {
			throw new IllegalArgumentException("Property key must not be null/blank");
		}

		String value = CONFIG.getProperty(key);

		if (value == null || value.trim().isEmpty()) {
			logger.error("Property '{}' is missing/empty in Config.properties ({})", key, CONFIG_PATH);
			throw new RuntimeException("Key not found or empty in Config.properties: " + key);
		}

		return value.trim();
	}

	/**
	 * Gets timeout in seconds from Config.properties using key {@code timeout}.
	 *
	 * @return timeout in seconds.
	 */
	private int getConfiguredTimeout() {
		return Integer.parseInt(getPropertyFileValue("timeout"));
	}

	// ==========================================================
	// BROWSER INITIALIZATION
	// ==========================================================

	/**
	 * Launches a browser instance based on {@code browserType} in
	 * Config.properties.
	 *
	 * <p>
	 * <b>Supported values:</b> CHROME, FIREFOX, EDGE
	 * </p>
	 *
	 * <p>
	 * <b>Also does:</b>
	 * </p>
	 * <ul>
	 * <li>Maximize window</li>
	 * <li>Implicit wait using {@code timeout}</li>
	 * </ul>
	 *
	 * @throws IllegalArgumentException if browserType is invalid.
	 * @throws NumberFormatException    if timeout is not a valid integer.
	 */
	public static void browserLaunch() {
		String browserType = getPropertyFileValue("browserType");
		int timeout = Integer.parseInt(getPropertyFileValue("timeout"));

		logger.info("Launching browser: {}", browserType);

		switch (browserType.trim().toUpperCase()) {
		case "CHROME":
			driver = new ChromeDriver();
			break;

		case "FIREFOX":
			driver = new FirefoxDriver();
			break;

		case "EDGE":
			driver = new EdgeDriver();
			break;

		default:
			throw new IllegalArgumentException("Invalid browserType in Config.properties: " + browserType);
		}

		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(timeout));

		logger.info("Browser launched successfully | Browser={} | ImplicitWait={}s", browserType.toUpperCase(),
				timeout);
	}

	/**
	 * Navigates to application URL based on {@code environment}.
	 *
	 * <p>
	 * environment values: QA / UAT / PROD
	 * </p>
	 * <ul>
	 * <li>QA -> qaUrl</li>
	 * <li>UAT -> uatUrl</li>
	 * <li>PROD -> prodUrl</li>
	 * </ul>
	 *
	 * @throws IllegalArgumentException if environment is invalid.
	 */
	public static void enterApplnUrl() {
		String env = getPropertyFileValue("environment").trim().toUpperCase();
		env = System.getProperty("EnvDetails", env);
		String url;
		switch (env) {
		case "QA":
			url = getPropertyFileValue("qaUrl");
			break;
		case "UAT":
			url = getPropertyFileValue("uatUrl");
			break;
		case "PROD":
			url = getPropertyFileValue("prodUrl");
			break;
		default:
			throw new IllegalArgumentException("Invalid environment in Config.properties: " + env);
		}

		logger.info("Navigating to URL ({}) : {}", env, url);
		driver.get(url);
	}

	/** Closes current window. */
	public static void closeBrowser() {
		if (driver != null) {
			logger.info("Closing current browser window");
			driver.close();
		}
	}

	/** Quits entire session. */
	public static void quitBrowser() {
		if (driver != null) {
			logger.info("Quitting browser session");
			driver.quit();
			driver = null;
		}
	}

	// ==========================================================
	// WAIT UTILITIES
	// ==========================================================

	/**
	 * Applies implicit wait.
	 *
	 * @param secs seconds to wait implicitly.
	 */
	public void implicitWait(int secs) {
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(secs));
	}

	/**
	 * Waits until element is visible.
	 *
	 * @param element target element.
	 * @return visible element.
	 * @throws TimeoutException if not visible within timeout.
	 */
	public WebElement waitForVisible(WebElement element) {
		int timeout = getConfiguredTimeout();
		return new WebDriverWait(driver, Duration.ofSeconds(timeout)).until(ExpectedConditions.visibilityOf(element));
	}

	/**
	 * Waits until element is clickable.
	 *
	 * @param element target element.
	 * @return clickable element.
	 * @throws TimeoutException if not clickable within timeout.
	 */
	public WebElement waitForClickable(WebElement element) {
		int timeout = getConfiguredTimeout();
		return new WebDriverWait(driver, Duration.ofSeconds(timeout))
				.until(ExpectedConditions.elementToBeClickable(element));
	}

	/**
	 * Validates element before interaction.
	 *
	 * <p>
	 * Checks:
	 * </p>
	 * <ul>
	 * <li>Not null</li>
	 * <li>Visible (explicit wait)</li>
	 * <li>Displayed</li>
	 * <li>Enabled</li>
	 * </ul>
	 *
	 * <p>
	 * Also retries once if element becomes stale.
	 * </p>
	 *
	 * @param element    element to validate.
	 * @param actionName logical action name (for logs/errors).
	 */
	private void validateElementForInteraction(WebElement element, String actionName) {
		if (element == null) {
			throw new IllegalArgumentException(actionName + " FAILED -> WebElement is null.");
		}

		try {
			waitForVisible(element);

			if (!element.isDisplayed()) {
				throw new IllegalStateException(actionName + " FAILED -> Element is NOT displayed.");
			}
			if (!element.isEnabled()) {
				throw new IllegalStateException(actionName + " FAILED -> Element is NOT enabled.");
			}

		} catch (StaleElementReferenceException e) {
			logger.warn("StaleElement during {}. Retrying once...", actionName);
			waitForVisible(element);

			if (!element.isDisplayed() || !element.isEnabled()) {
				throw new IllegalStateException(actionName + " FAILED -> Element became stale / not interactable.");
			}
		}
	}

	// ==========================================================
	// ELEMENT INTERACTIONS
	// ==========================================================

	/**
	 * Clears and types text into an element.
	 *
	 * @param element input element.
	 * @param data    text to type.
	 */
	public void elementSendKeys(WebElement element, String data) {
		validateElementForInteraction(element, "elementSendKeys");
		element.clear();
		element.sendKeys(data);
	}

	/**
	 * Types text and presses ENTER.
	 *
	 * @param element input element.
	 * @param data    text to type.
	 */
	public void elementSendKeysEnter(WebElement element, String data) {
		validateElementForInteraction(element, "elementSendKeysEnter");
		element.sendKeys(data, Keys.ENTER);
	}

	/**
	 * Clicks an element safely.
	 *
	 * @param element clickable element.
	 */
	public void elementClick(WebElement element) {
		waitForClickable(element);
		validateElementForInteraction(element, "elementClick");
		element.click();
	}

	/**
	 * Clears an element value.
	 *
	 * @param element input element.
	 */
	public void elementClear(WebElement element) {
		validateElementForInteraction(element, "elementClear");
		element.clear();
	}

	// ==========================================================
	// DROPDOWN
	// ==========================================================

	/** Selects dropdown by visible text. */
	public void selectByVisibleText(WebElement element, String text) {
		validateElementForInteraction(element, "selectByVisibleText");
		new Select(element).selectByVisibleText(text);
	}

	/** Selects dropdown by value attribute. */
	public void selectByValue(WebElement element, String value) {
		validateElementForInteraction(element, "selectByValue");
		new Select(element).selectByValue(value);
	}

	/** Selects dropdown by index. */
	public void selectByIndex(WebElement element, int index) {
		validateElementForInteraction(element, "selectByIndex");
		new Select(element).selectByIndex(index);
	}

	/**
	 * Returns all dropdown option texts.
	 *
	 * @param element select element.
	 * @return list of option texts.
	 */
	public List<String> getAllDropdownOptions(WebElement element) {
		waitForVisible(element);
		Select select = new Select(element);
		List<String> optionTexts = new ArrayList<>();
		for (WebElement option : select.getOptions()) {
			optionTexts.add(option.getText());
		}
		return optionTexts;
	}

	// ==========================================================
	// ALERTS
	// ==========================================================

	/** Accepts active alert. */
	public void acceptAlert() throws NoAlertPresentException {
		driver.switchTo().alert().accept();
	}

	/** Dismisses active alert. */
	public void dismissAlert() throws NoAlertPresentException {
		driver.switchTo().alert().dismiss();
	}

	/** Gets alert text. */
	public String getAlertText() throws NoAlertPresentException {
		return driver.switchTo().alert().getText();
	}

	// ==========================================================
	// ACTIONS / JS / ROBOT
	// ==========================================================

	/** Lazy Actions init. */
	private Actions getActions() {
		if (actions == null) {
			actions = new Actions(driver);
		}
		return actions;
	}

	/** Lazy JS executor init. */
	private JavascriptExecutor getJsExecutor() {
		if (js == null) {
			js = (JavascriptExecutor) driver;
		}
		return js;
	}

	/** Drag and drop. */
	public void dragAndDrop(WebElement src, WebElement dest) {
		validateElementForInteraction(src, "dragAndDrop - source");
		validateElementForInteraction(dest, "dragAndDrop - destination");
		getActions().dragAndDrop(src, dest).perform();
	}

	/** Move to element. */
	public void moveToElement(WebElement element) {
		validateElementForInteraction(element, "moveToElement");
		getActions().moveToElement(element).perform();
	}

	/** Press ENTER using Robot. */
	public void enterKey() throws AWTException {
		if (robot == null) {
			robot = new Robot();
		}
		robot.keyPress(KeyEvent.VK_ENTER);
		robot.keyRelease(KeyEvent.VK_ENTER);
	}

	/** Scroll element into view. */
	public void scrollIntoView(WebElement element) {
		validateElementForInteraction(element, "scrollIntoView");
		getJsExecutor().executeScript("arguments[0].scrollIntoView(true)", element);
	}

	/** Click using JavaScript. */
	public void clickUsingJs(WebElement element) {
		validateElementForInteraction(element, "clickUsingJs");
		getJsExecutor().executeScript("arguments[0].click();", element);
	}

	// ==========================================================
	// SCREENSHOT
	// ==========================================================

	/**
	 * Captures screenshot and saves as PNG with timestamp.
	 *
	 * @param fileName base name (without extension).
	 * @throws IOException if saving fails.
	 */
	public void screenshotWithTimestamp(String fileName) throws IOException {
		String folder = getProjectPath() + getPropertyFileValue("screenshotPath");
		File dir = new File(folder);
		if (!dir.exists()) {
			dir.mkdirs();
		}

		String time = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		File dest = new File(dir, fileName + "_" + time + ".png");
		FileUtils.copyFile(src, dest);

		logger.info("Screenshot saved: {}", dest.getAbsolutePath());
	}

	/** Returns screenshot as bytes (useful for report attach). */
	public byte[] getScreenshotAsBytes() {
		return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
	}

	// ==========================================================
	// WINDOW / FRAME
	// ==========================================================

	/** Switches from main window to first child window (if any). */
	public void switchToChildWindow() {
		String main = driver.getWindowHandle();
		for (String each : driver.getWindowHandles()) {
			if (!each.equals(main)) {
				driver.switchTo().window(each);
				return;
			}
		}
	}

	/** Closes all child windows and returns to main. */
	public void closeAllChildWindows() {
		String main = driver.getWindowHandle();
		Set<String> handles = driver.getWindowHandles();
		for (String h : handles) {
			if (!h.equals(main)) {
				driver.switchTo().window(h).close();
			}
		}
		driver.switchTo().window(main);
	}

	/** Switch to frame by index. */
	public void switchToFrameByIndex(int index) {
		driver.switchTo().frame(index);
	}

	/** Switch to frame by name or id. */
	public void switchToFrameByNameOrId(String nameOrId) {
		driver.switchTo().frame(nameOrId);
	}

	/** Switch to frame by WebElement. */
	public void switchToFrameByElement(WebElement frameElement) {
		validateElementForInteraction(frameElement, "switchToFrameByElement");
		driver.switchTo().frame(frameElement);
	}

	/** Switch back to default content. */
	public void switchToDefaultContent() {
		driver.switchTo().defaultContent();
	}

	// ==========================================================
	// NAVIGATION / INFO
	// ==========================================================

	/** @return current page title. */
	public String getPageTitle() {
		return driver.getTitle();
	}

	/** @return current URL. */
	public String getCurrentUrl() {
		return driver.getCurrentUrl();
	}

	/** Navigate back. */
	public void navigateBack() {
		driver.navigate().back();
	}

	/** Navigate forward. */
	public void navigateForward() {
		driver.navigate().forward();
	}

	/** Refresh page. */
	public void refreshPage() {
		driver.navigate().refresh();
	}

	/** Scroll to bottom. */
	public void scrollToBottom() {
		getJsExecutor().executeScript("window.scrollTo(0, document.body.scrollHeight)");
	}

	/** Scroll to top. */
	public void scrollToTop() {
		getJsExecutor().executeScript("window.scrollTo(0, 0)");
	}

	/** Click using Actions. */
	public void actionClick(WebElement element) {
		validateElementForInteraction(element, "actionClick");
		getActions().click(element).perform();
	}

	/** Double click using Actions. */
	public void doubleClick(WebElement element) {
		validateElementForInteraction(element, "doubleClick");
		getActions().doubleClick(element).perform();
	}

	/** Right click using Actions. */
	public void rightClick(WebElement element) {
		validateElementForInteraction(element, "rightClick");
		getActions().contextClick(element).perform();
	}

	/** Press ENTER using Actions. */
	public void actionEnterKey() {
		getActions().sendKeys(Keys.ENTER).perform();
	}

	// ==========================================================
	// LOCATORS / ELEMENT INFO
	// ==========================================================

	/** Find element by id. */
	public WebElement findById(String id) throws NoSuchElementException {
		return driver.findElement(By.id(id));
	}

	/** Find element by name. */
	public WebElement findByName(String name) throws NoSuchElementException {
		return driver.findElement(By.name(name));
	}

	/** Find element by xpath. */
	public WebElement findByXpath(String xpath) throws NoSuchElementException {
		return driver.findElement(By.xpath(xpath));
	}

	/** Safe check element present in DOM. */
	public boolean isElementPresent(By locator) {
		return !driver.findElements(locator).isEmpty();
	}

	/** Get visible text from element. */
	public String getElementText(WebElement element) {
		validateElementForInteraction(element, "getElementText");
		return element.getText();
	}

	/** Get DOM property value. */
	public String getDomProperty(WebElement element, String propertyName) {
		waitForVisible(element);
		return element.getDomProperty(propertyName);
	}

	/** Check selected status. */
	public boolean isElementSelected(WebElement element) {
		validateElementForInteraction(element, "isElementSelected");
		return element.isSelected();
	}

	/** Check enabled status. */
	public boolean isElementEnabled(WebElement element) {
		validateElementForInteraction(element, "isElementEnabled");
		return element.isEnabled();
	}

	// ==========================================================
	// SLEEP (DEBUG ONLY)
	// ==========================================================

	/**
	 * Thread sleep (debugging only). Prefer explicit waits in real test flow.
	 *
	 * @param millis milliseconds to sleep.
	 */
	public void sleep(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
}
