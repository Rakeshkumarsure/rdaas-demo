package com.exl.rdaas.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.exl.rdaas.provider.ResponseEntityProvider;
import com.exl.rdaas.service.JsonScoreService;
import com.exl.rdaas.service.XmlScoreService;

import reactor.core.publisher.Mono;

@Service
public class ResponseEntityFactory implements ResponseEntityProvider {

	private final JsonScoreService jsonScoreService;
	private final XmlScoreService xmlScoreService;

	@Autowired
	public ResponseEntityFactory(XmlScoreService xmlScoreService, JsonScoreService jsonScoreService) {
		this.xmlScoreService = xmlScoreService;
		this.jsonScoreService = jsonScoreService;
	}

	/*
	 * @Override public ResponseEntity<String> createResponseEntity(String payload,
	 * String contentType) throws Exception {
	 * 
	 * switch (contentType) { case "application/json": return new
	 * ResponseEntity<String>(this.jsonScoreService.jsonResponse(payload),
	 * HttpStatus.OK); case "application/xml": return new
	 * ResponseEntity<String>(this.xmlScoreService.xmlResponse(payload),
	 * HttpStatus.OK); default: return new
	 * ResponseEntity<String>("Content-type not supported", HttpStatus.BAD_REQUEST);
	 * } }
	 */

	@Override
	public Mono<ResponseEntity<String>> createResponseEntity(String payload, String contentType, String tenantid) throws Exception {
		switch (contentType) {
		case "application/json":
			return this.jsonScoreService.jsonResponse(payload, tenantid)
					.map(jsonResponse -> new ResponseEntity<String>(jsonResponse, HttpStatus.OK));

		case "application/xml":
			return this.xmlScoreService.xmlResponse(payload, tenantid)
					.map(xmlResponse -> new ResponseEntity<String>(xmlResponse, HttpStatus.OK));

		default:
			return Mono.just(new ResponseEntity<String>("Content-type not supported", HttpStatus.BAD_REQUEST));
		}
	}

}
