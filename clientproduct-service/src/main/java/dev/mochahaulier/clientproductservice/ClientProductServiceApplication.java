package dev.mochahaulier.clientproductservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class ClientProductServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(ClientProductServiceApplication.class, args);
	}
}
