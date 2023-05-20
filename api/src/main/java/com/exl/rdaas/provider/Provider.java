package com.exl.rdaas.provider;

import java.io.Serializable;
import java.util.Map;

public interface Provider extends Serializable{
	
	public String getName();
	
	public String getContentType();
	
	public String getHttpMethod();
	
	public String getBaseUrl();
	
	public String getUri();
	
	public String getKey();
	
	public Map<String, String> getHeaders();
}