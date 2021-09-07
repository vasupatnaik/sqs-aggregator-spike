package me.spike.message.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.aws.messaging.listener.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

@Component
@Slf4j
public class SQSListener {

    //this apprach is not needed anymore. TODO Delete this class.

//    @SqsListener(value = "${cloud.aws.sqs.name}", deletionPolicy = SqsMessageDeletionPolicy.NEVER)
    public void handleMessage(final String payload, final Acknowledgment acknowledgment) throws ExecutionException, InterruptedException {
        log.info("Received Payload "+payload);
// do the required batch stuff.
        acknowledgment.acknowledge().get();
    }
}

