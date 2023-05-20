package com.exl.rdaas.jwt.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.exl.rdaas.jwt.model.Product;

public interface ProviderRepository extends JpaRepository<Product, Long> {

}
