package com.github.ssecurity.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.github.ssecurity.model.AppUser;

@Repository
public interface UserRepository extends CrudRepository<AppUser, Long>{

	Optional<AppUser> findByName(String username);
}
