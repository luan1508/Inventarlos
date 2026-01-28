package com.herbei.qr_inventory.repository;

import com.herbei.qr_inventory.model.Item;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long>
{
    @Query("SELECT i FROM Item i ORDER BY i.ITEMID DESC LIMIT 1")
    Optional<Item> findNewestItem();
    
    @Query("SELECT i FROM Item i ORDER BY i.ITEMID ASC LIMIT 1")
    Optional<Item> findOldestItem();
    
    @Query("SELECT i FROM Item i WHERE LOWER(i.itemName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Item> searchByName(@Param("searchTerm") String searchTerm);
}