package me.spike.message.controller;

import me.spike.message.dto.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class MessageLoadRestController {

    @Autowired
    QueueMessagingTemplate template;

    /**
     * Simple util api for pushing message to SQS for checking the batch processing module.
     *
     * @return N.A
     */
    @PostMapping("v1/message")
    public ResponseEntity populateSQSWithMessages(){
        for (int i=0; i<500; i++)
            new Thread(() -> template.convertAndSend(Message.builder().uuid(UUID.randomUUID().toString()).build())).run(); //Lazy enough to use sending batch messages :D,this is good enough for this spike.
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
