package com.cleartax.training_superheroes.services;

import com.cleartax.training_superheroes.config.SqsConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse;

import java.util.ArrayList;
import java.util.List;

@Service
public class QueueService {

    @Autowired
    private SqsConfig sqsConfig;

    @Autowired
    private SqsClient sqsClient;

    public List<String> getAllMessagesInQueue() {
        List<String> messageBodies = new ArrayList<>();
        ReceiveMessageResponse receiveMessageResponse = sqsClient.receiveMessage(ReceiveMessageRequest.builder()
            .queueUrl(sqsConfig.getQueueUrl())
            .maxNumberOfMessages(10) // Adjust the number of messages to retrieve
            .waitTimeSeconds(10) // Enable long polling
            .build());

        List<Message> messages = receiveMessageResponse.messages();
        for (Message message : messages) {
            messageBodies.add("Message ID: " + message.messageId() + 
                              ", Message Body: " + message.body());
        }
        return messageBodies;
    }
} 