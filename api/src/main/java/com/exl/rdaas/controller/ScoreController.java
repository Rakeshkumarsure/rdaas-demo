package com.exl.rdaas.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.exl.rdaas.jwt.util.JWtUtils;
import com.exl.rdaas.util.ResponseEntityFactory;

import jakarta.servlet.http.HttpServletRequest;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("api/")
public class ScoreController {

	private final ResponseEntityFactory entityFactory;
	
	@Autowired
	public ScoreController(ResponseEntityFactory entityFactory) {
		this.entityFactory = entityFactory;
	}

	@PostMapping(value = "v1/rdaas", produces = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE }, consumes = { MediaType.APPLICATION_JSON_VALUE,
					MediaType.APPLICATION_XML_VALUE })
	public Mono<ResponseEntity<String>> postScore(@RequestBody String pScore,
			@RequestHeader("Content-type") String contentType, HttpServletRequest request) throws Exception {

		String token = request.getHeader("Authorization").replace("Bearer ", "");
		String tenantId = JWtUtils.getTenantIdFromToken(token);

		return this.entityFactory.createResponseEntity(pScore, contentType, tenantId);
	}


}
