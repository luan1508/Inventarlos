package com.herbei.qr_inventory.controller;

import com.herbei.qr_inventory.model.Category;
import com.herbei.qr_inventory.model.Item;
import com.herbei.qr_inventory.model.ItemRequest;
import com.herbei.qr_inventory.model.Location;
import com.herbei.qr_inventory.service.ItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/items")
//@CrossOrigin(origins = "*")
public class ItemController
{
    //#region Attribute

    private final ItemService itemService;

    //#endregion

    //#region Konstruktor
    public ItemController(ItemService itemService)
    {
        this.itemService = itemService;
    }
    //#endregion

    //#region MappingFunctions
    @PostMapping("/location")
    public ResponseEntity<Location> createLocation(@RequestBody Location location)
    {
        return itemService.createLocation(location);
    }

    @PostMapping("/category")
    public ResponseEntity<Category> createCategory(@RequestBody Category category)
    {
        return itemService.createCategory(category);
    }

    @GetMapping("/location")
    public List<Location> getLocations() {
        return itemService.getLocations();
    }

    @GetMapping("/category")
    public List<Category> getCategories() {
        return itemService.getCategories();
    }

    // CREATE
    @PostMapping
    public ResponseEntity<?> createItem(@RequestBody ItemRequest request)
    {
        return itemService.createItem(request);
    }

    // READ ALL
    @GetMapping
    public List<Item> getAllItems() { return itemService.getAllItems(); }

    // READ ONE
    @GetMapping("/{id}")
    public ResponseEntity<Item> getItemById(@PathVariable Long id)
    {
        return itemService.getItemById(id);
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<?> updateItem(@PathVariable Long id, @RequestBody ItemRequest updatedItem)
    {
        return itemService.updateItem(id, updatedItem);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) throws IOException
    {
        return itemService.deleteItem(id);
    }

    // GET QR-CODE IMAGE
    @GetMapping("/{id}/qrcode")
    public ResponseEntity<byte[]> getQrCodeImage(@PathVariable Long id)
    {
        return itemService.getQrCodeImage(id);
    }
    
    // GET NEWEST ITEM
    @GetMapping("/newest")
    public ResponseEntity<Item> getNewestItem()
    {
        return itemService.getNewestItem();
    }
    
    // GET OLDEST ITEM
    @GetMapping("/oldest")
    public ResponseEntity<Item> getOldestItem()
    {
        return itemService.getOldestItem();
    }
    
    // SEARCH ITEMS BY NAME
    @GetMapping("/search")
    public List<Item> searchItems(@RequestParam(required = false) String name)
    {
        return itemService.searchItemsByName(name);
    }
    
    // EXPORT TO EXCEL
    @GetMapping("/export/excel")
    public ResponseEntity<byte[]> exportToExcel()
    {
        return itemService.exportToExcel();
    }
    //#endregion
}
