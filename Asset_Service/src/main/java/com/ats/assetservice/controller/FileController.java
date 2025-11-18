package com.ats.assetservice.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ats.assetservice.document.service.ExcelService;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/assets/excel")
public class FileController {

	@Autowired
	ExcelService excelService;
	
	@PostMapping("/import")
	public ResponseEntity<?> importExcel(@RequestParam("file") MultipartFile file) throws IOException {
		 List<String> errors = excelService.importFromExcel(file);

		if (errors.isEmpty()) {
	        return ResponseEntity.ok("Excel imported successfully");
	    } else {
	        return ResponseEntity.badRequest().body(errors);
	    }
//	    excelService.importFromExcel(file);
//	    return ResponseEntity.ok().build();
	}

	@GetMapping("/export")
	public void exportExcel(HttpServletResponse response) throws IOException {
	    response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
	    response.setHeader("Content-Disposition", "attachment; filename=assets.xlsx");
	    excelService.exportToExcel(response);
	}
}
