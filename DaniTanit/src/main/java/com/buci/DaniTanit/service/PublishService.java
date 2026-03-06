package com.buci.DaniTanit.service;

import com.buci.DaniTanit.converter.PublishConverter;
import com.buci.DaniTanit.dao.PublishRepository;
import com.buci.DaniTanit.dto.PublishDto;
import com.buci.DaniTanit.entities.Publish;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PublishService {
    private final PublishRepository publishRepository;
    private final PublishConverter publishConverter;

    public List<PublishDto> findAll() {
        return publishRepository.findAll().stream()
            .map(publishConverter::toDto)
            .collect(Collectors.toList());
    }

    public PublishDto findById(Long id) {
        Publish publish = publishRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Publish not found with id: " + id));
        return publishConverter.toDto(publish);
    }

    public Long create(PublishDto publishDto) {
        Publish publish = publishConverter.toEntity(publishDto);
        return publishRepository.save(publish).getId();
    }

    public void delete(Long id) {
        if (!publishRepository.existsById(id)) {
            throw new RuntimeException("Publish not found with id: " + id);
        }
        publishRepository.deleteById(id);
    }
}
