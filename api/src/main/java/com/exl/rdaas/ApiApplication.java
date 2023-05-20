package com.exl.rdaas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import com.exl.rdaas.util.CustomConfig;
import com.exl.rdaas.util.WebSecurityConfig;

/**
 * @author Gurbaj Singh
 *
 */


@Import({
		CustomConfig.class,  WebSecurityConfig.class })
@SpringBootApplication(scanBasePackages = "com.exl.rdaas")
public class ApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiApplication.class, args);
	}
}