package com.herbei.qr_inventory.repository;

import com.herbei.qr_inventory.model.Location;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface LocationRepository extends CrudRepository<Location,Long> {
    Optional<Location> findByLocationName(String locationName);
}
