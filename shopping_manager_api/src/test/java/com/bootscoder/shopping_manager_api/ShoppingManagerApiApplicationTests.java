package com.bootscoder.shopping_manager_api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.beans.Encoder;

@SpringBootTest
class ShoppingManagerApiApplicationTests {
    @Autowired
    private PasswordEncoder passwordEncoder;

	@Test
	void contextLoads() {
        String boots = passwordEncoder.encode("boots");
        System.out.printf("password:"+boots+"!!!");
	}

}
