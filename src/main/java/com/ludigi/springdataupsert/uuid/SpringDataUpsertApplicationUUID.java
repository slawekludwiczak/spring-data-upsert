package com.ludigi.springdataupsert.uuid;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.UUID;

@SpringBootApplication
public class SpringDataUpsertApplicationUUID implements CommandLineRunner {
    private final ExampleUUIDRepository exampleUUIDRepository;

    public SpringDataUpsertApplicationUUID(ExampleUUIDRepository exampleUUIDRepository) {
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
