package com.cleartax.training_superheroes.services;

import com.cleartax.training_superheroes.config.SqsConfig;
import io.awspring.cloud.sqs.annotation.SqsListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
//import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.cleartax.training_superheroes.entities.Superhero;
import com.cleartax.training_superheroes.repos.SuperheroRepository;

@Service
public class SuperheroConsumer {
  @Autowired
  private SqsConfig sqsConfig;

  @Autowired
  private SuperheroRepository superheroRepository;

  @Value("${sqs.queue.url}")
  private String queueUrl;

  @SqsListener("${sqs.queue.name}")
  public void consumeSuperhero(String messageBody) {
    try {
      // Process the message
      System.out.println("Received message: " + messageBody);

      try {
        // Deserialize the message body to Superhero object
        Superhero superhero = new ObjectMapper().readValue(messageBody, Superhero.class);

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

        // If you need to delete the message, you can use sqsClient here
        // sqsClient.deleteMessage(...);
      } catch (JsonProcessingException e) {
        System.err.println("Error processing message: " + e.getMessage());
      }
    } catch (Exception e) {
      System.err.println("Error: " + e.getMessage());
      System.out.println("The queue might be empty!!");
    }
  }
}
