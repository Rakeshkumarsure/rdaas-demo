package com.exl.rdaas.jwt.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.exl.rdaas.jwt.model.TenantDetails;

public interface TenantDetailsRepository extends JpaRepository<TenantDetails, Long> {

	TenantDetails findByTenantid(String tenantid);
}
