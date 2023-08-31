[SimpleJpaRepository.save()](https://github.com/spring-projects/spring-data-jpa/blob/main/spring-data-jpa/src/main/java/org/springframework/data/jpa/repository/support/SimpleJpaRepository.java#L612) method has wrong upsert logic
and is valid only for generated ids.

save method looks like this:

```java
@Transactional
public <S extends T> S save(S entity) {
    Assert.notNull(entity, "Entity must not be null");
    if (this.entityInformation.isNew(entity)) {
        this.em.persist(entity);
        return entity;
    } else {
        return this.em.merge(entity);
    }
}
```

It checks whenever entity is `new` and if so, it invokes persist method, and if entity is not `new`, it invokes merge().
Checking if entity is `new` is done in `JpaMetamodelEntityInformation` and then delegated to [AbstractEntityInformation.isNew()](https://github.com/spring-projects/spring-data-commons/blob/main/src/main/java/org/springframework/data/repository/core/support/AbstractEntityInformation.java#L42):

```java
public boolean isNew(T entity) {
    ID id = this.getId(entity);
    Class<ID> idType = this.getIdType();
    if (!idType.isPrimitive()) {
        return id == null;
    } else if (id instanceof Number) {
        return ((Number)id).longValue() == 0L;
    } else {
        throw new IllegalArgumentException(String.format("Unsupported primitive id type %s", idType));
    }
}
```

The problem is that this logic makes sense only if the id is generated.
For primitive types there is also wrong assumption that entity with `id = 0` is new.
If we have entity like:

```java
@Entity
public class ExampleEntity {
    @Id
    private long id;
    private String name;
    ...
}
```

and repository:

```java
public interface ExampleEntityRepository extends CrudRepository<ExampleEntity, Long> {
}
```

When we save few instances of `ExampleEntity` we can see the inconsistency:

```java
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
```

There is unnecessary overhead of checking if entity is "new" or not. This verification is just fictitious.
Since we cannot do any assumptions if the entity is new or not without checking persistence context,
the benefit of this check is only for specific case with auto generated ids.

## Possible solutions

### 1 - just merge

Instead of trying to check whenever entity is new or not, just use merge method and it will take care of that:

```java
@Transactional
public <S extends T> S save(S entity) {
    Assert.notNull(entity, "Entity must not be null");
    return this.em.merge(entity);
}
```

### 2 - verify novelty of entity only for entities with generated ids
...

### Incostistency with Jakarta Persistence spec

There is also a problem with documentation of `EntityInformation.isNew()`. It states that
```
Returns whether the given entity is considered to be new.
```

but according to [jakarta persistence specification](https://jakarta.ee/specifications/persistence/3.0/jakarta-persistence-spec-3.0.html#a1929):

```
A new entity instance has no persistent identity, 
and is not yet associated with a persistence context.
```

So simple id verification is not sufficient.
