package com.exl.rdaas.model;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.exl.rdaas.util.ExperianJsonProfileProvider;


@Component("authDetails")
public class AuthDetails implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Autowired
	ExperianJsonProfileProvider experianJsonProfileProvider;
	

	public String getContenttype(String serviceName, String tenantId) throws Exception {
		return experianJsonProfileProvider.getProperty(serviceName, "contenttype", tenantId).toString();
	}

	public String getHttpmethod(String serviceName, String tenantId) throws Exception {
		return experianJsonProfileProvider.getProperty(serviceName, "httpmethod", tenantId).toString();
	}

	public String getTokenbaseurl(String serviceName, String tenantId) throws Exception{
		return experianJsonProfileProvider.getProperty(serviceName, "keybaseurl", tenantId).toString();
	}

	public String getAuthtype(String serviceName, String tenantId) throws Exception {
		return experianJsonProfileProvider.getProperty(serviceName, "authtype", tenantId).toString();
	}

	public String getTokenuri(String serviceName, String tenantId) throws Exception{
		return experianJsonProfileProvider.getProperty(serviceName, "keyuri", tenantId).toString();
	}
	
}
