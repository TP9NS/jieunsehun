package com.example.ourapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;



@SpringBootApplication
@EntityScan("com.example.ourapp") // 최상위 패키지를 스캔하여 모든 엔티티 포함
@ComponentScan("com.example.ourapp")
public class JieunsehunApplication {

	public static void main(String[] args) {
		SpringApplication.run(JieunsehunApplication.class, args);
		System.out.println("안녕 나는 멋진 세훈이야 ajtwlstpgns 아이디를 갖고 있지!");
	}

}
