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

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
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

        VehicleEntity entity = new VehicleEntity(1, 2, LocalDate.of(2019, 01, 01), LocalDate.now());
        savedEntity = repository.save(entity);

        assertEqualsRecommendation(entity, savedEntity);
    }


    @Test
   	public void create() {

        VehicleEntity newEntity = new VehicleEntity(1, 3, LocalDate.of(2019, 06, 01), LocalDate.now());
        repository.save(newEntity);

        VehicleEntity foundEntity = repository.findById(newEntity.getId()).get();
        assertEqualsRecommendation(newEntity, foundEntity);

        assertEquals(2, repository.count());
    }

    @Test
   	public void update() {
        savedEntity.setDateFrom(LocalDate.of(2020,01,01));
        repository.save(savedEntity);

        VehicleEntity foundEntity = repository.findById(savedEntity.getId()).get();
        assertEquals(1, (long)foundEntity.getVersion());
        assertEquals(LocalDate.of(2020,01,01), foundEntity.getDateFrom());
    }

    @Test
   	public void delete() {
        repository.delete(savedEntity);
        assertFalse(repository.existsById(savedEntity.getId()));
    }

    @Test
   	public void getByProductId() {
        List<VehicleEntity> entityList = repository.findByDriverId(savedEntity.getDriverId());

        assertThat(entityList, hasSize(1));
        assertEqualsRecommendation(savedEntity, entityList.get(0));
    }

    @Test(expected = DuplicateKeyException.class)
   	public void duplicateError() {
        VehicleEntity entity = new VehicleEntity(1, 2, LocalDate.of(2019, 01, 01), LocalDate.now());

        repository.save(entity);
    }

    @Test
   	public void optimisticLockError() {

        // Store the saved entity in two separate entity objects
        VehicleEntity entity1 = repository.findById(savedEntity.getId()).get();
        VehicleEntity entity2 = repository.findById(savedEntity.getId()).get();

        // Update the entity using the first entity object
        entity1.setDateFrom(LocalDate.of(2020,01,01));
        repository.save(entity1);

        //  Update the entity using the second entity object.
        // This should fail since the second entity now holds a old version number, i.e. a Optimistic Lock Error
        try {
            entity2.setDateFrom(LocalDate.of(2019, 07, 01));
            repository.save(entity2);

            fail("Expected an OptimisticLockingFailureException");
        } catch (OptimisticLockingFailureException e) {}

        // Get the updated entity from the database and verify its new sate
        VehicleEntity updatedEntity = repository.findById(savedEntity.getId()).get();
        assertEquals(1, (int)updatedEntity.getVersion());
        assertEquals(LocalDate.of(2020, 01, 01), updatedEntity.getDateFrom());
    }

    private void assertEqualsRecommendation(VehicleEntity expectedEntity, VehicleEntity actualEntity) {
        assertEquals(expectedEntity.getId(),               actualEntity.getId());
        assertEquals(expectedEntity.getVersion(),          actualEntity.getVersion());
        assertEquals(expectedEntity.getDriverId(),        actualEntity.getDriverId());
        assertEquals(expectedEntity.getVehicleId(), actualEntity.getVehicleId());
        assertEquals(expectedEntity.getDateFrom(),           actualEntity.getDateFrom());
        assertEquals(expectedEntity.getDateTo(),          actualEntity.getDateTo());
    }
}
