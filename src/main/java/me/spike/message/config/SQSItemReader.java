package me.spike.message.config;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

import java.util.List;

/** This ItemReader impl class is used for reading messages from SQS.
 *
 */
public class SQSItemReader implements ItemReader<List<Message>> {

    String sqsUrl;
    AmazonSQS amazonSQS;

    public SQSItemReader(AmazonSQS amazonSQS, String sqsUrl) {
        this.amazonSQS = amazonSQS;
        this.sqsUrl = sqsUrl;
    }

    @Override
    public List<Message> read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {

        ReceiveMessageRequest request = new ReceiveMessageRequest()
                .withMaxNumberOfMessages(10)
                .withQueueUrl(sqsUrl);

        ReceiveMessageResult result= amazonSQS.receiveMessage(request);
        return result.getMessages(); // The trick here is, since this invocation will not return null, this job runs perpetually.
    }
}
