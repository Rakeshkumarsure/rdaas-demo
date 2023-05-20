package com.exl.rdaas.util;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.function.Function;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.springframework.stereotype.Controller;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;

@Controller("convertor")
public class Convertor {

	final XmlMapper XML_MAPPER = (XmlMapper) new XmlMapper();

	// Function to convert XML to JSON
    public Function<Node, JsonNode> convertXmlToJson = xml -> {
        try {
            Transformer transformer = XmlParserFactory.getTransformerFactory();
            DOMSource source = new DOMSource(xml);
            StringWriter writer = new StringWriter();
            transformer.transform(source, new StreamResult(writer));
            String str = writer.toString();
            return XML_MAPPER.readValue(str, JsonNode.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    };

    // Function to convert JSON to XML
    public Function<JsonNode, Document> convertJsonToXml = jsonNode -> {
        try {
        	XML_MAPPER.configure(SerializationFeature.INDENT_OUTPUT, true);
        	XML_MAPPER.configure(ToXmlGenerator.Feature.WRITE_XML_DECLARATION, false);
        	XML_MAPPER.configure(ToXmlGenerator.Feature.WRITE_XML_1_1, false);
            ObjectWriter ow = XML_MAPPER.writer();
            StringWriter w = new StringWriter();
            ow.writeValue(w, jsonNode);
            DocumentBuilder builder = XmlParserFactory.getDocumentBuilder();
            return builder.parse(new InputSource(new StringReader(w.toString())));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    };

}
