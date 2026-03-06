package com.buci.DaniTanit.presentation;

import com.buci.DaniTanit.dto.PublishDto;
import com.buci.DaniTanit.service.PublishService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/rest/publish", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class PublishController {
    private final PublishService publishService;

    @GetMapping("/all")
    public ResponseEntity<List<PublishDto>> findAll() {
        return ResponseEntity.ok(publishService.findAll());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<PublishDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(publishService.findById(id));
    }

    @PostMapping("/create")
    public ResponseEntity<Long> create(@RequestBody PublishDto publishDto) {
        return new ResponseEntity<>(publishService.create(publishDto), HttpStatus.CREATED);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        publishService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
