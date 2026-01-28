package com.herbei.qr_inventory.repository;

import com.herbei.qr_inventory.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long>
{
    //Optional<Item> findNewestItem;
    //Optional<Item> findOldestItem;
}