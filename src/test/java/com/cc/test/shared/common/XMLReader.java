package com.cc.test.shared.common;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XMLReader {
	
	private DocumentBuilderFactory docBuilderFactory;
	private DocumentBuilder docBuilder;
	private Document doc;
	private NodeList nodeList;
	
	private final String XML_FILE = "webElements.xml";
	
	public XMLReader(String filePath) throws SAXException, IOException, ParserConfigurationException {
		File file = new File(filePath);
		docBuilderFactory = DocumentBuilderFactory.newInstance();
		docBuilder = docBuilderFactory.newDocumentBuilder();
		doc = docBuilder.parse(file);
		doc.getDocumentElement().normalize();
	}
	
	public NodeList getElementsByTagName(String tagName) {
		return doc.getElementsByTagName(tagName);
	}
	
	public NodeList getElementsByXPath(String expression) throws Exception {
		XPathFactory xPathfactory = XPathFactory.newInstance();
		XPath xpath = xPathfactory.newXPath();
		XPathExpression expr = xpath.compile(expression);
		NodeList nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
		return nl;
	}
	
	public Element getElementById(String id){
		return doc.getElementById(id);
	}
	
	public DocumentBuilderFactory getDocBuilderFactory() {
		return docBuilderFactory;
	}

	public void setDocBuilderFactory(DocumentBuilderFactory docBuilderFactory) {
		this.docBuilderFactory = docBuilderFactory;
	}
	
	public DocumentBuilder getDocBuilder() {
		return docBuilder;
	}

	public void setDocBuilder(DocumentBuilder docBuilder) {
		this.docBuilder = docBuilder;
	}
	
	public Document getDoc() {
		return doc;
	}

	public void setDoc(Document doc) {
		this.doc = doc;
	}
	
	public NodeList getNodeList() {
		return nodeList;
	}

	public void setNodeList(NodeList nodeList) {
		this.nodeList = nodeList;
	}


}
