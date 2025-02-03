package com.example.scm;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.scm.services.MailService;

@SpringBootTest
class ScmApplicationTests {

	@Autowired
	private MailService mailService; 

	@Test
	void sendEmail() {
		mailService.sendEmail("deve73kumar@gmail.com", "this is my first email from scm project", "Hello from Scm project");
	}

}
