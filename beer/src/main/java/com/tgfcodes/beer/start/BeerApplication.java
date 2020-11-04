package com.tgfcodes.beer.start;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EntityScan(basePackages = {"com.tgfcodes.beer.model"})
@ComponentScan(basePackages = {"com.tgfcodes.beer.*"})
@EnableJpaRepositories(basePackages = {"com.tgfcodes.beer.repository"})
@EnableTransactionManagement
@EnableAsync
@EnableCaching
public class BeerApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(BeerApplication.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(BeerApplication.class);
	}

}
