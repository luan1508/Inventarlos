package com.herbei.qr_inventory.service;

import com.google.zxing.WriterException;
import com.herbei.qr_inventory.model.Category;
import com.herbei.qr_inventory.model.Item;
import com.herbei.qr_inventory.model.ItemRequest;
import com.herbei.qr_inventory.model.Location;
import com.herbei.qr_inventory.repository.CategoryRepository;
import com.herbei.qr_inventory.repository.ItemRepository;
import com.herbei.qr_inventory.repository.LocationRepository;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ItemService
{
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final ItemRepository itemRepository;
    private final QrCodeService qrCodeService;

    public ItemService(CategoryRepository categoryRepository, LocationRepository locationRepository,
                       ItemRepository itemRepository, QrCodeService qrCodeService)
    {
        this.categoryRepository = categoryRepository;
        this.locationRepository = locationRepository;
        this.itemRepository = itemRepository;
        this.qrCodeService = qrCodeService;
    }

    public ResponseEntity<Location> createLocation(Location location)
    {
        Location saved = locationRepository.save(location);
        return ResponseEntity.ok(saved);
    }

    public ResponseEntity<Category> createCategory(Category category)
    {
        Category saved = categoryRepository.save(category);
        return ResponseEntity.ok(saved);
    }

    public List<Location> getLocations()
    {
        return locationRepository.findAll();
    }

    public List<Category> getCategories()
    {
        return categoryRepository.findAll();
    }

    public ResponseEntity<?> createItem(ItemRequest request)
    {
        List<String> missingFields = validateRequired(request);
        if (!missingFields.isEmpty())
        {
            String msg = "Bitte füllen Sie folgende Felder aus: Name, Standort, Kategorie";
            return ResponseEntity.badRequest().body(Map.of("message", msg, "missingFields", missingFields));
        }

        Location location = locationRepository.findByLocationName(request.getLocationName()).orElse(null);
        Category category = categoryRepository.findByCategoryName(request.getCategoryName()).orElse(null);
        List<String> invalidFields = new ArrayList<>();
        if (location == null) invalidFields.add("locationName");
        if (category == null) invalidFields.add("categoryName");
        if (!invalidFields.isEmpty())
        {
            String msg = "Ungültige Auswahl: Standort oder Kategorie existiert nicht";
            return ResponseEntity.badRequest().body(Map.of("message", msg, "missingFields", invalidFields));
        }

        Item item = new Item();
        item.setName(request.getName());
        item.setBeschreibung(request.getBeschreibung());
        item.setLocation(location);
        item.setCategory(category);

        Item savedItem = itemRepository.save(item);

        try
        {
            Path qrFolder = Path.of("qrcodes");
            Files.createDirectories(qrFolder);
            String qrFilePath = qrFolder.resolve("item-" + savedItem.getId() + ".png").toString();

            String qrText = "https://www.youtube.com/watch?v=xvFZjo5PgG0&list=RDx vFZjo5PgG0&start_radio=1"; //+ savedItem.getId();
            qrCodeService.generateQRCodeImage(qrText, 250, 250, qrFilePath);

            byte[] qrBytes = Files.readAllBytes(Path.of(qrFilePath));
            String qrBase64 = Base64.getEncoder().encodeToString(qrBytes);

            savedItem.setQrCode(qrFilePath);
            itemRepository.save(savedItem);

            ItemResponse response = new ItemResponse(savedItem, qrBase64);
            return ResponseEntity.ok(response);
        }
        catch (WriterException | IOException e)
        {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    public List<Item> getAllItems()
    {
        return itemRepository.findAll();
    }

    public ResponseEntity<Item> getItemById(Long id)
    {
        Optional<Item> item = itemRepository.findById(id);
        return item.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    public ResponseEntity<?> updateItem(Long id, ItemRequest updatedItem)
    {
        return itemRepository.findById(id).map(item ->
        {
            List<String> missingFields = validateRequired(updatedItem);
            if (!missingFields.isEmpty())
            {
                String msg = "Bitte füllen Sie folgende Felder aus: Name, Standort, Kategorie";
                return ResponseEntity.badRequest().body(Map.of("message", msg, "missingFields", missingFields));
            }

            Location location = locationRepository.findByLocationName(updatedItem.getLocationName()).orElse(null);
            Category category = categoryRepository.findByCategoryName(updatedItem.getCategoryName()).orElse(null);

            List<String> invalidFields = new ArrayList<>();
            if (location == null) invalidFields.add("locationName");
            if (category == null) invalidFields.add("categoryName");
            if (!invalidFields.isEmpty())
            {
                String msg = "Ungültige Auswahl: Standort oder Kategorie existiert nicht";
                return ResponseEntity.badRequest().body(Map.of("message", msg, "missingFields", invalidFields));
            }

            item.setName(updatedItem.getName());
            item.setBeschreibung(updatedItem.getBeschreibung());
            item.setCategory(category);
            item.setLocation(location);
            Item savedItem = itemRepository.save(item);
            return ResponseEntity.ok(savedItem);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    public ResponseEntity<Void> deleteItem(Long id) throws IOException
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
        return ResponseEntity.notFound().build();
    }

    public ResponseEntity<byte[]> getQrCodeImage(Long id)
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

    private List<String> validateRequired(ItemRequest request)
    {
        List<String> missingFields = new ArrayList<>();
        if (request.getName() == null || request.getName().isBlank()) missingFields.add("name");
        if (request.getLocationName() == null || request.getLocationName().isBlank()) missingFields.add("locationName");
        if (request.getCategoryName() == null || request.getCategoryName().isBlank()) missingFields.add("categoryName");
        return missingFields;
    }

    public record ItemResponse(Item item, String qrImageBase64) {}
}
