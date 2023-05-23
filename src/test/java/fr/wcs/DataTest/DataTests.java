package fr.wcs.DataTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.Optional;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import fr.wcs.DataTest.entities.Fire;
import fr.wcs.DataTest.entities.Fireman;
import fr.wcs.DataTest.repositories.FireRepository;
import fr.wcs.DataTest.repositories.FiremanRepository;
import jakarta.validation.ConstraintViolationException;

@DataJpaTest
class DataTests {

  @Autowired
  FireRepository fireRepository;

  @Autowired
  FiremanRepository firemanRepository;

    @Test
    public void testCreateFire() {
        int severity = 8;
        Instant date = Instant.now();
        var fire = new Fire(severity, date);

        // flush envoie les données instantanément à la base
        fireRepository.saveAndFlush(fire);

        Optional<Fire> fromDB = fireRepository.findById(fire.getId());

        assertTrue(fromDB.isPresent());
        assertEquals(fire.getId(), fromDB.get().getId());
        assertEquals(date, fromDB.get().getDate());
        assertEquals(severity, fromDB.get().getSeverity());
    }

    @Test
    public void testCreateFireman() {

        Instant date = Instant.now();

        var fire1 = new Fire(5, date);
        var fire2 = new Fire(9, date);
        var fire3 = new Fire(1, date);

        List<Fire> fires = new ArrayList<>();
        fires.add(fire1);
        fires.add(fire2);
        fires.add(fire3);

        var fireman1 = new Fireman("Axel", fires);

        fireRepository.saveAndFlush(fire1);
        fireRepository.saveAndFlush(fire2);
        fireRepository.saveAndFlush(fire3);
        firemanRepository.saveAndFlush(fireman1);

        Optional<Fireman> fromDB = firemanRepository.findById(fireman1.getId());

        assertTrue(fromDB.isPresent());
        assertEquals(fires, fromDB.get().getFires());
        assertEquals(fireman1.getName(), fromDB.get().getName());
    }

    @Test
    public void testCreateFireWithNegativeSeverity() {

        Instant date = Instant.now();
        var fire1 = new Fire(-6, date);

        
        assertThrows(ConstraintViolationException.class, () -> {
            fireRepository.saveAndFlush(fire1);
        });
    }

    @Test
    public void testGetVeteranWithMultipleFiremen() {
        
        var fire1 = new Fire(5, Instant.now());
        var fire2 = new Fire(9, Instant.now());
        var fire3 = new Fire(1, Instant.now());

        fireRepository.save(fire1);
        fireRepository.save(fire2);
        fireRepository.save(fire3);

        List<Fire> fires1 = new ArrayList<>();
        fires1.add(fire1);
        fires1.add(fire2);

        List<Fire> fires2 = new ArrayList<>();
        fires2.add(fire3);

        var fireman1 = new Fireman("Pompier 1", fires1);
        var fireman2 = new Fireman("Pompier 2", fires2);

        firemanRepository.save(fireman1);
        firemanRepository.save(fireman2);

        Optional<Fireman> veteran = firemanRepository.getVeteran();

        Assertions.assertTrue(veteran.isPresent());
        Assertions.assertEquals(fireman1.getName(), veteran.get().getName());
    }

    @Test
    public void testGetVeteranWithSingleFireman() {
        // Création d'un feu et d'un pompier
        var fire = new Fire(5, Instant.now());

        fireRepository.save(fire);

        List<Fire> fires = new ArrayList<>();
        fires.add(fire);
        var fireman1 = new Fireman("Pompier 1", fires);

        firemanRepository.save(fireman1);

        Optional<Fireman> veteran = firemanRepository.getVeteran();

        Assertions.assertTrue(veteran.isPresent());
        Assertions.assertEquals(fireman1.getName(), veteran.get().getName());
    }

    @Test
    public void testGetVeteranWithNoFireman() {
        Optional<Fireman> veteran = firemanRepository.getVeteran();

        Assertions.assertTrue(veteran.isEmpty());
    }
}