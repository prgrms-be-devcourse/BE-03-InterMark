package com.prgrms.be.intermark.common.config;

import java.io.IOException;
import java.net.ServerSocket;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.AnonymousAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

import io.findify.s3mock.S3Mock;

@TestConfiguration
public class S3MockConfig {

	private final int port = new ServerSocket(0).getLocalPort();

	public S3MockConfig() throws IOException {
	}

	@Bean(name = "s3Mock")
	public S3Mock s3Mock() {
		return new S3Mock.Builder()
			.withPort(port)
			.withInMemoryBackend()
			.build();
	}

	@Primary
	@Bean(name = "amazonS3", destroyMethod = "shutdown")
	public AmazonS3 amazonS3() {
		AwsClientBuilder.EndpointConfiguration endpoint = new AwsClientBuilder.EndpointConfiguration(
			"http://127.0.0.1:" + port, Regions.AP_NORTHEAST_2.name());
		AmazonS3 client = AmazonS3ClientBuilder
			.standard()
			.withPathStyleAccessEnabled(true)
			.withEndpointConfiguration(endpoint)
			.withCredentials(new AWSStaticCredentialsProvider(new AnonymousAWSCredentials()))
			.build();
		return client;
	}

}