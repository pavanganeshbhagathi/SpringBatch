package com.example.batch.batch.config;

import com.example.batch.batch.Entity.Profile;
import com.example.batch.batch.Entity.User;
import com.example.batch.batch.listener.JobCompletionNotificationListener;
import com.example.batch.batch.process.ProfileItemProcessor;
import com.example.batch.batch.process.filter.EmployeeNameFilterItemProcessor;
import com.example.batch.batch.process.validating.UserValidator;
import com.example.batch.batch.repository.ProfileRepository;
import com.example.batch.batch.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.batch.item.validator.ValidatingItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Sort;

import java.util.*;

@Configuration
@EnableBatchProcessing
@AllArgsConstructor
public class BatchConfiguration {


    public JobBuilderFactory jobBuilderFactory;

    public StepBuilderFactory stepBuilderFactory;


    @Lazy
    private UserRepository userRepository;


    @Lazy
    private ProfileRepository profileRepository;


    @Bean
    public RepositoryItemReader<User> reader() {
        RepositoryItemReader<User> reader = new RepositoryItemReader<>();
        reader.setRepository(userRepository);
        reader.setMethodName("findByStatusAndEmailVerified");

        List<Object> queryMethodArguments = new ArrayList<>();
        // for status
        queryMethodArguments.add("pending");
        // for emailVerified
        queryMethodArguments.add(Boolean.TRUE);

        reader.setArguments(queryMethodArguments);
        reader.setPageSize(100);
        Map<String, Sort.Direction> sorts = new HashMap<>();
        sorts.put("id", Sort.Direction.ASC);
        reader.setSort(sorts);

        return reader;
    }

    @Bean
    public RepositoryItemWriter<Profile> writer() {
        RepositoryItemWriter<Profile> writer = new RepositoryItemWriter<>();
        writer.setRepository(profileRepository);
        writer.setMethodName("save");
        return writer;
    }

    @Bean
    public ProfileItemProcessor processor() {
        return new ProfileItemProcessor();
    }

    @Bean
    public EmployeeNameFilterItemProcessor filterprocessor() {
        return new EmployeeNameFilterItemProcessor();
    }
    @Bean
    public UserValidator validateprocessor() {
        return new UserValidator();
    }

    @Bean
    public ValidatingItemProcessor<User> validatingItemProcessor() {
        ValidatingItemProcessor<User> itemProcessor = new ValidatingItemProcessor<>(new UserValidator());
        itemProcessor.setFilter(true);
        return itemProcessor;
    }

    @Bean
    public CompositeItemProcessor<User, ?> compositeItemProcessor() throws Exception {
        CompositeItemProcessor<User, ?> processor = new CompositeItemProcessor<>();
        List<ItemProcessor<User, ?>> itemProcessors = Arrays.asList(validatingItemProcessor(), filterprocessor(), processor());
        processor.setDelegates(itemProcessors);
        processor.afterPropertiesSet();

        return processor;
    }


    @Bean
    public Step step1(ItemReader<User> itemReader, ItemWriter<Profile> itemWriter)
            throws Exception {

        return this.stepBuilderFactory.get("step1").<User, Profile>chunk(5).reader(itemReader)
                .processor((ItemProcessor<? super User, ? extends Profile>) compositeItemProcessor())
                .writer(itemWriter).build();
    }

    @Bean
    public Job profileUpdateJob(JobCompletionNotificationListener listener, Step step1)
            throws Exception {

        return this.jobBuilderFactory.get("profileUpdateJob").incrementer(new RunIdIncrementer())
                .listener(listener).start(step1).build();
    }
}
