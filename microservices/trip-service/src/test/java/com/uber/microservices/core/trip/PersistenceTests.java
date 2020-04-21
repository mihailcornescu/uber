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

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
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

        TripEntity entity = new TripEntity().setDriverId(1).setTripId(2).setStartLocation("sl1")
                .setEndLocation("el1");
        savedEntity = repository.save(entity);

        assertEqualsRecommendation(entity, savedEntity);
    }

    @Test
   	public void create() {

        TripEntity newEntity = new TripEntity().setDriverId(1).setTripId(3).setStartLocation("sl2")
                .setEndLocation("el2");
        repository.save(newEntity);

        TripEntity foundEntity = repository.findById(newEntity.getId()).get();
        assertEqualsRecommendation(newEntity, foundEntity);

        assertEquals(2, repository.count());
    }

    @Test
   	public void update() {
        savedEntity.setStartLocation("a2");
        repository.save(savedEntity);

        TripEntity foundEntity = repository.findById(savedEntity.getId()).get();
        assertEquals(1, (long)foundEntity.getVersion());
        assertEquals("a2", foundEntity.getStartLocation());
    }

    @Test
   	public void delete() {
        repository.delete(savedEntity);
        assertFalse(repository.existsById(savedEntity.getId()));
    }

    @Test
   	public void getByDriverId() {
        List<TripEntity> entityList = repository.findByDriverId(savedEntity.getDriverId());

        assertThat(entityList, hasSize(1));
        assertEqualsRecommendation(savedEntity, entityList.get(0));
    }

    @Test(expected = DuplicateKeyException.class)
   	public void duplicateError() {
        TripEntity entity = new TripEntity().setDriverId(1).setTripId(2).setStartLocation("sl1")
                .setEndLocation("el1");
        repository.save(entity);
    }

    @Test
   	public void optimisticLockError() {

        // Store the saved entity in two separate entity objects
        TripEntity entity1 = repository.findById(savedEntity.getId()).get();
        TripEntity entity2 = repository.findById(savedEntity.getId()).get();

        // Update the entity using the first entity object
        entity1.setStartLocation("a1");
        repository.save(entity1);

        //  Update the entity using the second entity object.
        // This should fail since the second entity now holds a old version number, i.e. a Optimistic Lock Error
        try {
            entity2.setStartLocation("a2");
            repository.save(entity2);

            fail("Expected an OptimisticLockingFailureException");
        } catch (OptimisticLockingFailureException e) {}

        // Get the updated entity from the database and verify its new sate
        TripEntity updatedEntity = repository.findById(savedEntity.getId()).get();
        assertEquals(1, (int)updatedEntity.getVersion());
        assertEquals("a1", updatedEntity.getStartLocation());
    }

    private void assertEqualsRecommendation(TripEntity expectedEntity, TripEntity actualEntity) {
        assertEquals(expectedEntity.getId(),               actualEntity.getId());
        assertEquals(expectedEntity.getVersion(),          actualEntity.getVersion());
        assertEquals(expectedEntity.getDriverId(),        actualEntity.getDriverId());
        assertEquals(expectedEntity.getTripId(), actualEntity.getTripId());
        assertEquals(expectedEntity.getStartLocation(),           actualEntity.getStartLocation());
        assertEquals(expectedEntity.getEndLocation(),           actualEntity.getEndLocation());
        assertEquals(expectedEntity.getRating(),           actualEntity.getRating());
        assertEquals(expectedEntity.getStartTime(),          actualEntity.getStartTime());
        assertEquals(expectedEntity.getEndTime(),           actualEntity.getEndTime());
    }
}
