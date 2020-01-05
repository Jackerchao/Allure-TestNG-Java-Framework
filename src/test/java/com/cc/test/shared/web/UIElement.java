package com.cc.test.shared.web;

import java.lang.reflect.Method;

import org.openqa.selenium.By;
import org.w3c.dom.Element;

import com.cc.test.shared.report.Logging;
import com.cc.test.shared.web.util.Utility;
import com.cc.test.shared.web.util.Constants;

public class UIElement {
	private String elementArea = "";
	private String elementSection = "";
	private String elementName = "";
	private String file;
	private String elementXpath;
	private String byType;
	private String locator;
	private By by;
	
    public UIElement (UIElement element) {
    	this.elementName = element.getElementName();
    	this.byType = element.getByName();
    	this.locator = element.getByName();
    	this.file = element.getFile();
    	this.elementXpath = element.getElementXpath();
    }
    
    public UIElement (String area, String section, String elementName) {
    	this.setElementArea(area);
    	this.setElementSection(section);
    	this.elementName = elementName;
    	this.elementXpath = getElementXpath(area, section, elementName);
    	setFile(System.getProperty("ui.elements.file"));
    	getByAndLocatorByXpath();
    }
    
    public UIElement (String area, String section, String elementName, Object... args) {
    	this.setElementArea(area);
    	this.setElementSection(section);
    	this.elementName = elementName;
    	this.elementXpath = getElementXpath(area, section, elementName);
    	setFile(System.getProperty("ui.elements.file"));
    	getByAndLocatorByXpath(args);
    }
    
    public UIElement () {
    
    }
    
    public UIElement (By by) {
    	this.by = by;
    }
    
    public UIElement (String elementConfigXapth) {
    	setFile(System.getProperty("ui.elements.file"));
    	setElementXpath(elementConfigXapth);
    	getByAndLocatorByXpath();
    }
    
    public UIElement (String elementXapth, Object... args) {
    	setFile(System.getProperty("ui.elements.file"));
    	setElementXpath(elementXapth);
    	getByAndLocatorByXpath(args);
    }
    
    public UIElement (UIElement container, String elementXapth, Object... args) {
    	setFile(System.getProperty("ui.elements.file"));
    	setElementXpath(elementXapth);
    	getByAndLocatorByXpath(args);
    	setElementContainer(container);
    }
    
    public UIElement (UIElement container, UIElement element) {
    	setFile(System.getProperty("ui.elements.file"));
    	this.setElementArea(element.getElementArea());
    	this.setElementSection(element.getElementSection());
    	this.setFile(element.getFile());
    	
    	setElementContainer(container);
    	setLocator(container.getLocator() + element.getLocator());
    	setByName("xpath");
    }
    
	public String getByName() {
		return byType;
	}

	public void setByName(String by) {
		this.byType = by;
	}
	
	public String getLocator() {
		return locator;
	}

	public void setLocator(String locator) {
		this.locator = locator;
	}
	
	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}
	
	public String getElementXpath() {
		return elementXpath;
	}

	public void setElementXpath(String elementXpath) {
		this.elementXpath = elementXpath;
	}
	
	public String getElementName() {
		return elementName;
	}

	public void setElementName(String elementName) {
		this.elementName = elementName;
	}
	
	private void getByAndLocatorByXpath (Object... args) {
		
		String[] experssions = getElementXpath().split("//");
		String locator = null;
		String by = null;
		for (int i = 1; i < experssions.length; i++) {
			String tempExcepression = "//" + experssions[i];
			Element elementNode;
			try {
				elementNode = (Element) Utility.getNodesByXpath(getFile(), tempExcepression).item(0);
				if (i == 1) {
					locator = elementNode.getAttribute(Constants.XPATH_CONFIG_LOCATOR);
					by = elementNode.getAttribute(Constants.XPATH_CONFIG_FINDBY);
				} else {
					String tempLocator = elementNode.getAttribute(Constants.XPATH_CONFIG_LOCATOR);
					if (tempLocator.startsWith(".")) {
						tempLocator = tempLocator.substring(1, tempLocator.length());
					}
					locator += tempLocator;
				}
			} catch (Exception e) {
				Logging.logError("Failed to get locator/by for in file-" + getFile() + "with xpath - " + getElementXpath());
				e.printStackTrace();
			}
		}
		
		if (args.length > 0) {
			locator = String.format(locator, args);
		}
		
		setByName(by);
		setLocator(locator);
	}
	
	public By getBy() {
		if (this.by == null) {
			return setBy (this.getByName(), this.getLocator());
		} else {
			return by;
		}
		
	}

	public By setBy(String byName, String locator) {
		By elementBy = null;
		
		try {
			Method method = By.class.getDeclaredMethod(byName, String.class);
			
			elementBy = (By) method.invoke(null, locator);
		} catch (Exception e) {
			return null;
		}
		
		this.by = elementBy;
		this.byType = byName;
		this.locator = locator;
		
		return elementBy;
	}
	
	/**
	 * @param container
	 * @return
	 */
	private void setElementContainer (UIElement container) {
		if (container == null) {
			return;
		}
		
		setLocator (container.getLocator() + locator);
	}
	
	public UIElement append (UIElement element) {
		UIElement ele = new UIElement(this);
		ele.setLocator(this.locator + element.getLocator());
		
		return ele;
	}
	
	private String getElementXpath (String area, String section, String elementName) {
		String element_pathformat = "//Area[@Name = '%s']/Section[@Name = '%s']/Element[@Name = '%s']";
		return String.format(element_pathformat, area, section,elementName );
	}


	public String getElementArea() {
		return elementArea;
	}

	public void setElementArea(String elementArea) {
		this.elementArea = elementArea;
	}

	public String getElementSection() {
		return elementSection;
	}

	public void setElementSection(String elementSection) {
		this.elementSection = elementSection;
	}
	
	public void setBy(By by) {
		this.by = by;
	}
	





	


	

	
    
}
