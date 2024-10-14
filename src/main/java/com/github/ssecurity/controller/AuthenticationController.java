package com.github.ssecurity.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.ssecurity.dto.LoginUserDto;
import com.github.ssecurity.dto.RegisterUserDto;
import com.github.ssecurity.dto.VerifyUserDto;
import com.github.ssecurity.model.AppUser;
import com.github.ssecurity.response.LoginResponse;
import com.github.ssecurity.service.AuthenticationService;
import com.github.ssecurity.service.JwtService;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
	
	@Autowired
	private AuthenticationService authService;
	@Autowired
	private JwtService jwtService;
	
	@PostMapping("/signup")
	public ResponseEntity<AppUser> register(@RequestBody RegisterUserDto registerUserDto){
		System.out.println("Calling register");
		AppUser user = authService.signup(registerUserDto);
		return ResponseEntity.ok(user);
	}
	
	@PostMapping("/login")
	public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginUserDto loginUserDto) {
		AppUser user = authService.authenticate(loginUserDto);
		String jwtToken = jwtService.generateToken(user);
		LoginResponse response = new LoginResponse(jwtToken, jwtService.getExpirationTime());
		return ResponseEntity.ok(response);
	}
	
	@PostMapping("/verify")
	public ResponseEntity<?> verifyUser(@RequestBody VerifyUserDto verifyUserDto){
		try {
			authService.verifyUser(verifyUserDto);
			return ResponseEntity.ok("Account Verified Succesfully");
		}catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@PostMapping("/resend")
	public ResponseEntity<?> resendVerificationCode(@RequestParam String email){
		try {
			authService.resendVerificationCode(email);
			return ResponseEntity.ok("Verification Code sent");
		}catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
}
