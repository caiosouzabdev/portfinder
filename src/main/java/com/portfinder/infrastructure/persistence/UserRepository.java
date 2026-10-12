package com.portfinder.infrastructure.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

	Optional<UserEntity> findByGoogleSub(String googleSub);

	Optional<UserEntity> findByEmailIgnoreCase(String email);
}
