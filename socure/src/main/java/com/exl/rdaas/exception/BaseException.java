package com.exl.rdaas.exception;

import org.springframework.http.HttpStatus;

public class BaseException extends Exception{

    private static final long serialVersionUID = 1L;
    
	private HttpStatus status;
    private String desc;

    public BaseException(HttpStatus status, String desc) {
        this.status = status;
        this.desc = desc;
    }
}
