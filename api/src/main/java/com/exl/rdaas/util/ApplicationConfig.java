package com.exl.rdaas.util;

import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties
public class ApplicationConfig {
	
	private Map<String, List<TenantDetails>> tenantDetails;

	public Map<String, List<TenantDetails>> getTenantDetails() {
		return tenantDetails;
	}

	public void setTenantDetails(Map<String, List<TenantDetails>> tenantDetails) {
		this.tenantDetails = tenantDetails;
	}
	
	public static class TenantDetails {
		private String name;
		private String baseUrl;
		private String uri;
		private String contenttype;
		private String httpmethod;
		

		public TenantDetails() {
		}
		
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getBaseUrl() {
			return baseUrl;
		}
		public void setBaseUrl(String baseUrl) {
			this.baseUrl = baseUrl;
		}
		public String getUri() {
			return uri;
		}
		public void setUri(String uri) {
			this.uri = uri;
		}
		public String getContenttype() {
			return contenttype;
		}
		public void setContenttype(String contenttype) {
			this.contenttype = contenttype;
		}
		public String getHttpmethod() {
			return httpmethod;
		}
		public void setHttpmethod(String httpmethod) {
			this.httpmethod = httpmethod;
		}
	}
}
