package com.tgfcodes.beer.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

@Component
@Configuration
public class S3Config {

	@Value("${amazon.access.key}")
	private String ACCESSKEY;
	@Value("${amazon.secret.key")
	private String SECRETKEY;

	@Bean
	public AmazonS3 amazonS3() {
		BasicAWSCredentials basicAWSCredentials = new BasicAWSCredentials(ACCESSKEY, SECRETKEY);
		AmazonS3 amazonS3 = AmazonS3ClientBuilder.standard().withRegion(Regions.US_EAST_1)
				.withCredentials(new AWSStaticCredentialsProvider(basicAWSCredentials)).build();
		return amazonS3;
	}
	
}
