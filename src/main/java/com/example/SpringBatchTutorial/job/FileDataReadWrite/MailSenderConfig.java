package com.example.SpringBatchTutorial.job.FileDataReadWrite;

import com.example.SpringBatchTutorial.core.domain.mail.MailHistory;
import com.example.SpringBatchTutorial.core.domain.mail.MailHistoryRepository;
import com.example.SpringBatchTutorial.core.domain.user.User1;
import com.example.SpringBatchTutorial.core.domain.user.User1Repository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;


/**
 * desc: csv 파일 읽고 쓰기
 * run: --job.name=fileReadWriteJob
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class MailSenderConfig {

    @Autowired
    private User1Repository user1Repository;

    @Autowired
    private MailHistoryRepository mailHistoryRepository;

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job mailSenderJob(Step mailSenderStep) {
        return jobBuilderFactory.get("mailSenderJob")
                .incrementer(new RunIdIncrementer())
                .start(mailSenderStep)
                .build();
    }

    @JobScope
    @Bean
    public Step mailSenderStep(
            ItemReader mailSenderItemReader,
            ItemProcessor mailSenderItemProcessor,
            ItemWriter mailSenderWriter) {

        return stepBuilderFactory.get("mailSenderStep")
                .<User1, MailHistory>chunk(5)
                .reader(mailSenderItemReader)
                .processor(mailSenderItemProcessor)
                .writer(mailSenderWriter)
                .build();
    }

    @StepScope
    @Bean
    public ItemProcessor<User1, MailHistory> mailSenderItemProcessor() {
        log.info("process");

        return new ItemProcessor<User1, MailHistory>() {
            @Override
            public MailHistory process(User1 item) throws Exception {

                log.info("process");
                // mail 보내기 로직
                int count = 5;

                // 메일 전송 성공시
                MailHistory result =
                        MailHistory.builder()
                                .date(null)
                                .result("0")
                                .count(count)
                                .build();

                return result;
            }
        };
    }

    @StepScope
    @Bean
    public RepositoryItemReader<User1> mailSenderItemReader() {
        log.info("reader");

        RepositoryItemReader<User1> result =  new RepositoryItemReaderBuilder<User1>()
                .name("mailSenderItemReader")
                .repository(user1Repository)
                .methodName("findAll")
                .pageSize(5)
                .arguments(Arrays.asList())
                .sorts(Collections.singletonMap("email", Sort.Direction.ASC))
                .build();

        return result;
    }

    @StepScope
    @Bean
    public ItemWriter<MailHistory> mailSenderWriter() {
        return new ItemWriter<MailHistory>() {
            @Override
            public void write(List<? extends MailHistory> items) throws Exception {
                log.info("writer");

                items.forEach(item -> {
                    log.info(item.toString());
                    mailHistoryRepository.save(item);
                    }
                );
            }
        };
    }
}
