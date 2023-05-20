package com.exl.rdaas.provider;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.exl.rdaas.util.Connector;
import com.exl.rdaas.util.SocureJsonProfileProvider;

@Controller("socure")
public class Socure extends Connector{

	@Autowired
	SocureJsonProfileProvider socureJsonProfileProvider;
	
	private String tenantId;
	
	private String serviceName;
	
	public Socure() {
	}
	
	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}


	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	@Override
	public String getUri() throws Exception  {
		return socureJsonProfileProvider.getProperty(serviceName, "uri", tenantId).toString();
	}

	@Override
	public String getBaseUrl() throws Exception {
		return socureJsonProfileProvider.getProperty(serviceName, "baseuri", tenantId).toString();
	}

	@Override
	public String getContentType() throws Exception {
		return socureJsonProfileProvider.getProperty(serviceName, "contenttype", tenantId).toString();
	}

	@Override
	public String getHttpMethod() throws Exception {
		return socureJsonProfileProvider.getProperty(serviceName, "httpmethod", tenantId).toString();
	}

	@Override
	public String getKey() throws Exception {
		//Get key from AWS vault storage
		return socureJsonProfileProvider.getProperty(serviceName, "key", tenantId).toString();
	}

	
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, String> getHeaders() throws Exception {
		return  (Map<String, String>) socureJsonProfileProvider.getProperty(serviceName, "headers", tenantId);
	}

}

