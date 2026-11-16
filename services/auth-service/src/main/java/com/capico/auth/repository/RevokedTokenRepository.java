package com.capico.auth.repository;

import com.capico.auth.entity.RevokedToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RevokedTokenRepository extends JpaRepository<RevokedToken, String> {
}
