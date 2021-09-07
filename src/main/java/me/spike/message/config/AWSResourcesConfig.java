package me.spike.message.config;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.STSAssumeRoleSessionCredentialsProvider;
import com.amazonaws.services.securitytoken.AWSSecurityTokenService;
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClientBuilder;
import com.amazonaws.services.securitytoken.model.AssumeRoleRequest;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.aws.messaging.config.SimpleMessageListenerContainerFactory;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class AWSResourcesConfig {

    @Value("${cloud.aws.region.static}")
    private String sqsRegion;
    @Value("${cloud.aws.sqs.maxMsgToRead}")
    private Integer maxMsg;

    @Bean
    @ConfigurationProperties(prefix = "cloud.aws.sqs.assume-role")
    public AssumeRoleRequest assumeRoleRequestSqs() {
        return new AssumeRoleRequest();
    }

    @Bean("amazonSqsPrimary")
    public AmazonSQS amazonSQSClient() {

        AssumeRoleRequest assumeRoleRequest = assumeRoleRequestSqs();
        AWSSecurityTokenService stsClient = AWSSecurityTokenServiceClientBuilder.standard().withRegion(sqsRegion).build();
        STSAssumeRoleSessionCredentialsProvider.Builder stsBuilder = new STSAssumeRoleSessionCredentialsProvider
                .Builder(assumeRoleRequest.getRoleArn(), assumeRoleRequest.getRoleSessionName());
        AWSCredentialsProvider stsProvider = stsBuilder
                .withRoleSessionDurationSeconds(assumeRoleRequest.getDurationSeconds())
                .withStsClient(stsClient)
                .build();
        stsProvider.refresh();

        AmazonSQS client = AmazonSQSClientBuilder
                .standard()
                .withCredentials(stsProvider)
                .withRegion(sqsRegion)
                .build();

        return client;
    }

    @Bean
    @Primary
    public AmazonSQSAsync amazonSQSAsyncClient() {

        AssumeRoleRequest assumeRoleRequest = assumeRoleRequestSqs();
        AWSSecurityTokenService stsClient = AWSSecurityTokenServiceClientBuilder.standard().withRegion(sqsRegion).build();
        STSAssumeRoleSessionCredentialsProvider.Builder stsBuilder = new STSAssumeRoleSessionCredentialsProvider
                .Builder(assumeRoleRequest.getRoleArn(), assumeRoleRequest.getRoleSessionName());
        AWSCredentialsProvider stsProvider = stsBuilder
                .withRoleSessionDurationSeconds(assumeRoleRequest.getDurationSeconds())
                .withStsClient(stsClient)
                .build();
        stsProvider.refresh();

        AmazonSQSAsync client = AmazonSQSAsyncClientBuilder
                .standard()
                .withCredentials(stsProvider)
                .withRegion(sqsRegion)
                .build();

        return client;
    }

    @Bean
    public SimpleMessageListenerContainerFactory simpleMessageListenerContainerFactory(AmazonSQSAsync amazonSQSAsync) {
        SimpleMessageListenerContainerFactory factory = new SimpleMessageListenerContainerFactory();
        factory.setWaitTimeOut(20); //Long polling, the maximum value of 20 seconds
        factory.setAmazonSqs(amazonSQSAsync);
        factory.setMaxNumberOfMessages(maxMsg);
        return factory;
    }

    @Bean
    public QueueMessagingTemplate queueMessagingTemplate(AmazonSQSAsync amazonSQSAsync, @Value("${cloud.aws.sqs.name}") String sqsName) {
        final QueueMessagingTemplate queueMessagingTemplate = new QueueMessagingTemplate(amazonSQSAsync);
        queueMessagingTemplate.setDefaultDestinationName(sqsName);
        return queueMessagingTemplate;
    }

}
