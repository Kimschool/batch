package com.example.SpringBatchTutorial.job.FileDataReadWrite;

import com.example.SpringBatchTutorial.core.domain.mail.MailHistory;
import com.example.SpringBatchTutorial.core.domain.mail.MailHistoryRepository;
import com.example.SpringBatchTutorial.core.domain.user.User1;
import com.example.SpringBatchTutorial.core.domain.user.User1Repository;
import com.example.SpringBatchTutorial.job.FileDataReadWrite.dto.Player;
import com.example.SpringBatchTutorial.job.FileDataReadWrite.dto.PlayerYears;
import lombok.RequiredArgsConstructor;
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

import java.util.Collections;
import java.util.List;


/**
 * desc: csv 파일 읽고 쓰기
 * run: --job.name=fileReadWriteJob
 */
@Configuration
@RequiredArgsConstructor
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
    public Step mailSenderStep(ItemReader mailSenderItemReader,
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
        return new ItemProcessor<User1, MailHistory>() {
            @Override
            public MailHistory process(User1 item) throws Exception {

                // mail 보내기 로직
                int count = 5;

                // 메일 전송 성공시
                MailHistory result =
                        MailHistory.builder().
                                result("0")
                                .count(count)
                                .build();

                return result;
            }
        };
    }

    @StepScope
    @Bean
    public RepositoryItemReader<User1> mailSenderItemReader() {
        return new RepositoryItemReaderBuilder<User1>()
                .name("userItemReader")
                .repository(user1Repository)
                .methodName("findAll")
                .sorts(Collections.singletonMap("email", Sort.Direction.ASC))
                .build();
    }

    @StepScope
    @Bean
    public ItemWriter<MailHistory> mailSenderWriter() {
        return new ItemWriter<MailHistory>() {
            @Override
            public void write(List<? extends MailHistory> items) throws Exception {
                items.forEach(item -> mailHistoryRepository.save(item));
            }
        };
    }
}
