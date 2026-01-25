package com.example.backend.controller;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
@RestController
public class TestController {
	
	@GetMapping("/api/test")
	public String test() {
		return("Backend is runnning");
	}

}
