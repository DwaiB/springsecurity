package com.github.ssecurity.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.github.ssecurity.dto.LoginUserDto;
import com.github.ssecurity.dto.RegisterUserDto;
import com.github.ssecurity.dto.VerifyUserDto;
import com.github.ssecurity.model.AppUser;
import com.github.ssecurity.repository.UserRepository;

import jakarta.mail.MessagingException;


@Service
public class AuthenticationService {
	@Autowired
	private UserRepository repository;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private EmailService emailService;
	
	public AppUser signup(RegisterUserDto input) {
		AppUser user = new AppUser(input.getUsername(),passwordEncoder.encode(input.getPassword()));
		System.out.println("Saving User: "+user.getUsername());
		user.setVerificationCode(generateVerificationCode());
		user.setEnabled(true);
		user.setVerificationExpiry(LocalDateTime.now().plusMinutes(15));
		sendVerificationEmail(user);
		repository.save(user);
		return user;
	}
	public String generateVerificationCode() {
		Random random = new Random();
		return String.valueOf(random.nextInt(900000)+100000);
		
	}
	public void sendVerificationEmail(AppUser user) {
		 String subject = "Account Verification";
	        String verificationCode = "VERIFICATION CODE " + user.getVerificationCode();
	        String htmlMessage = "<html>"
	                + "<body style=\"font-family: Arial, sans-serif;\">"
	                + "<div style=\"background-color: #f5f5f5; padding: 20px;\">"
	                + "<h2 style=\"color: #333;\">Welcome to our app!</h2>"
	                + "<p style=\"font-size: 16px;\">Please enter the verification code below to continue:</p>"
	                + "<div style=\"background-color: #fff; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0,0,0,0.1);\">"
	                + "<h3 style=\"color: #333;\">Verification Code:</h3>"
	                + "<p style=\"font-size: 18px; font-weight: bold; color: #007bff;\">" + verificationCode + "</p>"
	                + "</div>"
	                + "</div>"
	                + "</body>"
	                + "</html>";

	        try {
	            emailService.sendVerificationEmail(user.getEmail(), subject, htmlMessage);
	        } catch (MessagingException e) {
	            // Handle email sending exception
	            e.printStackTrace();
	        }
	}
	public void resendVerificationCode(String email) {
		Optional<AppUser> opuser = repository.findByName(email);
		if(opuser.isPresent()) {
			AppUser user = opuser.get();
			if (user.isEnabled()) {
                throw new RuntimeException("Account is already verified");
            }
            user.setVerificationCode(generateVerificationCode());
            user.setVerificationExpiry(LocalDateTime.now().plusMinutes(20));
            sendVerificationEmail(user);
            repository.save(user);
		}else {
			throw new RuntimeException("User Not Found");
		}
	}
	
	public AppUser authenticate(LoginUserDto input) {
		Optional<AppUser> opuser = repository.findByName(input.getEmail());
		if(!opuser.isPresent()) {
			throw new RuntimeException("User Not found");
		}
		AppUser user = opuser.get();
		authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));
		return user;
	}
	public void verifyUser(VerifyUserDto input) {
        Optional<AppUser> optionalUser = repository.findByName(input.getEmail());
        if (optionalUser.isPresent()) {
        	AppUser user = optionalUser.get();
            if (user.getVerificationExpiry().isBefore(LocalDateTime.now())) {
                throw new RuntimeException("Verification code has expired");
            }
            if (user.getVerificationCode().equals(input.getVerificationCode())) {
                user.setEnabled(true);
                user.setVerificationCode(null);
                user.setVerificationExpiry(null);
                repository.save(user);
            } else {
                throw new RuntimeException("Invalid verification code");
            }
        } else {
            throw new RuntimeException("User not found");
        }
    }
}
