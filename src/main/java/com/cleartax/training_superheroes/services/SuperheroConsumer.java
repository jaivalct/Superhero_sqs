package com.cleartax.training_superheroes.services;

import com.cleartax.training_superheroes.config.SqsConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

@Service
public class SuperheroConsumer {
  @Autowired
  private SqsConfig sqsConfig;
  @Autowired
  private SqsClient sqsClient;

  @Scheduled(fixedDelay = 5000)
  public void consumeSuperhero() {
    try {
      ReceiveMessageResponse receiveMessageResponse = sqsClient.receiveMessage(ReceiveMessageRequest.builder()
              .queueUrl(sqsConfig.getQueueUrl())
              .maxNumberOfMessages(1)  // Adjust according to your needs
              .waitTimeSeconds(10) // Enable long polling
              .build());

      receiveMessageResponse.messages().forEach(message -> {
        // Process the message
        String body = message.body();
        System.out.println("Received message: " + body);

        // Delete the message after processing
        sqsClient.deleteMessage(DeleteMessageRequest.builder()
                .queueUrl(sqsConfig.getQueueUrl())
                .receiptHandle(message.receiptHandle())
                .build());
      });
    } catch (SqsException e) {
      System.err.println("SQS error: " + e.awsErrorDetails().errorMessage());
      System.out.println("The queue might be empty!!");
    }

  }
}