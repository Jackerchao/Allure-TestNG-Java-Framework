package com.cc.test.shared.common;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ElementHelper {
	
	private XMLReader xmlReader;
	private XPathFactory xPathfactory;
	private XPath xpath;
	
	public ElementHelper(String filePath) throws SAXException, IOException, ParserConfigurationException {
		xmlReader = new XMLReader(filePath);
		xPathfactory = XPathFactory.newInstance();
		xpath = xPathfactory.newXPath();
	}
	
	public NodeList getNodeListByXpath(String expression) {
		NodeList nodeList;
		try{
			XPathExpression xpathExpression = xpath.compile(expression);
			nodeList = (NodeList) xpathExpression.evaluate(xmlReader.getDoc(), XPathConstants.NODESET);
		}catch (XPathExpressionException e) {
			return null;
		}
		return nodeList;
	}

}
