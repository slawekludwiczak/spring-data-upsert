package com.ludigi.springdataupsert.uuid;

import com.ludigi.springdataupsert.ExampleEntityRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.UUID;

//@SpringBootApplication
public class SpringDataUpsertApplicationUUID implements CommandLineRunner {
    private final ExampleEntityRepository exampleEntityRepository;
    private final ExampleUUIDRepository exampleUUIDRepository;

    public SpringDataUpsertApplicationUUID(ExampleEntityRepository exampleEntityRepository,
                                           ExampleUUIDRepository exampleUUIDRepository) {
        this.exampleEntityRepository = exampleEntityRepository;
        this.exampleUUIDRepository = exampleUUIDRepository;
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringDataUpsertApplicationUUID.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        ExampleUUID entity1 = new ExampleUUID(UUID.randomUUID(), "Entity1");
        exampleUUIDRepository.save(entity1); //merge instead of persist
    }
}
