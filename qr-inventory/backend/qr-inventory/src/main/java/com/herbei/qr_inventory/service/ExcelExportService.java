package com.herbei.qr_inventory.service;

import com.herbei.qr_inventory.model.Item;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ExcelExportService 
{
    /**
     * Erstellt eine Excel-Datei aus einer Liste von Items
     * @param items Liste der zu exportierenden Items
     * @return Excel-Datei als Byte-Array
     * @throws IOException bei Schreibfehlern
     */
    public byte[] createExcelFile(List<Item> items) throws IOException 
    {
        try (Workbook workbook = new XSSFWorkbook();
            ByteArrayOutputStream out = new ByteArrayOutputStream()) 
        {
            Sheet sheet = workbook.createSheet("Items");
            
            // Erstelle Header-Zeile
            createHeaderRow(sheet, workbook);
            
            // Fülle Daten
            fillDataRows(sheet, items, workbook);
            
            // Auto-size columns
            autoSizeColumns(sheet, 6);
            
            // Schreibe in Output Stream
            workbook.write(out);
            return out.toByteArray();
        }
    }
    
    /**
     * Erstellt die Header-Zeile mit Formatierung
     */
    private void createHeaderRow(Sheet sheet, Workbook workbook) 
    {
        Row headerRow = sheet.createRow(0);
        CellStyle headerStyle = createHeaderCellStyle(workbook);
        
        String[] headers = {"ID", "Name", "Beschreibung", "Standort", "Kategorie", "QR-Code"};
        
        for (int i = 0; i < headers.length; i++) 
        {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
    }
    
    /**
     * Füllt die Datenzeilen
     */
    private void fillDataRows(Sheet sheet, List<Item> items, Workbook workbook) 
    {
        CellStyle dataStyle = createDataCellStyle(workbook);
        int rowNum = 1;
        
        for (Item item : items) 
        {
            Row row = sheet.createRow(rowNum++);
            
            // ID
            Cell cell0 = row.createCell(0);
            cell0.setCellValue(item.getId());
            cell0.setCellStyle(dataStyle);
            
            // Name
            Cell cell1 = row.createCell(1);
            cell1.setCellValue(item.getName() != null ? item.getName() : "");
            cell1.setCellStyle(dataStyle);
            
            // Beschreibung
            Cell cell2 = row.createCell(2);
            cell2.setCellValue(item.getBeschreibung() != null ? item.getBeschreibung() : "");
            cell2.setCellStyle(dataStyle);
            
            // Standort
            Cell cell3 = row.createCell(3);
            String location = item.getLocation() != null ? item.getLocation().getName() : "";
            cell3.setCellValue(location);
            cell3.setCellStyle(dataStyle);
            
            // Kategorie
            Cell cell4 = row.createCell(4);
            String category = item.getCategory() != null ? item.getCategory().getName() : "";
            cell4.setCellValue(category);
            cell4.setCellStyle(dataStyle);
            
            // QR-Code
            Cell cell5 = row.createCell(5);
            cell5.setCellValue(item.getQrCode() != null ? item.getQrCode() : "");
            cell5.setCellStyle(dataStyle);
        }
    }
    
    /**
     * Erstellt den Style für Header-Zellen
     */
    private CellStyle createHeaderCellStyle(Workbook workbook) 
    {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        font.setColor(IndexedColors.WHITE.getIndex());
        
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_80_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        
        return style;
    }
    
    /**
     * Erstellt den Style für Daten-Zellen
     */
    private CellStyle createDataCellStyle(Workbook workbook) 
    {
        CellStyle style = workbook.createCellStyle();
        
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        
        return style;
    }
    
    /**
     * Passt die Spaltenbreite automatisch an
     */
    private void autoSizeColumns(Sheet sheet, int numberOfColumns) 
    {
        for (int i = 0; i < numberOfColumns; i++) 
        {
            sheet.autoSizeColumn(i);
            // Füge etwas Padding hinzu
            int currentWidth = sheet.getColumnWidth(i);
            sheet.setColumnWidth(i, currentWidth + 1000);
        }
    }
    
    /**
     * Generiert einen Dateinamen mit Zeitstempel
     */
    public String generateFileName() 
    {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        return "QR-Inventar_Export_" + now.format(formatter) + ".xlsx";
    }
}
