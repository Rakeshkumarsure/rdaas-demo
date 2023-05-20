package com.exl.rdaas.util;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;

public class XmlParserFactory {
	
    private static final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    private static final TransformerFactory tf = TransformerFactory.newInstance();
	
    public static DocumentBuilder getDocumentBuilder() throws Exception {
        return factory.newDocumentBuilder();
    }
    
    public static Transformer getTransformerFactory() throws Exception {
    	return tf.newTransformer();
    }
}