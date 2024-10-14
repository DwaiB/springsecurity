package com.github.ssecurity.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.ssecurity.model.AppUser;
import com.github.ssecurity.repository.UserRepository;

@Service
public class UserDetailsServices {

	@Autowired
	private UserRepository repository;
	
	public List<AppUser> allUsers(){
		 List<AppUser> users = new ArrayList<>();
		 repository.findAll().forEach(users::add);
	        return users;
	}
	
}
