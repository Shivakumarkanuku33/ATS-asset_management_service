package com.ats.assetservice.document.service.impl;

import com.ats.assetservice.entity.Asset;
import com.ats.assetservice.entity.AssetStatus;
import com.ats.assetservice.document.service.ExcelService;
import com.ats.assetservice.mapper.AssetMapper;
import com.ats.assetservice.repository.AssetRepository;
import com.ats.assetservice.service.AuditLogService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.commons.compress.archivers.dump.InvalidFormatException;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ExcelServiceImpl implements ExcelService {

    private final AssetRepository assetRepository;
    private final AssetMapper mapper;
    private final AuditLogService auditLogService;

    private static final String[] HEADERS = {
            "assetTag","name","category","status","purchaseDate","purchaseCost",
            "vendor","warrantyMonths","description","locationId"
    };

    @Override
    @Transactional
    public List<String> importFromExcel(MultipartFile file) throws IOException {
        List<String> errors = new ArrayList<>();
        if (file == null || file.isEmpty()) {
            errors.add("File is empty");
            return errors;
        }

        try (InputStream is = file.getInputStream();
             Workbook workbook = WorkbookFactory.create(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                errors.add("No sheet found in file");
                return errors;
            }

            int batchSize = 50;
            List<Asset> batch = new ArrayList<>(batchSize);

            // Read headers
            Row headerRow = sheet.getRow(0);
            Map<String, Integer> headerIndex = new HashMap<>();
            for (int c = 0; c < headerRow.getLastCellNum(); c++) {
                Cell cell = headerRow.getCell(c);
                if (cell != null) {
                    headerIndex.put(cell.getStringCellValue().trim(), c);
                }
            }

            for (int r = 1; r <= sheet.getLastRowNum(); r++) {
                Row row = sheet.getRow(r);
                if (row == null) continue;

                try {
                    Asset asset = parseRowToAsset(row, headerIndex);

                    // --- EXISTING ASSET CHECK & LIFECYCLE UPDATE ---
                    Optional<Asset> existing = assetRepository.findByAssetTag(asset.getAssetTag());
                    if (existing.isPresent()) {
                        Asset db = existing.get();

                        // Update lifecycle/status if provided in Excel
                        if (asset.getStatus() != null && !asset.getStatus().isBlank()) {
                            db.setStatus(asset.getStatus());
                        }

                        // Update other fields
                        db.setName(asset.getName());
                        db.setCategory(asset.getCategory());
                        db.setVendor(asset.getVendor());
                        db.setDescription(asset.getDescription());
                        db.setLocationId(asset.getLocationId());
                        db.setPurchaseCost(asset.getPurchaseCost());
                        db.setPurchaseDate(asset.getPurchaseDate());
                        db.setWarrantyMonths(asset.getWarrantyMonths());

                        db.setUpdatedAt(java.time.LocalDateTime.now());

                        batch.add(db);
                        continue; // Skip new asset logic
                    }

                    // --- NEW ASSET ---
                    if (asset.getAssetTag() == null || asset.getAssetTag().isBlank()) {
                        asset.setAssetTag("AST-" + UUID.randomUUID().toString().substring(0, 8));
                    }

                    // Default lifecycle status
                    if (asset.getStatus() == null || asset.getStatus().isBlank()) {
                        asset.setStatus(AssetStatus.AVAILABLE.name());
                    }

                    asset.setIsActive(true);
                    asset.setCreatedAt(java.time.LocalDateTime.now());
                    asset.setUpdatedAt(java.time.LocalDateTime.now());

                    batch.add(asset);

                    if (batch.size() == batchSize) {
                        assetRepository.saveAll(batch);
                        batch.forEach(a -> auditLogService.logCreate(a));
                        batch.clear();
                    }

                } catch (Exception ex) {
                    errors.add("Row " + (r + 1) + " : " + ex.getMessage());
                }
            }

            if (!batch.isEmpty()) {
                assetRepository.saveAll(batch);
                batch.forEach(a -> auditLogService.logCreate(a));
            }

        } catch (InvalidFormatException e) {
            errors.add("Invalid Excel format: " + e.getMessage());
        }

        return errors;
    }

    private Asset parseRowToAsset(Row row, Map<String, Integer> headerIndex) {
        Asset asset = new Asset();

        asset.setAssetTag(getStringCell(row, headerIndex.get("assetTag")));
        asset.setName(getStringCell(row, headerIndex.get("name")));
        asset.setCategory(getStringCell(row, headerIndex.get("category")));

        // --- LIFECYCLE PARSING ---
        String statusStr = getStringCell(row, headerIndex.get("status"));
        if (statusStr != null && !statusStr.isBlank()) {
            try {
                AssetStatus lifecycle = AssetStatus.valueOf(statusStr.trim().toUpperCase());
                asset.setStatus(lifecycle.name());
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid lifecycle/status: " + statusStr);
            }
        }

        // purchaseDate as yyyy-MM-dd or Excel date
        Cell pdCell = getCell(row, headerIndex.get("purchaseDate"));
        if (pdCell != null) {
            if (pdCell.getCellType() == CellType.STRING) {
                String v = pdCell.getStringCellValue();
                if (v != null && !v.isBlank()) {
                    asset.setPurchaseDate(LocalDate.parse(v.trim()));
                }
            } else if (DateUtil.isCellDateFormatted(pdCell)) {
                Date date = pdCell.getDateCellValue();
                asset.setPurchaseDate(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            }
        }

        // purchaseCost (numeric)
        Cell pcCell = getCell(row, headerIndex.get("purchaseCost"));
        if (pcCell != null) {
            if (pcCell.getCellType() == CellType.NUMERIC) {
                asset.setPurchaseCost(pcCell.getNumericCellValue());
            } else if (pcCell.getCellType() == CellType.STRING) {
                String v = pcCell.getStringCellValue();
                if (v != null && !v.isBlank()) {
                    asset.setPurchaseCost(Double.parseDouble(v.trim()));
                }
            }
        }

        asset.setVendor(getStringCell(row, headerIndex.get("vendor")));
        Integer w = getIntCell(row, headerIndex.get("warrantyMonths"));
        asset.setWarrantyMonths(w);
        asset.setDescription(getStringCell(row, headerIndex.get("description")));
        Long loc = getLongCell(row, headerIndex.get("locationId"));
        asset.setLocationId(loc);

        // validations for required fields
        if (asset.getName() == null || asset.getName().isBlank())
            throw new IllegalArgumentException("name is required");
        if (asset.getCategory() == null || asset.getCategory().isBlank())
            throw new IllegalArgumentException("category is required");

        return asset;
    }

    private Cell getCell(Row row, Integer idx) {
        if (idx == null) return null;
        return row.getCell(idx);
    }

    private String getStringCell(Row row, Integer idx) {
        Cell c = getCell(row, idx);
        if (c == null) return null;
        if (c.getCellType() == CellType.STRING) return c.getStringCellValue().trim();
        if (c.getCellType() == CellType.NUMERIC) return String.valueOf(c.getNumericCellValue());
        if (c.getCellType() == CellType.BOOLEAN) return String.valueOf(c.getBooleanCellValue());
        return c.toString().trim();
    }

    private Integer getIntCell(Row row, Integer idx) {
        Cell c = getCell(row, idx);
        if (c == null) return null;
        if (c.getCellType() == CellType.NUMERIC) return (int) c.getNumericCellValue();
        try {
            return Integer.valueOf(getStringCell(row, idx));
        } catch (Exception ex) {
            return null;
        }
    }

    private Long getLongCell(Row row, Integer idx) {
        Cell c = getCell(row, idx);
        if (c == null) return null;
        if (c.getCellType() == CellType.NUMERIC) return (long) c.getNumericCellValue();
        try {
            return Long.valueOf(getStringCell(row, idx));
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public void exportToExcel(HttpServletResponse response) throws IOException {
        List<Asset> assets = assetRepository.findAll();

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Assets");
            // header
            Row header = sheet.createRow(0);
            for (int i = 0; i < HEADERS.length; i++) {
                header.createCell(i).setCellValue(HEADERS[i]);
            }

            int r = 1;
            for (Asset a : assets) {
                Row row = sheet.createRow(r++);
                row.createCell(0).setCellValue(a.getAssetTag());
                row.createCell(1).setCellValue(a.getName());
                row.createCell(2).setCellValue(a.getCategory());
                row.createCell(3).setCellValue(a.getStatus());
                row.createCell(4).setCellValue(a.getPurchaseDate() != null ? a.getPurchaseDate().toString() : "");
                row.createCell(5).setCellValue(a.getPurchaseCost() != null ? a.getPurchaseCost() : 0);
                row.createCell(6).setCellValue(a.getVendor() != null ? a.getVendor() : "");
                row.createCell(7).setCellValue(a.getWarrantyMonths() != null ? a.getWarrantyMonths() : 0);
                row.createCell(8).setCellValue(a.getDescription() != null ? a.getDescription() : "");
                row.createCell(9).setCellValue(a.getLocationId() != null ? a.getLocationId() : 0);
            }

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=assets.xlsx");
            workbook.write(response.getOutputStream());
            response.flushBuffer();
        }
    }
}
