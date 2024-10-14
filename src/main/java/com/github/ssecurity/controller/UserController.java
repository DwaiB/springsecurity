package com.github.ssecurity.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.ssecurity.model.AppUser;
import com.github.ssecurity.service.UserDetailsServices;

@RestController
@RequestMapping("/users")
public class UserController {
	
	@Autowired
	private UserDetailsServices userService;
	
	@GetMapping("/me")
	public ResponseEntity<AppUser>  authenticatedUser(){
		AppUser user = (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return ResponseEntity.ok(user);
	}
	
	@GetMapping("/")
	public ResponseEntity<List<AppUser>> allUsers(){
		return ResponseEntity.ok(userService.allUsers());
	}
}
