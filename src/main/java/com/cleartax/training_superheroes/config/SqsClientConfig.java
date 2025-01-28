package com.cleartax.training_superheroes.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import java.net.URI;

@Configuration
public class SqsClientConfig {

  private final SqsConfig sqsConfig;

  public SqsClientConfig(SqsConfig sqsConfig) {
    this.sqsConfig = sqsConfig;
  }

  @Bean
  public SqsClient sqsClient() {
    return SqsClient.builder()
        .region(Region.of(sqsConfig.getRegion()))
        .credentialsProvider(StaticCredentialsProvider.create(
            AwsBasicCredentials.create(
                sqsConfig.getAccessKey(),
                sqsConfig.getSecretKey()
            )
        ))
        .endpointOverride(URI.create(sqsConfig.getLocalstackEndpoint()))
        .overrideConfiguration(ClientOverrideConfiguration.builder()
            .build())
        .build();
  }
}