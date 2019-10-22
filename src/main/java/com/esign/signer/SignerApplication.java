package com.esign.signer;
 

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;  


//@ComponentScan("com.esign.signer") 
//@EnableAutoConfiguration
@SpringBootApplication
public class SignerApplication {

	public static void main(String[] args) {

		System.out.println("-----------------SİGNER APP START-------------------");
		SpringApplication.run(SignerApplication.class, args);

		System.out.println("-----------------SİGNER APP RUN-------------------");
		// SampleBase.init();
	}
 
}
