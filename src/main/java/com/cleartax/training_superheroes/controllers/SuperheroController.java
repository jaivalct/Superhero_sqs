package com.cleartax.training_superheroes.controllers;

import com.cleartax.training_superheroes.config.SqsConfig;
import com.cleartax.training_superheroes.entities.Superhero;
import com.cleartax.training_superheroes.dto.SuperheroRequestBody;
import com.cleartax.training_superheroes.services.QueueService;
import com.cleartax.training_superheroes.services.SuperheroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

import java.util.List;

@RestController
public class SuperheroController {

    private SuperheroService superheroService;

    @Autowired
    private SqsConfig sqsConfig;

    @Autowired
    private SqsClient sqsClient;

    @Autowired
    private QueueService queueService;

    @Autowired
    public SuperheroController(SuperheroService superheroService, SqsClient sqsClient){
        this.superheroService = superheroService;
        this.sqsClient = sqsClient;
    }

    @GetMapping("/hello")
    public String hello(@RequestParam(value = "username", defaultValue = "World") String username) {
        sqsClient.sendMessage(SendMessageRequest.builder()
            .queueUrl(sqsConfig.getQueueUrl())
            .messageBody("SpiderMan")
            .build());

        return String.format("Hello %s!, %s", username);
    }

    @GetMapping("/update_superhero_async")
    public String updateSuperhero(@RequestParam(value = "superHeroName", defaultValue = "ironMan") String superHeroName) {
        SendMessageResponse result = sqsClient.sendMessage(SendMessageRequest.builder()
            .queueUrl(sqsConfig.getQueueUrl())
            .messageBody(superHeroName)
            .build());

        return String.format("Message sent to queue with message id %s and superHero %s", result.messageId(), superHeroName);
    }

    @PostMapping("/superhero")
    public Superhero persistSuperhero(@RequestBody SuperheroRequestBody superheroRequestBody){
        return superheroService.persistSuperhero(superheroRequestBody, superheroRequestBody.getUniverse());
    }

    @PostMapping("/push_all_superheroes")
    public String pushAllSuperheroes() {
        superheroService.pushAllSuperheroesToQueue(sqsConfig.getQueueUrl());
        return "All superheroes have been pushed to the queue.";
    }


    @GetMapping("/get_all_messages")
    public List<String> getAllMessages() {
        return queueService.getAllMessagesInQueue();
    }
}
