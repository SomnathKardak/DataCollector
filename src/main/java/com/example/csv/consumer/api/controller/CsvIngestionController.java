package com.example.csv.consumer.api.controller;

import com.example.csv.consumer.api.service.CsvIngestionService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/csv")
public class CsvIngestionController {

    private final CsvIngestionService ingestionService;

    public CsvIngestionController(CsvIngestionService ingestionService) {
        this.ingestionService = ingestionService;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadCsv(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "type", required = false) String type) {
        try {
            CsvIngestionService.IngestionResult result = ingestionService.ingestCsv(file, type);
            return ResponseEntity.ok(Map.of(
                    "status", "SUCCESS",
                    "fileName", result.fileName(),
                    "table", result.tableName(),
                    "rowsInserted", result.rowsInserted()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "BAD_REQUEST",
                    "message", e.getMessage()
            ));
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "status", "ERROR",
                    "message", "Failed to read CSV file: " + e.getMessage()
            ));
        }
    }
}