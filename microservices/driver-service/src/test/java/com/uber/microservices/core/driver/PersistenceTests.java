package com.uber.microservices.core.driver;

import com.uber.microservices.core.driver.persistence.DriverEntity;
import com.uber.microservices.core.driver.persistence.DriverRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataMongoTest
public class PersistenceTests {

    @Autowired
    private DriverRepository repository;

    private DriverEntity savedEntity;

    @Before
   	public void setupDb() {
        repository.deleteAll();

        DriverEntity entity = new DriverEntity(1, "n", "pn1");
        savedEntity = repository.save(entity);

        areDriverEqual(entity, savedEntity);
    }


    @Test
   	public void create() {
        DriverEntity newEntity = new DriverEntity(2, "n", "pn2");
        repository.save(newEntity);

        DriverEntity foundEntity = repository.findById(newEntity.getId()).get();
        areDriverEqual(newEntity, foundEntity);

        assertEquals(2, repository.count());
    }

    @Test
   	public void update() {
        savedEntity.setName("n2");
        repository.save(savedEntity);

        DriverEntity foundEntity = repository.findById(savedEntity.getId()).get();
        assertEquals(1, (long)foundEntity.getVersion());
        assertEquals("n2", foundEntity.getName());
    }

    @Test
   	public void delete() {
        repository.delete(savedEntity);
        assertFalse(repository.existsById(savedEntity.getId()));
    }

    @Test
   	public void getByDriverId() {
        Optional<DriverEntity> entity = repository.findByDriverId(savedEntity.getDriverId());

        assertTrue(entity.isPresent());
        areDriverEqual(savedEntity, entity.get());    }

    @Test(expected = DuplicateKeyException.class)
   	public void duplicateError() {
        DriverEntity entity = new DriverEntity(savedEntity.getDriverId(), "n", "pn");
        repository.save(entity);
    }

    @Test
   	public void optimisticLockError() {

        // Store the saved entity in two separate entity objects
        DriverEntity entity1 = repository.findById(savedEntity.getId()).get();
        DriverEntity entity2 = repository.findById(savedEntity.getId()).get();

        // Update the entity using the first entity object
        entity1.setName("n1");
        repository.save(entity1);

        //  Update the entity using the second entity object.
        // This should fail since the second entity now holds a old version number, i.e. a Optimistic Lock Error
        try {
            entity2.setName("n2");
            repository.save(entity2);

            fail("Expected an OptimisticLockingFailureException");
        } catch (OptimisticLockingFailureException e) {}

        // Get the updated entity from the database and verify its new sate
        DriverEntity updatedEntity = repository.findById(savedEntity.getId()).get();
        assertEquals(1, (int)updatedEntity.getVersion());
        assertEquals("n1", updatedEntity.getName());
    }

    private boolean areDriverEqual(DriverEntity expectedEntity, DriverEntity actualEntity) {
        return
            (expectedEntity.getId().equals(actualEntity.getId())) &&
            (expectedEntity.getVersion() == actualEntity.getVersion()) &&
            (expectedEntity.getDriverId() == actualEntity.getDriverId()) &&
            (expectedEntity.getName().equals(actualEntity.getName()));
    }
}
