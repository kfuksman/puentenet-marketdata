package com.puentenet.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.puentenet.domain.Instrument;

@Repository
public interface InstrumentRepository extends JpaRepository<Instrument, Long> {
    Optional<Instrument> findBySymbol(String symbol);
    List<Instrument> findByActiveTrue();
    
    @Query("SELECT i FROM Instrument i WHERE i.active = true AND (LOWER(i.symbol) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(i.name) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<Instrument> searchInstruments(@Param("search") String search);
} 