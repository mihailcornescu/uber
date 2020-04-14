package com.uber.microservices.core.history;

import com.uber.microservices.core.history.persistence.HistoryEntity;
import com.uber.microservices.core.history.persistence.HistoryRepository;
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
    private HistoryRepository repository;

    private HistoryEntity savedEntity;

    @Before
   	public void setupDb() {
   		repository.deleteAll().block();

        HistoryEntity entity = new HistoryEntity(1, 2, LocalDate.now(), LocalDate.now());
        savedEntity = repository.save(entity).block();

        assertEqualsHistory(entity, savedEntity);
    }


    @Test
   	public void create() {

        HistoryEntity newEntity = new HistoryEntity(1, 3, LocalDate.now(), LocalDate.now());
        repository.save(newEntity).block();

        HistoryEntity foundEntity = repository.findById(newEntity.getId()).block();
        assertEqualsHistory(newEntity, foundEntity);

        assertEquals(2, (long)repository.count().block());
    }

    @Test
   	public void update() {
        savedEntity.setDateTo(LocalDate.now());
        repository.save(savedEntity).block();

        HistoryEntity foundEntity = repository.findById(savedEntity.getId()).block();
        assertEquals(1, (long)foundEntity.getVersion());
        assertEquals(LocalDate.now(), foundEntity.getDateTo());
    }

    @Test
   	public void delete() {
        repository.delete(savedEntity).block();
        assertFalse(repository.existsById(savedEntity.getId()).block());
    }

    @Test
   	public void getByDriverId() {
        List<HistoryEntity> entityList = repository.findByDriverId(savedEntity.getDriverId()).collectList().block();

        assertThat(entityList, hasSize(1));
        assertEqualsHistory(savedEntity, entityList.get(0));
    }

    @Test(expected = DuplicateKeyException.class)
   	public void duplicateError() {
        HistoryEntity entity = new HistoryEntity(1, 2, LocalDate.now(), LocalDate.now());
        repository.save(entity).block();
    }

    @Test
   	public void optimisticLockError() {

        // Store the saved entity in two separate entity objects
        HistoryEntity entity1 = repository.findById(savedEntity.getId()).block();
        HistoryEntity entity2 = repository.findById(savedEntity.getId()).block();

        // Update the entity using the first entity object
        entity1.setDateTo(LocalDate.now());
        repository.save(entity1).block();

        //  Update the entity using the second entity object.
        // This should fail since the second entity now holds a old version number, i.e. a Optimistic Lock Error
        try {
            entity2.setDateTo(LocalDate.now());
            repository.save(entity2).block();

            fail("Expected an OptimisticLockingFailureException");
        } catch (OptimisticLockingFailureException e) {}

        // Get the updated entity from the database and verify its new sate
        HistoryEntity updatedEntity = repository.findById(savedEntity.getId()).block();
        assertEquals(1, (int)updatedEntity.getVersion());
        assertEquals(LocalDate.now(), updatedEntity.getDateTo());
    }

    private void assertEqualsHistory(HistoryEntity expectedEntity, HistoryEntity actualEntity) {
        assertEquals(expectedEntity.getId(),               actualEntity.getId());
        assertEquals(expectedEntity.getVersion(),          actualEntity.getVersion());
        assertEquals(expectedEntity.getDriverId(),        actualEntity.getDriverId());
        assertEquals(expectedEntity.getVehicleId(), actualEntity.getVehicleId());
        assertEquals(expectedEntity.getDateFrom(),           actualEntity.getDateFrom());
    }
}
