package com.exl.rdaas.util;

import org.springframework.stereotype.Component;

@Component
public class KeyManager {
	
	/*
	 * public RSAKey rsaKey() throws Exception { try { KeyPairGenerator gen =
	 * KeyPairGenerator.getInstance("RSA"); gen.initialize(2048); var pair =
	 * gen.generateKeyPair(); RSAPublicKey rsaPublicKey =
	 * (RSAPublicKey)pair.getPublic(); RSAPrivateKey rsaPrivateKey =
	 * (RSAPrivateKey)pair.getPrivate(); return new RSAKey.Builder(rsaPublicKey)
	 * .privateKey(rsaPrivateKey) .keyID(UUID.randomUUID().toString()) .build(); }
	 * catch (NoSuchAlgorithmException e) { throw new Exception("Invalid key"); } }
	 */
}
