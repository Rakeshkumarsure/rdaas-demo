package com.exl.rdaas.model;

import org.w3c.dom.Document;

import com.fasterxml.jackson.databind.JsonNode;

public class ProviderNode {

	private JsonNode jsonNode;
	private Document xmlNode;
	private boolean includeProviders;
	private boolean isConvertible;

	public ProviderNode() {
	}

	public JsonNode getJsonNode() {
		return jsonNode;
	}

	public void setJsonNode(JsonNode jsonNode) {
		this.jsonNode = jsonNode;
	}

	public Document getXmlNode() {
		return xmlNode;
	}

	public void setXmlNode(Document xmlNode) {
		this.xmlNode = xmlNode;
	}

	public boolean isIncludeProviders() {
		return includeProviders;
	}

	public void setIncludeProviders(boolean includeProviders) {
		this.includeProviders = includeProviders;
	}

	public boolean isConvertible() {
		return isConvertible;
	}

	public void setConvertible(boolean isConvertible) {
		this.isConvertible = isConvertible;
	}
}
