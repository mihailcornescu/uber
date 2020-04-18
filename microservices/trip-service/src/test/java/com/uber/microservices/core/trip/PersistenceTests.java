package com.uber.microservices.core.trip;

import com.uber.microservices.core.trip.persistence.TripEntity;
import com.uber.microservices.core.trip.persistence.TripRepository;
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
    private TripRepository repository;

    private TripEntity savedEntity;

    @Before
   	public void setupDb() {
        repository.deleteAll();

        TripEntity entity = new TripEntity(1, "sl1", "el1");
        savedEntity = repository.save(entity);

        areTripEqual(entity, savedEntity);
    }


    @Test
   	public void create() {
        TripEntity newEntity = new TripEntity(2, "sl2", "el2");
        repository.save(newEntity);

        TripEntity foundEntity = repository.findById(newEntity.getId()).get();
        areTripEqual(newEntity, foundEntity);

        assertEquals(2, repository.count());
    }

    @Test
   	public void update() {
        savedEntity.setStartLocation("sl2");
        repository.save(savedEntity);

        TripEntity foundEntity = repository.findById(savedEntity.getId()).get();
        assertEquals(1, (long)foundEntity.getVersion());
        assertEquals("sl2", foundEntity.getStartLocation());
    }

    @Test
   	public void delete() {
        repository.delete(savedEntity);
        assertFalse(repository.existsById(savedEntity.getId()));
    }

    @Test
   	public void getByTripId() {
        Optional<TripEntity> entity = repository.findByTripId(savedEntity.getTripId());

        assertTrue(entity.isPresent());
        areTripEqual(savedEntity, entity.get());    }

    @Test(expected = DuplicateKeyException.class)
   	public void duplicateError() {
        TripEntity entity = new TripEntity(savedEntity.getTripId(), "sl1", "sl1");
        repository.save(entity);
    }

    @Test
   	public void optimisticLockError() {

        // Store the saved entity in two separate entity objects
        TripEntity entity1 = repository.findById(savedEntity.getId()).get();
        TripEntity entity2 = repository.findById(savedEntity.getId()).get();

        // Update the entity using the first entity object
        entity1.setStartLocation("sl1");
        repository.save(entity1);

        //  Update the entity using the second entity object.
        // This should fail since the second entity now holds a old version number, i.e. a Optimistic Lock Error
        try {
            entity2.setStartLocation("sl2");
            repository.save(entity2);

            fail("Expected an OptimisticLockingFailureException");
        } catch (OptimisticLockingFailureException e) {}

        // Get the updated entity from the database and verify its new sate
        TripEntity updatedEntity = repository.findById(savedEntity.getId()).get();
        assertEquals(1, (int)updatedEntity.getVersion());
        assertEquals("sl1", updatedEntity.getStartLocation());
    }

    private boolean areTripEqual(TripEntity expectedEntity, TripEntity actualEntity) {
        return
            (expectedEntity.getId().equals(actualEntity.getId())) &&
            (expectedEntity.getVersion() == actualEntity.getVersion()) &&
            (expectedEntity.getTripId() == actualEntity.getTripId()) &&
            (expectedEntity.getStartLocation().equals(actualEntity.getStartLocation())) &&
            (expectedEntity.getEndLocation().equals(actualEntity.getEndLocation()));
    }
}
