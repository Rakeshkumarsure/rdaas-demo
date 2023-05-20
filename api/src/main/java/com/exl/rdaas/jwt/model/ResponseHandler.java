package com.exl.rdaas.jwt.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.databind.JsonNode;

public class ResponseHandler implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public static ResponseEntity<Object> generateResponse(String responseId, String category, String message, HttpStatus status, JsonNode responseObj) {
        Map<String, Object> map = new HashMap<>();
        	map.put("requestId", responseId);
        	map.put("category", category);
            map.put("message", message);
            map.put("status", status.value());
            map.put("data", responseObj);

            return new ResponseEntity<Object>(map,status);
    }
	

}
