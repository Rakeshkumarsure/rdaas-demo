package com.exl.rdaas.util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "rdaas")
public class RdaasXmlResponse {

	private String requestId;
	
	
	private Document body;
	
	private String message;
	
	public RdaasXmlResponse() {
		
	}

	public RdaasXmlResponse( String requestId,Document body, String message) {
		this.body = body;
		this.message = message;
		this.requestId = requestId;
	}
	@XmlAnyElement
	public Element getBody() {
	    return body.getDocumentElement();
	}


	  @XmlElement
	public String getRequestId() {
		return requestId;
	}

	  @XmlElement
	public String getMessage() {
		return message;
	}


}