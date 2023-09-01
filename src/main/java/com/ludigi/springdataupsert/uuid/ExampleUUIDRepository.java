package com.ludigi.springdataupsert.uuid;

import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface ExampleUUIDRepository extends CrudRepository<ExampleUUID, UUID> {
}
