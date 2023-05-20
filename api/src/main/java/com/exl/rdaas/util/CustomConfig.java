package com.exl.rdaas.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.util.AntPathMatcher;

import com.fasterxml.jackson.databind.ObjectMapper;

public class CustomConfig {

	@Autowired
	private ApplicationContext context;

	@Autowired
	ApplicationConfig applicationConfig;

	@Bean
	ObjectMapper objectMapper() {
		return new ObjectMapper();
	}
	
	@Bean
	AntPathMatcher antPathMatcher() {
		return new AntPathMatcher();
	}
	
	 @Bean
	    public MappingJackson2HttpMessageConverter jsonConverter() {
	        return new MappingJackson2HttpMessageConverter();
	    }

	    @Bean
	    public MappingJackson2XmlHttpMessageConverter xmlConverter() {
	        return new MappingJackson2XmlHttpMessageConverter();
	    }
	
		/*
		 * @Bean Map<String, Connector> rdaasProviders() { Map<String, Connector>
		 * connectorMap = new HashMap<>();
		 * applicationConfig.getTenantDetails().forEach((connectorName,
		 * tenantDetailsList) -> { for (ApplicationConfig.TenantDetails tenantDetail :
		 * tenantDetailsList) { if (null != tenantDetail.getName()) { Object lObj =
		 * (Object) context.getBean(tenantDetail.getName()); Connector lConnector =
		 * (Connector) lObj; lConnector.setTenantId(connectorName);
		 * connectorMap.put(tenantDetail.getName(), lConnector); } } }); return
		 * connectorMap; }
		 */

}
