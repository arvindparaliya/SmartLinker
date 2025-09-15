package com.smartlinker;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.smartlinker.services.EmailService;

@SpringBootTest
class ApplicationTests {

	
	@Test
	void contextLoads() {
	}

	@Autowired
	private EmailService service;


	void sendEmailTest() {
		service.sendEmail("arvindiim2023@gmail.com", "Just managing the emails",
				"this is smartlinker project working on email service");
	}


// 	void testUnits() {


// 		int result=40;

// 		 List<String>  list = List.of("arjun","shyam","ankit");

// //		assertThat(result).isEqualTo(50);

// 		 assertThat(list).asList().size().isGreaterThan(5);




// 	}

}
