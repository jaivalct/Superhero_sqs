package com.cleartax.training_superheroes.services;

import com.cleartax.training_superheroes.config.SqsConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.cleartax.training_superheroes.entities.Superhero;
import com.cleartax.training_superheroes.repos.SuperheroRepository;

@Service
public class SuperheroConsumer {
  @Autowired
  private SqsConfig sqsConfig;
  @Autowired
  private SqsClient sqsClient;
  @Autowired
  private SuperheroRepository superheroRepository;

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

        try {
          // Deserialize the message body to Superhero object
          Superhero superhero = new ObjectMapper().readValue(body, Superhero.class);

          // Check if superhero exists
          Superhero existingSuperhero = superheroRepository.findByName(superhero.getName());
          if (existingSuperhero != null) {
            // Update the power of the existing superhero
            existingSuperhero.setPower(superhero.getPower());
            superheroRepository.save(existingSuperhero);
            System.out.println("Updated superhero: " + superhero.getName() + " with new power: " + superhero.getPower());
          } else {
            // Save the new superhero
            // superheroRepository.save(superhero);
            // System.out.println("Created new superhero: " + superhero.getName() + " with power: " + superhero.getPower());
            System.out.println("Superhero not found in database");
          }

          // Delete the message after processing
          sqsClient.deleteMessage(DeleteMessageRequest.builder()
                  .queueUrl(sqsConfig.getQueueUrl())
                  .receiptHandle(message.receiptHandle())
                  .build());
        } catch (JsonProcessingException e) {
          System.err.println("Error processing message: " + e.getMessage());
        }
      });
    } catch (SqsException e) {
      System.err.println("SQS error: " + e.awsErrorDetails().errorMessage());
      System.out.println("The queue might be empty!!");
    }
  }
}
