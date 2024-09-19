package com.ojt.klb.repository;

import com.ojt.klb.model.CardType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CardTypeRepository extends JpaRepository<CardType, Long> {
    List<CardType> findByIsActiveTrue();
}
