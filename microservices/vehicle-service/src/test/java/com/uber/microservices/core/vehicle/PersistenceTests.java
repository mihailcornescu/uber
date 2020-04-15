package com.uber.microservices.core.vehicle;

import com.uber.microservices.core.vehicle.persistence.VehicleEntity;
import com.uber.microservices.core.vehicle.persistence.VehicleRepository;
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
    private VehicleRepository repository;

    private VehicleEntity savedEntity;

    @Before
   	public void setupDb() {
        repository.deleteAll();

        VehicleEntity entity = new VehicleEntity(1, "n", "c", "r");
        savedEntity = repository.save(entity);

        areDriverEqual(entity, savedEntity);
    }


    @Test
   	public void create() {
        VehicleEntity newEntity = new VehicleEntity(2, "n", "c", "r");
        repository.save(newEntity);

        VehicleEntity foundEntity = repository.findById(newEntity.getId()).get();
        areDriverEqual(newEntity, foundEntity);

        assertEquals(2, repository.count());
    }

    @Test
   	public void update() {
        savedEntity.setName("n2");
        repository.save(savedEntity);

        VehicleEntity foundEntity = repository.findById(savedEntity.getId()).get();
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
        Optional<VehicleEntity> entity = repository.findByVehicleId(savedEntity.getVehicleId());

        assertTrue(entity.isPresent());
        areDriverEqual(savedEntity, entity.get());
    }

    @Test(expected = DuplicateKeyException.class)
   	public void duplicateError() {
        VehicleEntity entity = new VehicleEntity(savedEntity.getVehicleId(), "n", "c", "r");
        repository.save(entity);
    }

    @Test
   	public void optimisticLockError() {

        // Store the saved entity in two separate entity objects
        VehicleEntity entity1 = repository.findById(savedEntity.getId()).get();
        VehicleEntity entity2 = repository.findById(savedEntity.getId()).get();

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
        VehicleEntity updatedEntity = repository.findById(savedEntity.getId()).get();
        assertEquals(1, (int)updatedEntity.getVersion());
        assertEquals("n1", updatedEntity.getName());
    }

    private boolean areDriverEqual(VehicleEntity expectedEntity, VehicleEntity actualEntity) {
        return
            (expectedEntity.getId().equals(actualEntity.getId())) &&
            (expectedEntity.getVersion() == actualEntity.getVersion()) &&
            (expectedEntity.getVehicleId() == actualEntity.getVehicleId()) &&
            (expectedEntity.getName().equals(actualEntity.getName()));
    }
}
