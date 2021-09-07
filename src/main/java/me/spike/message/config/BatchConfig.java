package me.spike.message.config;

import com.amazonaws.internal.SdkInternalList;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Configuration
@Slf4j
public class BatchConfig {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;
    @Autowired
    public StepBuilderFactory stepBuilderFactory;
    @Autowired
    @Qualifier("amazonSqsPrimary")
    AmazonSQS amazonSQS;
    @Value("${cloud.aws.sqs.url}")
    private String sqsUrl;

    @Bean
    public Job job(){
        return this.jobBuilderFactory.get("read-from-sqs-job")
                .start(chunkBasedStep())
                .build();
    }

    @Bean
    public Step chunkBasedStep() {

        return (Step) this.stepBuilderFactory.get("chunk-sqs-message-step-"+ UUID.randomUUID().toString())
                .chunk(10)
                .reader(itemReader())
                .writer(new ItemWriter<Object>() {
                    @Override
                    public void write(List<? extends Object> items) throws Exception {
                        List<Message> messages = (List<Message>) items.stream()
                                .flatMap(l -> ((SdkInternalList)l).stream())
                                .filter( f -> f != null)
                                .collect(Collectors.toList());
                        if(messages.size()>0)
                            log.info("Final Messages Size to be processed : {}",messages.size());
                    }
                }).build();
    }

    @Bean
    public ItemReader<List<Message>> itemReader() {
        return new SQSItemReader(amazonSQS,sqsUrl);
    }
}
