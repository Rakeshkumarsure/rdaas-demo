package com.exl.rdaas.jwt.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.exl.rdaas.jwt.model.ERole;
import com.exl.rdaas.jwt.model.JwtResponse;
import com.exl.rdaas.jwt.model.LoginRequest;
import com.exl.rdaas.jwt.model.MessageResponse;
import com.exl.rdaas.jwt.model.Product;
import com.exl.rdaas.jwt.model.Role;
import com.exl.rdaas.jwt.model.SignupRequest;
import com.exl.rdaas.jwt.model.TenantDetails;
import com.exl.rdaas.jwt.model.User;
import com.exl.rdaas.jwt.repository.ProviderRepository;
import com.exl.rdaas.jwt.repository.RoleRepository;
import com.exl.rdaas.jwt.repository.TenantDetailsRepository;
import com.exl.rdaas.jwt.repository.UserDetailsImpl;
import com.exl.rdaas.jwt.repository.UserRepository;
import com.exl.rdaas.jwt.util.JWtUtils;

import jakarta.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api")
public class AuthController {
	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	UserRepository userRepository;

	@Autowired
	RoleRepository roleRepository;
	
	@Autowired
	TenantDetailsRepository tenantDetailsRepository;
	
	@Autowired
	ProviderRepository providerRepository;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	JWtUtils jwtUtils;

	@PostMapping("v1/rdaas/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
		
		

		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateJwtToken(authentication);

		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
		List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
				.collect(Collectors.toList());

		return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getUsername(), userDetails.getTenantid(), userDetails.getEmail(), roles)); 
	}

	@PostMapping("v1/rdaas/signup")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
		if (userRepository.existsByUsername(signUpRequest.getUsername())) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
		}

		if (userRepository.existsByEmail(signUpRequest.getEmail())) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
		}
		
		// For now, tenantID is generated below, will use better way to do so.
		final String tenantId = JWtUtils.generateTenantId();
		User user = new User(signUpRequest.getUsername(), signUpRequest.getEmail(),
				encoder.encode(signUpRequest.getPassword()), tenantId);

		
		//For now, Roles for user are defined here, will use better strategy to add roles per user.
		Set<Role> roles = new HashSet<>();
		Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
				.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
		roles.add(modRole);
		Role userRole = roleRepository.findByName(ERole.ROLE_USER)
				.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
		roles.add(userRole);

		user.setRoles(roles);
		
		//Again, for now, hardcoded the Products access for tenant.
		Product socureProduct = new Product("socure");
		Product experianProduct = new Product("experian");
		
		List<Product> productList = new ArrayList<>();
		productList.add(socureProduct);
		productList.add(experianProduct);
		
		List<Product> savedProducts = providerRepository.saveAll(productList);

		// Create a new TenantDetails object
		TenantDetails tenantDetails = new TenantDetails();
		tenantDetails.setTenantid(tenantId);
		tenantDetails.getProducts().addAll(savedProducts);
		tenantDetailsRepository.save(tenantDetails);
		User savedUser = userRepository.save(user);
		return ResponseEntity.ok(new MessageResponse("user saved successfully.", savedUser.getUsername(), savedUser.getTenantid() , savedUser.getEmail(), savedUser.getRoles()));
	}
}
