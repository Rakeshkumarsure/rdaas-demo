package com.exl.rdaas.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class XmlHelper {
	
	private DocumentBuilderFactory documentBuilderFactory;
	private DocumentBuilder documentBuilder;

	public XmlHelper() {
		try {
			this.documentBuilderFactory = DocumentBuilderFactory.newInstance();
			this.documentBuilder = this.documentBuilderFactory.newDocumentBuilder();
		} catch (ParserConfigurationException pce) {
			// log error
		}
	}

	// Simple test
	public static void main(String argv[]) {
		XmlHelper xmlHelper = new XmlHelper();
          try {
              String xmlDoc = "<?xml version=\"1.0\"?><flatFields><java>6.0</java><xml>yes</xml><tags>given tags</tags><doc /><multipleRow /></flatFields>";
              Document doc = xmlHelper.getDocumentBuilder().parse(
                      xmlHelper.getInputStream(xmlDoc)
              );
              doc.getDocumentElement().normalize();
              NodeList nodeLst = doc.getDocumentElement().getChildNodes();
              Map elemen = xmlHelper.getElementKeyValue(nodeLst);
              Iterator it = elemen.entrySet().iterator();
              while (it.hasNext()) {
                  Map.Entry pairs = (Map.Entry)it.next();
                  	System.out.println(pairs.getKey() + " = " + pairs.getValue());
              }
            } catch (SAXParseException e) {
               //log error
            } catch (SAXException e) {
                   //log error
            } catch (IOException e) {
                   //log error
            }
    }

	/**
	 * Provides NodeList from the given Document object by the tagName
	 * 
	 * @param Document - doc
	 * @param tagname
	 * @return NodeList
	 */
	public NodeList getNodeListByTag(Document doc, String tagname) {
		NodeList nodeList = null;
		if (doc != null && !tagname.isEmpty()) {
			nodeList = doc.getElementsByTagName(tagname);
		}
		return nodeList;
	}

	/**
	 *
	 * @param nodeList
	 * @return Map
	 */
	public Map getElementKeyValue(NodeList nodeList) {
		Map elements = new HashMap();
		if (nodeList != null && nodeList.getLength() > 0) {
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i); // Take the node from the list
				NodeList valueNode = node.getChildNodes(); // get the children of the node
				String value = (valueNode.item(0) != null) ? valueNode.item(0).getNodeValue() : "";
				elements.put(node.getNodeName(), value);
			}
		}
		return elements;
	}

	/**
	 * Returns InputString given string
	 * 
	 * @param string
	 * @return InputStream
	 */
	public InputStream getInputStream(String string) {
		InputStream inputStream = null;
		if (!string.isEmpty()) {
			try {
				inputStream = new ByteArrayInputStream(string.getBytes("UTF-8"));
			} catch (UnsupportedEncodingException uee) {
			}
		}
		return inputStream;
	}

	/**
	 * Setters and getters
	 * 
	 * @return
	 */
	public DocumentBuilderFactory getDocumentBuilderFactory() {
		return documentBuilderFactory;
	}

	public void setDocumentBuilderFactory(DocumentBuilderFactory documentBuilderFactory) {
		this.documentBuilderFactory = documentBuilderFactory;
	}

	public DocumentBuilder getDocumentBuilder() {
		return documentBuilder;
	}

	public void setDocumentBuilder(DocumentBuilder documentBuilder) {
		this.documentBuilder = documentBuilder;
	}
}