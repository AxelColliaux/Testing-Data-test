package fr.wcs.DataTest.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fr.wcs.DataTest.entities.Fire;

@Repository
public interface FireRepository extends JpaRepository<Fire, Long> {
    
}
