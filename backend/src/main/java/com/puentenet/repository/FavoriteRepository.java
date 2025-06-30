package com.puentenet.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.puentenet.domain.Favorite;
import com.puentenet.domain.Instrument;
import com.puentenet.domain.User;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findByUser(User user);
    
    @Query("SELECT f.instrument FROM Favorite f WHERE f.user = :user")
    List<Instrument> findInstrumentsByUser(@Param("user") User user);
    
    Optional<Favorite> findByUserAndInstrument(User user, Instrument instrument);
    
    boolean existsByUserAndInstrument(User user, Instrument instrument);
    
    void deleteByUserAndInstrument(User user, Instrument instrument);
} 