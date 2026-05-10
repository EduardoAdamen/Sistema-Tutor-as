package com.itch.tutorias.repository;

import com.itch.tutorias.model.ActividadPatGeneral;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActividadPatGeneralRepository extends JpaRepository<ActividadPatGeneral, Integer> {
}
