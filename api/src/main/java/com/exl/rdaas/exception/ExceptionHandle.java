package com.exl.rdaas.exception;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.reactive.function.client.ClientResponse;

import com.fasterxml.jackson.databind.JsonNode;

import reactor.core.publisher.Mono;

@ControllerAdvice
public class ExceptionHandle {

  @ResponseBody
  @ExceptionHandler(value = BaseException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public Mono<String> badRequest(BaseException clientResponse) {
	  return Mono.just(clientResponse.getMessage());
  }
}