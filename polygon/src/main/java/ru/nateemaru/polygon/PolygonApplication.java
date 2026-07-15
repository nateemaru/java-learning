package ru.nateemaru.polygon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@ConfigurationPropertiesScan(basePackages = "ru.nateemaru.polygon.configuration")
@EntityScan(basePackages = "ru.nateemaru.polygon.entity")
public class PolygonApplication {
	public static void main(String[] args) {
		SpringApplication.run(PolygonApplication.class, args);
	}
}
