package com.example.ourapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;



@SpringBootApplication
@EntityScan("com.example.ourapp.entity")
@ComponentScan({"com.example.ourapp.user", "com.example.controller", "com.example.service"})
public class JieunsehunApplication {

	public static void main(String[] args) {
		SpringApplication.run(JieunsehunApplication.class, args);
		System.out.println("초지ㅣㅇ느 개나나바빠");
	}

}
