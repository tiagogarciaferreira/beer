package com.tgfcodes.beer.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SegurancaController {

	@GetMapping(value = "/login")
	private String login(@AuthenticationPrincipal User user) {
		if (user != null) {
			return "redirect:/dashboard/";
		}
		return "Login";
	}
	
}
