package com.tgfcodes.beer.config;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.tgfcodes.beer.config.format.BigDecimalFormatter;
import com.tgfcodes.beer.controller.converter.CidadeConverter;
import com.tgfcodes.beer.controller.converter.EstadoConverter;
import com.tgfcodes.beer.controller.converter.EstiloConverter;
import com.tgfcodes.beer.controller.converter.GrupoConverter;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
	public void addFormatters(FormatterRegistry registry) {
	    registry.addConverter(new EstiloConverter());
	    registry.addConverter(new EstadoConverter());
	    registry.addConverter(new CidadeConverter());
	    registry.addConverter(new GrupoConverter());
	    
	    BigDecimalFormatter bigDecimalFormatter = new BigDecimalFormatter("#,##0.00");
	    registry.addFormatterForFieldType(BigDecimal.class, bigDecimalFormatter);
	    BigDecimalFormatter integerFormatter = new BigDecimalFormatter("#,##0");
		registry.addFormatterForFieldType(Integer.class, integerFormatter);
		
		DateTimeFormatterRegistrar dateTimeFormatter = new DateTimeFormatterRegistrar();
		dateTimeFormatter.setDateFormatter(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
		dateTimeFormatter.setTimeFormatter(DateTimeFormatter.ofPattern("HH:mm"));
		dateTimeFormatter.registerFormatters(registry);
		
    }
    
	@Bean
	public MessageSource messageSource() {
		ReloadableResourceBundleMessageSource bundle = new ReloadableResourceBundleMessageSource();
		bundle.setBasename("classpath:/messages");
		bundle.setDefaultEncoding("UTF-8"); 
		return bundle;
	}
	
	 @Bean
	 public CacheManager cacheManager() {
		 CaffeineCacheManager cacheManager = new CaffeineCacheManager("customer");
		 cacheManager.setCaffeine(caffeineCacheBuilder());
		 return cacheManager;
	 }

	 private Caffeine <Object, Object> caffeineCacheBuilder() {
		 return Caffeine.newBuilder()
				  .initialCapacity(3)
				  .maximumSize(3)
				  .expireAfterAccess(30, TimeUnit.SECONDS)
				  .weakKeys()
				  .recordStats();
	 }
}
