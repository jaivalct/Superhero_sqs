package com.cleartax.training_superheroes.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class SqsConfig {

  @Value("${sqs.queue.name}")
  private String queueName;

  @Value("${sqs.queue.url}")
  private String queueUrl;

  @Value("${spring.cloud.aws.sqs.region}")
  private String region;

  @Value("${spring.cloud.aws.credentials.access-key}")
  private String accessKey;

  @Value("${spring.cloud.aws.credentials.secret-key}")
  private String secretKey;

  // @Value("${sqs.queue.session-token}")
  // private String sessionToken;

  @Value("${spring.cloud.aws.sqs.endpoint}")
  private String localstackEndpoint;

}

