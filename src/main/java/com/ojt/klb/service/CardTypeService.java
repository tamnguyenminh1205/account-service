package com.ojt.klb.service;

import com.ojt.klb.dto.CardTypeDto;

import java.util.List;
import java.util.Optional;

public interface CardTypeService {
    Optional<CardTypeDto> createCardType(CardTypeDto cardTypeDto);
    List<CardTypeDto> getAllCardTypesIsActive();
    void updateCardType(Long id, CardTypeDto cardTypeDto);
    List<CardTypeDto> getAllCardTypes();
}
