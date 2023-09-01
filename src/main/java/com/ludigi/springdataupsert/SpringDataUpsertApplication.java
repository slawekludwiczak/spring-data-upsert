package com.ludigi.springdataupsert;

import com.ludigi.springdataupsert.uuid.ExampleUUIDRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringDataUpsertApplication implements CommandLineRunner {
    private final ExampleEntityRepository exampleEntityRepository;
    private final ExampleUUIDRepository exampleUUIDRepository;

    public SpringDataUpsertApplication(ExampleEntityRepository exampleEntityRepository,
                                       ExampleUUIDRepository exampleUUIDRepository) {
        this.exampleEntityRepository = exampleEntityRepository;
        this.exampleUUIDRepository = exampleUUIDRepository;
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringDataUpsertApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        ExampleEntity entity0 = new ExampleEntity(0, "Entity0");
        ExampleEntity entity1 = new ExampleEntity(1, "Entity1");
        ExampleEntity entity2 = new ExampleEntity(2, "Entity2");
        exampleEntityRepository.save(entity0); //persist
        exampleEntityRepository.save(entity1); //merge
        exampleEntityRepository.save(entity2); //merge
        ExampleEntity entity0ToUpdate = exampleEntityRepository.findById(0L).orElseThrow();
        entity0ToUpdate.setName("UpdatedEntity0");
        //should merge, but trying to persist, thus throws JdbcSQLIntegrityConstraintViolationException
        exampleEntityRepository.save(entity0ToUpdate);
    }
}
