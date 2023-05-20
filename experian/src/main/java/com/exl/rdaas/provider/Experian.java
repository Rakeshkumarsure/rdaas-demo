package com.exl.rdaas.provider;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.exl.rdaas.util.Connector;
import com.exl.rdaas.util.ExperianJsonProfileProvider;
import com.exl.rdaas.util.KeyProvider;

@Controller("experian")
public class Experian extends Connector{

	@Autowired
	ExperianJsonProfileProvider experianJsonProfileProvider;
	
	@Autowired
	KeyProvider keyProvider;
	
	private String serviceName;
	
	private String tenantId;
	
	public Experian() {
	}
	
	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	
	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	@Override
	public String getUri() throws Exception  {
		return this.experianJsonProfileProvider.getProperty(serviceName, "uri", tenantId).toString();
	}

	@Override
	public String getBaseUrl() throws Exception {
		return this.experianJsonProfileProvider.getProperty(serviceName, "baseurl", tenantId).toString();
	}

	@Override
	public String getContentType() throws Exception {
		return this.experianJsonProfileProvider.getProperty(serviceName, "contenttype", tenantId).toString();
	}

	@Override
	public String getHttpMethod() throws Exception {
		return this.experianJsonProfileProvider.getProperty(serviceName, "httpmethod", tenantId).toString();
	}

	
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, String> getHeaders() throws Exception {
		 
	  Map<String, String> map = (Map<String, String>) this.experianJsonProfileProvider.getProperty(serviceName, "headers", tenantId);
	  map.put("Authorization", "Bearer "+ this.keyProvider.getAuthKey(serviceName, tenantId));
	  return map;
	}

	@Override
	public String getKey() throws Exception {
		//Get key from Experian
		return this.keyProvider.getAuthKey(serviceName, tenantId);
	}

}

