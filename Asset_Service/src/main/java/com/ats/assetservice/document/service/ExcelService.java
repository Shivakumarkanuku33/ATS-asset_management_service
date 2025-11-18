package com.ats.assetservice.document.service;

import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

public interface ExcelService {

	List<String> importFromExcel(MultipartFile file) throws IOException;
	
	void exportToExcel(HttpServletResponse response) throws IOException;
}
