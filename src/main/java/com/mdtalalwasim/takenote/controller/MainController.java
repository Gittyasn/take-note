package com.mdtalalwasim.takenote.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;

import com.mdtalalwasim.takenote.entity.User;
import com.mdtalalwasim.takenote.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
public class MainController {
	
	@Autowired
	UserService userService;
	
	@ModelAttribute
	public void addLoggedInUser(java.security.Principal principal, org.springframework.ui.Model model) {
		if (principal != null) {
			User user = this.userService.getUserByEmail(principal.getName());
			model.addAttribute("loggedInUser", user);
		}
	}
	
	@GetMapping("/")
	public String index() {
		return "index";
	}
	
	@GetMapping("/register")
	public String register(org.springframework.ui.Model model, java.security.Principal principal) {
		if (principal != null) {
			return "redirect:/user/view-notes";
		}
		model.addAttribute("user", new User());
		return "register";
	}
	
	
	@PostMapping("/save-user")
	public String saveUser(@Valid @ModelAttribute User user, BindingResult bindingResult, HttpSession session) {
		if (bindingResult.hasErrors()) {
			session.setAttribute("message", bindingResult.getAllErrors().get(0).getDefaultMessage());
			return "redirect:/register";
		}
		User userExistOrNot = this.userService.getUserByEmail(user.getEmail());
		
		if(userExistOrNot != null) {
			session.setAttribute("message", "User already exist!");
		}else {
			
			User saveUser = this.userService.saveUser(user);
			
			if(saveUser!=null) {
				session.setAttribute("message", "User register successfully!");
			}else {
				session.setAttribute("message", "Something wrong on server!");
			}
			
		}
		
		
		System.out.println(user);

		return "redirect:/register";
	}
	
	
	@GetMapping("/signin")
	public String login(java.security.Principal principal) { 
		if (principal != null) {
			return "redirect:/user/view-notes";
		}
		return "login";
	}
	
	

}
