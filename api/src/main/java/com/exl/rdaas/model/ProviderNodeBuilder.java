package com.exl.rdaas.model;



import org.w3c.dom.Document;

import com.fasterxml.jackson.databind.JsonNode;

public class ProviderNodeBuilder {
	
    private JsonNode jsonNode;
    private Document xmlNode;
    private boolean includeProviders;

    public ProviderNodeBuilder setJsonNode(JsonNode jsonNode) {
        this.jsonNode = jsonNode;
        return this;
    }

    public ProviderNodeBuilder setXmlNode(Document xmlNode) {
        this.xmlNode = xmlNode;
        return this;
    }

    public ProviderNodeBuilder setIncludeProviders(boolean includeProviders) {
        this.includeProviders = includeProviders;
        return this;
    }

    public ProviderNode build() {
        ProviderNode providerNode = new ProviderNode();
        providerNode.setJsonNode(jsonNode);
        providerNode.setXmlNode(xmlNode);
        providerNode.setIncludeProviders(includeProviders);
        return providerNode;
    }
}

