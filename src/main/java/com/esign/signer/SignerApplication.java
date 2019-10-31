package com.esign.signer;
 

import org.springframework.boot.SpringApplication; 
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;  

/**
 *SignerApplication.java sınıfı main sınıfıdır.
 *Uygulamayı ayağa kaldırır.@ComponentScan anotasyonu sayesinde componentleri hangi dizinden itibaren tarayacağını söylüyoruz.
 * 
 * @author mujdat.karacan
 *
 */
@ComponentScan(basePackages =  "com.esign") 
//@EnableAutoConfiguration
@SpringBootApplication
public class SignerApplication {

	public static void main(String[] args) {

		System.out.println("-----------------SİGNER APP START-------------------");
		SpringApplication.run(SignerApplication.class, args);

		System.out.println("-----------------SİGNER APP RUN-------------------"); 
	}
 
}
