package com.buci.DaniTanit.dao;

import com.buci.DaniTanit.entities.Publish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PublishRepository extends JpaRepository<Publish, Long> {
}
