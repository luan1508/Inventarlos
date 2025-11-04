package com.herbei.qr_inventory.controller;

import com.herbei.qr_inventory.model.Item;
import com.herbei.qr_inventory.repository.ItemRepository;
import com.herbei.qr_inventory.service.QrCodeService;
import com.google.zxing.WriterException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.Base64;

@RestController
@RequestMapping("/items")
//@CrossOrigin(origins = "*")
public class ItemController 
{

    private final ItemRepository itemRepository;
    private final QrCodeService qrCodeService;

    public ItemController(ItemRepository itemRepository, QrCodeService qrCodeService) 
    {
        this.itemRepository = itemRepository;
        this.qrCodeService = qrCodeService;
    }

    // CREATE
    @PostMapping
    public ResponseEntity<ItemResponse> createItem(@RequestBody Item item) {

        Item savedItem = itemRepository.save(item);

        try
        {
            String baseDir = System.getProperty("user.dir");
            Path qrFolder = Path.of(baseDir, "qrcodes");
            Files.createDirectories(qrFolder);

            System.out.println("PATH: " +qrFolder);

            String qrFilePath = qrFolder.resolve("item-" + savedItem.getId() + ".png").toString();

            String qrText = "http://localhost:8080/item.html?id=" + savedItem.getId();

            qrCodeService.generateQRCodeImage(qrText, 250, 250, qrFilePath);

            byte[] qrBytes = Files.readAllBytes(Path.of(qrFilePath));
            String qrBase64 = Base64.getEncoder().encodeToString(qrBytes);

            savedItem.setQrCode(qrFilePath);
            itemRepository.save(savedItem);

            ItemResponse response = new ItemResponse(savedItem, qrBase64);
            return ResponseEntity.ok(response);

        } catch (WriterException | IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }



    // READ ALL
    @GetMapping
    public List<Item> getAllItems() { return itemRepository.findAll(); }

    // READ ONE
    @GetMapping("/{id}")
    public ResponseEntity<Item> getItemById(@PathVariable Long id) 
    {
        Optional<Item> item = itemRepository.findById(id);
        return item.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<Item> updateItem(@PathVariable Long id, @RequestBody Item updatedItem) 
    {
        return itemRepository.findById(id).map(item -> 
        {
            item.setName(updatedItem.getName());
            item.setBeschreibung(updatedItem.getBeschreibung());
            item.setCategory(updatedItem.getCategory());
            item.setLocation(updatedItem.getLocation());
            Item savedItem = itemRepository.save(item);
            return ResponseEntity.ok(savedItem);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) throws IOException 
    {
        Optional<Item> itemOpt = itemRepository.findById(id);
        if (itemOpt.isPresent()) 
        {
            Item item = itemOpt.get();
            if (item.getQrCode() != null) 
            {
                Files.deleteIfExists(Path.of(item.getQrCode()));
            }
            itemRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } 
        else 
        {
            return ResponseEntity.notFound().build();
        }
    }

    // GET QR-CODE IMAGE
    @GetMapping("/{id}/qrcode")
    public ResponseEntity<byte[]> getQrCodeImage(@PathVariable Long id) 
    {
        Optional<Item> itemOpt = itemRepository.findById(id);
        if (itemOpt.isEmpty()) 
        {
            return ResponseEntity.notFound().build();
        }

        Item item = itemOpt.get();
        if (item.getQrCode() == null || item.getQrCode().isBlank()) 
        {
            return ResponseEntity.notFound().build();
        }
        try 
        {
            byte[] qrBytes = Files.readAllBytes(Path.of(item.getQrCode()));
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .body(qrBytes);
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    // Hilfsklasse für Response mit Base64-Bild
    public record ItemResponse(Item item, String qrImageBase64) 
    {
        // ToDo 
    }
}
