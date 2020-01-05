package com.cc.test.shared.web.util;

import java.io.File;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.cc.test.shared.common.Util;
import com.cc.test.shared.report.Logging;
import com.cc.test.shared.web.Browser;
import com.cc.test.shared.web.UIElement;
import com.thoughtworks.selenium.SeleniumException;

public class SeleniumLib {
	
	private WebDriver driver = null;
	private Set<String> beforePopup;
	private Set<String> afterPopup;
	private static String PARENT_WINDOWHANDLE;
	
	private String loginUserNameId = "USER";
	private String loginPasswordId = "PASSWORD";
	private String loginSubmitId = "signonBtn";
	
	public SeleniumLib(WebDriver driver) {
		this.driver = driver;
	}
	
	public void loginPortal(WebDriver webDriver, String userName, String pwd, int retry) {
		boolean passed = false;
		for (int i = 0; i < retry; i++) {
			try {
				WebElement eTxtUser = webDriver.findElement(By.id(loginUserNameId));
				eTxtUser.clear();
				eTxtUser.sendKeys(userName);
				Thread.sleep(100);
				WebElement password = webDriver.findElement(By.id(loginPasswordId));
				password.clear();
				password.sendKeys(pwd);
				Thread.sleep(100);
				
				WebElement submitButton = webDriver.findElement(By.id(loginSubmitId));
				submitButton.click();
				submitButton.click();
				
				waitSignOn(webDriver, 5);
				passed = isSignOn(webDriver);
				if (passed) {
					break;
				}
			} catch (Exception e) {
				passed = false;
			}
		}
		
		if (!passed) {
			Logging.logWarn("Failed to login site");
		} else {
			Logging.logInfo("Login -- succeed");
			Utility.threadWait(5);
		}
	}
	
	public void loginSite(WebDriver webDriver, String userName, String pwd, By userNameBy, By passwordBy, By submitBy) 
		throws InterruptedException{
		
		WebElement eTxtUser = webDriver.findElement(userNameBy);
		eTxtUser.clear();
		eTxtUser.sendKeys(userName);
		Thread.sleep(100);
		WebElement password = webDriver.findElement(passwordBy);
		password.clear();
		password.sendKeys(pwd);
		Thread.sleep(100);
		
		WebElement submitButton = webDriver.findElement(submitBy);
		submitButton.click();
		Thread.sleep(100);
	}
	
	public void waitSignOn (WebDriver driver, int seconds) {
		long startTime = System.currentTimeMillis();
		
		while (System.currentTimeMillis() - startTime < seconds * 1000 && !isSignOn(driver) ) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				Logging.logError("Sleeping interrupted");
				break;
			}
		}
	}
	
	public boolean isSignOn (WebDriver driver) {
		try {
			waitPageContentLoad();
			driver.findElement(By.id(loginSubmitId));
			return false;
		} catch (Exception e) {
			return true;
		}
	}
	
	public void waitElementClickable(WebElement element) {
		(new WebDriverWait(driver, 20)).until(ExpectedConditions.elementToBeClickable(element));
	}
	
	public void waitElementVisible(WebElement element) {
		(new WebDriverWait(driver, 20)).until(ExpectedConditions.visibilityOf(element));
	}
	
	public WebElement  waitElementVisible (UIElement element, int... time) {
		int wait = 20;
		if (time != null && time.length > 0 ) {
			wait = time[0];
		}
		
		return new WebDriverWait(driver, wait).until(ExpectedConditions.visibilityOfElementLocated(element.getBy()));
	}
	
	/**
	 * @param locator
	 * @throws Exception
	 */
	
	public void visibilityOfElementLocated(By locator) {
		(new WebDriverWait(driver, 20)).until(ExpectedConditions.visibilityOfElementLocated(locator));
	}
	
	public void visibilityOfElementLocated(By locator, int timeout) {
		(new WebDriverWait(driver, timeout)).until(ExpectedConditions.visibilityOfElementLocated(locator));
	}
	
	/**
	 * @param timeout
	 * @param element
	 * @return
	 */
	
	public boolean isElementVisible(int timeout, final WebElement element) {
		return new WebDriverWait(driver, timeout).until(new ExpectedCondition<Boolean>() {
			
			public Boolean apply(WebDriver arg0) {
				return element.isDisplayed();
			}
		});
	}
	
	public boolean isElementNotVisible(UIElement element) {
		try {
			new WebDriverWait(driver, 1).until(ExpectedConditions.invisibilityOfElementLocated(element.getBy()));
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public WebDriver startIEDriver(String driverPath) {
		File file = new File(driverPath);
		System.setProperty("webdriver.ie.driver", file.getAbsolutePath());
		DesiredCapabilities ieCapabilities = DesiredCapabilities.internetExplorer();
		ieCapabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
		ieCapabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
		this.driver = new InternetExplorerDriver(ieCapabilities);
		driverSetDefault();
		return this.driver;
	}
	
	public WebDriver startChromeDriver(String diverPath) {
		Map<String, Object> prefs = new HashMap<String, Object>();
		// To Turns off multiple download warning 
		prefs.put("profile.default_content_setting.popups", 0);
		prefs.put("profile.content_settings.pattern_pairs.*.multiple-automatic-downloads", 1);
		ChromeOptions options = new ChromeOptions();
		options.setExperimentalOption("prefs", prefs);
		options.addArguments("--cipher-suite-blacklist=0x0039,0x0033");
		options.addArguments("disable-popup-blocking");
		options.addArguments("start-maximized");
		options.addArguments("--no-sandbox");
		options.addArguments("--disable-dev-shm-usage");
		options.setExperimentalOption("useAutomationExtension", false);
		options.addArguments("--allow-running-insecure-content");
		File file = new File(diverPath);
		System.setProperty("webdriver.chrome.driver", file.getAbsolutePath());
		this.driver = new ChromeDriver(options);
		driver.manage().deleteAllCookies();
		return this.driver;
	}
	
	public WebDriver startPhantomJSDriver(String driverPath) {
		File file = new File(driverPath);
		System.setProperty(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, file.getAbsolutePath());
		Capabilities caps = new DesiredCapabilities();
		((DesiredCapabilities) caps).setJavascriptEnabled(true);
		((DesiredCapabilities) caps).setCapability("takesScreenshot", true);
		driver = new PhantomJSDriver(caps);
		driverSetDefault();
		return this.driver;
		
	}
	
	public WebDriver startDriver(Browser browserType, String driverPath) {
		if (browserType.equals(Browser.IE)) {
			return startIEDriver(driverPath);
		} else if (browserType.equals(Browser.PHANTOMJS)) {
			return startPhantomJSDriver(driverPath);
		} else {
			return startChromeDriver(driverPath);
		}
	}
	
	public void quitDriver() {
		driver.close();
		driver.quit();
		Utility.threadWait(1);
		driver = null;
	}
	
	public void sleep(int time) {
		try {
			Thread.sleep(time * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void sleep(double time) {
		try {
			Thread.sleep((long) (time * 1000));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void driverSetDefault() {
		driver.manage().deleteAllCookies();
		driver.manage().window().maximize();
	}
	
	public By getElementBy(String by, String locator) {
		By element = null;
		
		try {
			Method method = By.class.getDeclaredMethod(by, String.class);
			element = (By) method.invoke(null, locator);
		} catch (Exception e) {
			return null;
		}
		return element;
	}
	
	public void switchToDeFaultContent() {
		driver.switchTo().defaultContent();
	}
	
	public void switchFrame(String by, String locator) {
		if (by.equals("index")) {
			int index = Integer.parseInt(locator);
			driver.switchTo().frame(index);
		} else if (by.equals("xpath")) {
			driver.switchTo().frame(driver.findElement(By.xpath(locator)));
		} else {
			driver.switchTo().frame(locator);
		}
	}
	
	public void switchToFrame(UIElement uiElement) {
		switchFrame(uiElement.getBy());
	}
	
	/**
	 * @param by
	 */
	
	private void switchFrame(By by) {
		driver.switchTo().frame(driver.findElement(by));
	}
	
	public WebElement getElement(WebElement ancestor, UIElement uiElement) {
		WebElement element = null;
		element = ancestor.findElement(uiElement.getBy());
		return element;
	}
	
	public WebElement getElement(UIElement uiElement) {
		WebElement element = driver.findElement(uiElement.getBy());
		return element;
	}
	
	public List<WebElement> getElements (UIElement uiElement) {
		 List<WebElement> elements = driver.findElements(uiElement.getBy());
		 return elements;
	}
	
	public WebElement getElementWait(By by, int seconds) {
		WebElement element = null;
		element = new WebDriverWait(driver, seconds).until(ExpectedConditions.presenceOfElementLocated(by));
		return element;
	}
	
	public WebElement getElementWait(UIElement uiElement) {
		return getElementWait(uiElement, 10);
	}
	
	public WebElement getElementWait(UIElement uiElement, int seconds) {
		return getElementWait(uiElement.getBy(), seconds);
	}
	
	/**
	 * @param by
	 * @param seconds
	 * @throws Exception
	 */
	public WebElement visibilityOfElementLocator(By by, int seconds) {
		WebElement element = null;
		element = new  WebDriverWait(driver, seconds).until(ExpectedConditions.visibilityOfElementLocated(by));
		return element;
	}
	
	public By getBy(UIElement uiElement) {
		return uiElement.getBy();
	}
	
	public String getElementText(WebElement element) {
		String text = element.getText();
		
		if (StringUtils.isEmpty(text)) {
			String script = "return arguments[0].innerText";
			text = (String) ((JavascriptExecutor) driver).executeScript(script, element);
		}
		
		if (!StringUtils.isEmpty(text)) {
			return text.trim();
		} else {
			return "";
		}
	}
	
	public String getElementInnerHtml(WebElement element) {
		String script = "return arguments[0].innerHTML";
		String text = (String) ((JavascriptExecutor) driver).executeScript(script, element);
		return text;
	}
	
	public String getElementText(UIElement elementExpression) {
		WebElement element = getElement(elementExpression);
		String text = element.getText();
		
		if (StringUtils.isEmpty(text)) {
			String script = "return arguments[0].innerText";
			text = (String) ((JavascriptExecutor) driver).executeScript(script, element);
		}
		
		return text;
	}
	
	public WebElement getParent(WebElement e) {
		return e.findElement(By.xpath(".."));
	}
	
	public void focusOnElement(UIElement expression) {
		WebElement element = this.getElement(expression);
		viewElement(element);
		((JavascriptExecutor) driver).executeScript("arguments[0].focus", element);
		if ("input".equals(element.getTagName())) {
			element.sendKeys("");
		} else {
			new Actions(driver).moveToElement(element).perform();
		}
	}
	
	public void focusOnElement(WebElement element) {
		
		((JavascriptExecutor) driver).executeScript("arguments[0].focus", element);
		if ("input".equals(element.getTagName()) && element.getAttribute("type").equals("text") ) {
			element.sendKeys("");
		} else {
			new Actions(driver).moveToElement(element).perform();
		}
	} 
	
	public void clickOnRadio(UIElement elementPath) {
		Logging.info("Click on radio-" + elementPath.getElementName());
		WebElement element = getElement(elementPath);
		focusOnElement(element);
		element.click();
		Actions action = new Actions(driver);
		action.doubleClick(element);
		action.perform();
		Utility.threadWait(1);
	}
	
	public void performDoubleClick(UIElement elementPath) {
		WebElement element = getElement(elementPath);
		Actions action = new Actions(driver);
		action.doubleClick(element);
		action.perform();
	}
	
	public void clickOnCheckBox(UIElement elementPath, boolean check, int ancestor) {
		clickOnCheckBoxByAttribute(elementPath, check, ancestor, "class");
	}
	
	public void clickOnCheckBox(UIElement elementPath, boolean check) {
		Logging.info("Click on check box-" + elementPath.getElementName() );
		WebElement element = getWebElementWaitDefault(elementPath);
		boolean isChecked = element.isSelected();
		int retryCount = 3;
		if (check) {
			while (!isChecked && retryCount >0 ) {
				focusOnElement(element);
				element.click();
				Utility.threadWait(1);
				isChecked = element.isSelected();
				retryCount--;
			}
		} else {
			while (isChecked && retryCount > 0) {
				focusOnElement(element);
				element.click();
				Utility.threadWait(1);
				isChecked = element.isSelected();
				retryCount--;
			}
		}
	}
	
	public void clickOnCheckBoxByAttribute(UIElement elementPath, boolean check, int ancestor, String attribute) {
		Logging.logInfo("Click on check box-" + elementPath.getElementName());
		WebElement element = getWebElementWaitDefault(elementPath);
		
		WebElement ancestorElement = element;
		for (int i = 0; i < ancestor; i ++ ) {
			ancestorElement = getParent(ancestorElement);
		}
		
		String classStyle = ancestorElement.getAttribute(attribute);
		
		int retryCount = 3;
		
		if (check) {
			while (!isCheckboxChecked(classStyle) && retryCount > 0 ) {
				try {
					focusOnElement(element);
					element.click();
				} catch (Exception e) {
					Logging.logInfo("Retry click on check box" + element.getAttribute(attribute));
				}
				
				classStyle = ancestorElement.getAttribute(attribute);
				retryCount--;
				Utility.threadWait(1);
			}
		} else {
			while (isCheckboxChecked(classStyle) && retryCount > 0 ) {
				try {
					focusOnElement(element);
					element.click();
				} catch (Exception e) {
					Logging.logInfo("Retry click on check box" + element.getAttribute(attribute));
				}
				
				classStyle = ancestorElement.getAttribute(attribute);
				retryCount--;
				Utility.threadWait(1);
			}
		}
	}
	
	private boolean isCheckboxChecked(String classStyle) {
		return (classStyle == null ? "" : classStyle).contains("checked")
				|| (classStyle == null ? "" : classStyle).contains("true");
	}
	
	public boolean inputValueAndEnter(String inputValue, UIElement elementPath) {
		if (!StringUtils.isEmpty(inputValue)) {
			try {
				WebElement element = getWebElementWaitDefault(elementPath);
				String text = getInputValue(element);
				if (inputValue.equals(text)) {
					return false;
				} else {
					focusOnElement(element);
					element.clear();
					element.sendKeys(inputValue);
					element.sendKeys(Keys.ENTER);
					logFieldValue(elementPath.getElementName(), inputValue);
					return true;
				}
			} catch (Exception e) {
				logFailedFieldValue(elementPath, inputValue);
			}
			return false;
		} else {
			return false;
		}
	}
	
	public void clearValue(UIElement elementPath) {
		WebElement element;
		try {
			element = getWebElementWaitDefault(elementPath);
			focusOnElement(element);
			element.clear();
		} catch (Exception e) {
			logFailedFieldValue(elementPath, "Clear input value");
		}
	}
	
	public boolean inputValue(String inputValue, UIElement elementPath, boolean... isIgnoreComma) {
		if (!StringUtils.isEmpty(inputValue)) {
			boolean ignoreComma = false, isDigit = false;
			if (isIgnoreComma.length > 0) {
				ignoreComma = isIgnoreComma[0];
			}
			if (ignoreComma) {
				Pattern pattern = Pattern.compile("[0-9.]*");
				Matcher isNum = pattern.matcher(inputValue);
				isDigit = isNum.matches();
			}
			WebElement element = getWebElementWaitDefault(elementPath);
			String text = getInputValue(element);
			String actValue = "";
			if (inputValue.equals(text)) {
				return false;
			} else {
				focusOnElement(element);
				int count = 0;
				do {
					element.clear();
					count ++;
				} while (!StringUtils.isEmpty(element.getAttribute("value")) && count <= 3);
				
				count = 0;
				do {
					element.clear();
					element.sendKeys(inputValue);
					actValue = element.getAttribute("value");
					if (isDigit) {
						actValue = actValue.replaceAll(",", "");
					}
				} while (!actValue.equals(inputValue) && count < 3);
				
				logFieldValue(elementPath.getElementName(), inputValue);
				return true;
				
			}
		} else {
			return false;
		}
	}
	
	public void inputTextOnly(String passwordValue, UIElement elementPath) {
		WebElement password = getElementWait(elementPath);
		password.clear();
		password.sendKeys(passwordValue);
	}
	
	public boolean inputValueByJS(String inputValue, UIElement elementPath) {
		
		if (!StringUtils.isEmpty(inputValue)) {
			WebElement element = getWebElementWaitDefault(elementPath);
			
			String text = getInputValue(element);
			if (inputValue.equals(text)) {
				return false;
			} else {
				((JavascriptExecutor) driver).executeScript("arguments[0].value=arguments[1]", element, inputValue);
				logFieldValue(elementPath.getElementName(), inputValue);
			}
			
			return true;
			
		} else {
			return false;
		}
	}
	
	public void setElementAttribute(String attributeName, String value, UIElement elementPath) {
		WebElement element;
		try {
			element = getWebElementWaitDefault(elementPath);
			((JavascriptExecutor) driver).executeScript("arguments[0].setAttribute('arguments[1]','arguments[2]')", 
					element, attributeName, value);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String getInputValue(UIElement elementPath) {
		try {
			WebElement element = getElement(elementPath);
			return element.getAttribute("value");
		} catch (Exception e) {
			return null;
		}
	}
	
	public String getInputValue(WebElement element) {
		try {
			return element.getAttribute("value");
		} catch (Exception e) {
			return null;
		}
	}
	
	public void enterAndClickTwice(UIElement elementPath) {
		Logging.logInfo("Enter and double click on element-" + elementPath.getElementName());
		WebElement element = getWebElementWaitDefault(elementPath);
		waitElementClickable(element);
		focusOnElement(element);
		element.sendKeys(Keys.ENTER);
		element.click();
		element.click();
	}
	
	public void doubleClick(WebElement element) {
		waitElementClickable(element);
		(new Actions(driver)).moveToElement(element).doubleClick().perform();
	}
	
	public void doubleClick(UIElement elementPath) {
		Logging.logInfo("Double click on element-" + elementPath.getElementName());
		WebElement element = getWebElementWaitDefault(elementPath);
		waitElementClickable(element);
		Actions action = new Actions(driver);
		action.doubleClick(element);
		action.perform();
	}
	
	public boolean isElementEnable(WebElement element) {
		if (element == null) {
			Logging.logError("The element is null");
			return false;
		}
		
		String elementClass = element.getAttribute("class");
		if (StringUtils.isNotEmpty(elementClass) && elementClass.contains("disabled")) {
			return false;
		} else {
			return true;
		}
	}
	
	public boolean isElementEnable(UIElement elementPath) {
		try {
			WebElement element = getWebElementWaitDefault(elementPath);
			return isElementEnable(element);
		} catch (Exception e) {
			Logging.logError("Has issue get element.");
			return false;
		}
	}
	
	/**
	 * @param elementPath
	 * To scroll table at first and click element object
	 */
	public void javaScriptScrollClick(UIElement elementPath) {
		Logging.logInfo("Click on element-" + elementPath.getElementName());
		WebElement element = getWebElementWaitDefault(elementPath);
		((JavascriptExecutor) driver).executeScript("return arguments[0].scrollIntoView();", element);
		((JavascriptExecutor) driver).executeScript("arguments[0].click()", element);
	}
	
	/**
	 * @param elementPath
	 * @throws Exception
	 */
	public void javaScriptClick(UIElement elementPath) {
		Logging.logInfo("Click on element-" + elementPath.getElementName());
		WebElement element = getWebElementWaitDefault(elementPath);
		((JavascriptExecutor) driver).executeScript("arguments[0].click()", element);
	}
	
	public void click(UIElement elementPath) {
		Logging.logInfo("Click on element-" + elementPath.getElementName());
		WebElement element = getWebElementWaitDefault(elementPath);
		click(element);
	}
	
	public void click(WebElement element) throws SeleniumException {
		waitElementClickable(element);
		
		if (isElementEnable(element)) {
			focusOnElement(element);
			Utility.threadWait(0.5);
			element.click();
			Utility.threadWait(0.5);
		} else {
			throw new SeleniumException("The element is disabled.");
		}
	}
	
	public void rightClick(WebElement element) {
		Actions action = new Actions(driver);
		action.contextClick(element).build().perform();
	}
	
	public void rightClick() {
		Actions action = new Actions(driver);
		action.contextClick().build().perform();
	}
	
	public void sendEnter(WebElement element) {
		focusOnElement(element);
		element.sendKeys(Keys.ENTER);
	}
	
	public void sendEnter(UIElement elementPath) {
		WebElement element = getWebElementWaitDefault(elementPath);
		sendEnter(element);
	}
	
	public void sendTab(UIElement elementPath) {
		WebElement element = getWebElementWaitDefault(elementPath);
		
		focusOnElement(element);
		element.sendKeys(Keys.TAB);
	}
	
	public void sendArrowDown(WebElement el) {
		waitElementClickable(el);
		click(el);
		el.sendKeys(Keys.ARROW_DOWN);
		Utility.threadWait(0.2);
	}
	
	public void sendTabAdvance(UIElement elementPath) {
		WebElement element = getWebElementWaitDefault(elementPath);
		focusOnElement(element);
		element.sendKeys(Keys.TAB);
		element.click();
		element.sendKeys(Keys.TAB);
	}
	
	public void scrollToLocation (int x, int y) {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript(String.format("window.scrollTo(%d,%d);", x, y));
	}
	
	public WebElement selectElementFromListByText(String elementText, List<WebElement> elements) {
		WebElement element = null;
		for (int j = 0; j < elements.size(); j++) {
			String text = getElementText(elements.get(j));
			if (text.toUpperCase().equals(elementText.toUpperCase())) {
				element = elements.get(j);
				break;
			}
		}
		// If cannot fully match, then select partial match;
		if (element == null) {
			for (int j = 0; j < elements.size(); j++) {
				String text = getElementInnerHtml(elements.get(j));
				if (text.toUpperCase().equals(elementText.toUpperCase())) {}
				}
		}
		return element;
	}
	
	public void clickOnLinkText(String text) {
		
		WebElement link;
		link = driver.findElement(By.linkText(text));
		((JavascriptExecutor) driver).executeScript("arguments[0].focus", link);
		new Actions(driver).moveToElement(link).perform();
		link.click();
		
		link = driver.findElement(By.linkText(text));
		((JavascriptExecutor) driver).executeScript("arguments[0].focus", link);
		new Actions(driver).moveToElement(link).perform();
		link.click();
	}
	
	public void moveToElement(WebElement element) {
		Actions action = new Actions(driver);
		action.moveToElement(element).build().perform();
	}
	
	public void moveToElement(UIElement elementPath) {
		WebElement element = getWebElementWaitDefault(elementPath);
		Actions action = new Actions(driver);
		action.moveToElement(element).build().perform();
	}
	
	public void moveAndClick(UIElement elementPath) {
		WebElement element = getWebElementWaitDefault(elementPath);
		Actions action = new Actions(driver);
		action.moveToElement(element).click().perform();
	}
	
	public void viewElement(UIElement elementPath) {
		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", getElement(elementPath));
	}
	
	public void viewElement(WebElement element) {
		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
	}
	
	private void logFieldValue(String filed, String value) {
		Logging.logInfo(String.format("Set field '%s' value as '%s'", filed, value));
	}
	
	private void logFailedFieldValue(UIElement elementPath, String value) {
		Logging.logInfo(
				String.format("Failed to set value as '%s' for element '%s'", value, elementPath.getElementXpath()));
	}
	
	public WebDriver switchToFrame(UIElement frameXpath, int timeOutSecond) {
		By byMainFrame = getBy(frameXpath);
		WebElement eFrameMain = (new WebDriverWait(driver, timeOutSecond))
				.until(ExpectedConditions.presenceOfElementLocated(byMainFrame));
		return driver.switchTo().frame(eFrameMain);
	}
	
	public void selectValueFromDropdownList(String value, UIElement triggerElement, UIElement listElement, int retry, 
			String... listType) throws SeleniumException {
		
		boolean passed = false;
		String error = "";
		
		if (StringUtils.isEmpty(value)) {
			Logging.logInfo("No value to update for dropdown list-" + triggerElement.getElementName());
			return;
		}
		
		Logging.logInfo("Start select value--" + value + "from '" + listElement.getElementName() + "'" );
		for (int i = 0; i < retry; i++) {
			try {
				WebElement teTradeTypeBody = getElementWait(triggerElement, 15);
				teTradeTypeBody.click();
				
				WebElement tradeType = null;
				String locator = listElement.getLocator();
				
				String listTypeTag = "";
				if (listType.length > 0 && !StringUtils.isEmpty(listType[0])) {
					listTypeTag = listType[0];
				}
				
				if (locator.endsWith("/")) {
					if (StringUtils.isEmpty(listTypeTag)) {
						locator = locator + "li";
					} else {
						locator = locator + listTypeTag;
					}
				}
				listElement.setLocator(locator);
				
				List<WebElement> tradeTypeList = driver.findElements(listElement.getBy());
				tradeType = selectElementFromListByText(value, tradeTypeList);
				this.viewElement(tradeType);
				focusOnElement(tradeType);
				this.click(tradeType);
				int count = 1;
				while (!isElementNotVisible(listElement) && count > 0) {
					count--;
					this.click(triggerElement);
				}
				passed = true;
				error = "";
				Utility.threadWait(0.5);
				break;
			} catch (Exception e) {
				error = e.getMessage();
				continue;
			}
		}
		
		if (!passed) {
			String errorMessage = "Failed to set dropdown list value as '" + value.toUpperCase()
					+ "', error message is:" + error;
			throw new SeleniumException(errorMessage);
		} else {
			Logging.logInfo("Select dropdown list value successfully.");
		}
	}
	
	public boolean isExistsElement(UIElement element) {
		try {
			this.getElement(element);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public void selectValueFromDropDownList(String listXPath, String value) {
		List<WebElement> tradeTypeList = driver.findElements(By.xpath(listXPath));
		WebElement listElement = selectElementFromListByText(value, tradeTypeList);
		
		focusOnElement(listElement);
		listElement.click();
	}
	
	public void selectValueFromDropdownList(UIElement listPath, String value) {
		List<WebElement> tradeTypeList = driver.findElements(listPath.getBy());
		WebElement listElement = selectElementFromListByText(value, tradeTypeList);
		
		focusOnElement(listElement);
		listElement.click();
	}
	
	public void selectByVisibleText(UIElement elementPath, String text) {
		Logging.logInfo("selecting by visible on element-" + elementPath.getElementName());
		WebElement element = getWebElementWaitDefault(elementPath);
		new Select(element).selectByVisibleText(text);
	}
	
	public void beforeSwitchWindow() {
		PARENT_WINDOWHANDLE = driver.getWindowHandle();
		this.beforePopup = driver.getWindowHandles();
	}
	
	public void closeCurrentWindow() {
		driver.close();
	}
	
	public void switchToNewWindow() {
		if (driver.getWindowHandles().size() < 2) {
			return;
		}
		// defaultWindowHandle = driver.getWindowHandle();
		afterPopup = driver.getWindowHandles();
		afterPopup.removeAll(beforePopup);
		if (afterPopup.size() == 1) {
			switchToWindowByHandle(afterPopup.iterator().next());
		}
	}
	
	public void switchToNewWindow(Set<String> beforePopup) {
		if (driver.getWindowHandles().size() < 2) {
			return;
		}
		Set<String> afterPopup = driver.getWindowHandles();
		afterPopup.removeAll(beforePopup);
		if (afterPopup.size() == 1) {
			switchToWindowByHandle(afterPopup.iterator().next());
		}
	}
	
	public void switchToWindowByHandle(String windowHandle) {
		driver.switchTo().window(windowHandle);
	}
	
	public void closeAndReturnMainWindow(String handle) {
		if (driver.getWindowHandles().size() > 1) {
			driver.close();
		}
		switchToWindowByHandle(handle);
	}
	
	public String getTitle() {
		return driver.getTitle();
	}
	
	public String getCurrentUrl() {
		return driver.getCurrentUrl();
	}
	
	public WebElement getWebElementWaitDefault(UIElement elementPath) {
		WebElement element = getElementWait(elementPath.getBy(), 5);
		return element;
	}
	
	//default loading mask
	public WebElement getLoadingMask(WebDriver driver) {
		return (new WebDriverWait(driver, 20)).until(ExpectedConditions
					.presenceOfElementLocated(By.cssSelector(".x-mask-loading:not([style*='display: none'])")));
	}
	
	public boolean findElementsByText(String text) {
		return findElements("xpath", String.format("//*[text()='%s']", text));
	}
	
	public WebElement getElementByText(String text) {
		return getElement("xpath", String.format("//*[text()='%s']", text));
	}
	
	public WebElement getElement(String methodName, String strlocator) {
		return this.getElement(getByMethod(methodName, strlocator));
	}
	
	public WebElement getElement(By by) {
		return driver.findElement(by);
	}
	
	public boolean findElements(String methodName, String strlocator) {
		return this.findElements(getByMethod(methodName, strlocator));
	}
	
	public boolean findElements(By by) {
		return (driver.findElements(by).size() > 0) ? true : false;
	}
	
	public By getByMethod(String methodName, String locator) {
		return (By) Util.execMethod(By.class, methodName, String.class, locator);
	}
	
	public UIElement getUIElement(UIElement container, UIElement subElement) {
		String elementPath = container.getLocator() + subElement.getLocator();
		return new UIElement(By.xpath(elementPath));
	}
	
	public void waitLoadingComplete(UIElement loadingMask, int seconds) throws SeleniumException {
		long startTime = System.currentTimeMillis();
		
		while (System.currentTimeMillis() - startTime < seconds * 1000 && isPageLoading(loadingMask)) {
			try {
				Thread.sleep(500);			
				} catch (InterruptedException e) {
					Logging.logError("Sleeping interrupted");
					break;
				}
		}
		
		if (isPageLoading(loadingMask)) {
			throw new SeleniumException("Page is loading after wait " + seconds + " seconds");
		}
	}
	
	public boolean isPageLoading(UIElement loadingMask) {
		try {
			waitPageContentLoad();
			getElement(loadingMask);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public void waitPageContentLoad() {
		(new WebDriverWait(driver, 20)).until(ExpectedConditions.presenceOfElementLocated(By.tagName("div")));
	}
	
	public void selectDateFromUI(String inputDate, String inputDateFormat, String uiMonthFormat,
			UIElement dataContainer, UIElement yearMonth, UIElement nextMonth, UIElement previousMonth,
			UIElement dayPath) {
		try {
			Logging.logInfo("Select date-" + inputDate);
			SimpleDateFormat inputDateFormater = new SimpleDateFormat(inputDateFormat, Locale.ENGLISH);
			SimpleDateFormat uiMonthFormater = new SimpleDateFormat(uiMonthFormat, Locale.ENGLISH);
			
			Date date = inputDateFormater.parse(inputDate);
			
			Calendar dateCalandar = Calendar.getInstance();
			dateCalandar.setTime(date);
			
			String uiMonth = this.getElementText(getUIElement(dataContainer, yearMonth));
			Date uiMonthDate = uiMonthFormater.parse(uiMonth);
			Calendar uiMonthCalandar = Calendar.getInstance();
			uiMonthCalandar.setTime(uiMonthDate);
			
			int yearBreak = (dateCalandar.get(Calendar.YEAR) - uiMonthCalandar.get(Calendar.YEAR)) * 12;
			int monthBreak = dateCalandar.get(Calendar.MONTH) - uiMonthCalandar.get(Calendar.MONTH);
			int totalMonthBreak = yearBreak + monthBreak;
			
			if (totalMonthBreak > 0) {
				for (int i = 0; i < totalMonthBreak; i++) {
					this.click(getUIElement(dataContainer, nextMonth));
				}
			} else if (totalMonthBreak < 0) {
				for (int i = totalMonthBreak; i < 0; i++) {
					this.click(getUIElement(dataContainer, previousMonth));
				}
			}
			
			String day = dateCalandar.get(Calendar.DAY_OF_MONTH) + "";
			
			List<WebElement> days = this.getElements(getUIElement(dataContainer, dayPath));
			for (int i = 0; i< days.size(); i++) {
				String text = this.getElementText(days.get(i));
				if (day.equalsIgnoreCase(text.trim())) {
					this.click(days.get(i));
					break;
				}
			}
			//Select day
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @param actual
	 * @param expected
	 */
	public void assertEquals(String actual, String expected) {
		if (StringUtils.equals(actual, expected)) {
			Logging.logInfo("The actual match the exoected. Text is ["+ expected +"]");
		} else {
			Logging.logWarn("The actual not match with the expected. Actual text is ["+ actual +"]; Expected is["
					+ expected +"].");
		}
	}
	
	public List<String> getElementsText(UIElement itemsUE) {
		List<String> itemText = new ArrayList<String>();
		List<WebElement> listWE = getElements(itemsUE);
		for (WebElement we : listWE) {
			String text = getElementText(we);
			itemText.add(text);
		}
		
		return itemText;
	}
	
	public String getNodeText(WebElement e) {
		String text = e.getText().trim();
		List<WebElement> children = e.findElements(By.xpath("./*"));
		for (WebElement child : children) {
			String childText = child.getText();
			if (StringUtils.isEmpty(childText)) {
				continue;
			}
			text = text.substring(0, text.indexOf(childText))
					+ text.substring(text.indexOf(childText)) + childText.length();
		}
		
		return text;
	}
	
	
	
	

}
