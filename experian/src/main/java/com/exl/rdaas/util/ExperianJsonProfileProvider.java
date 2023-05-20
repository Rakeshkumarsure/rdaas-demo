package com.exl.rdaas.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.exl.rdaas.exception.BaseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;

@Component("experianJsonProfileProvider")
public class ExperianJsonProfileProvider implements ProfileProvider{

  @Autowired
  private ObjectMapper objectMapper;
  
  private Map<String, Map<String, Object>> jsonMaps = new HashMap<>();
  
  @PostConstruct
  @Override
  public void loader() throws IOException {
	  Resource[] resources = new PathMatchingResourcePatternResolver().getResources("/experian/*.json");
      for (Resource resource : resources) {
          InputStream inputStream = resource.getInputStream();
          Map<String, Object> jsonMap = objectMapper.readValue(inputStream, new TypeReference<Map<String, Object>>() {});
          String fileName = resource.getFilename();
          String tenantId = fileName.substring(0, fileName.lastIndexOf("."));
          jsonMaps.put(tenantId, jsonMap);
      }
  }
  
  @Override
  public Object getProperty(String serviceName, String key, String tenantId) throws BaseException {
      Map<String, Object> jsonMap = jsonMaps.get(tenantId);
      if (jsonMap == null) {
          throw new BaseException(HttpStatus.INTERNAL_SERVER_ERROR, "JSON file not found for tenantId: " + tenantId);
      }
      Map<String, Object> serviceObj = (Map<String, Object>) jsonMap.get(serviceName);
      if (serviceObj == null) {
    	  throw new BaseException(HttpStatus.BAD_REQUEST,"Service not found in JSON file for serviceName: " + serviceName);
      }
      return serviceObj.get(key);
  }

}
