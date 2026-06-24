package com.prepXBackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.prepXBackend.helper.CSVHelper;
import com.prepXBackend.model.Question;
import com.prepXBackend.repository.QuestionRepository;
import com.prepXBackend.service.QuestionService;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/admin")
public class AdminQuestionController {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuestionService questionService;

    @Value("${admin.password}")
    private String adminPassword;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadCSVFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("password") String password) {

        if (!password.equals(adminPassword)) {
            return ResponseEntity.status(403).body("Invalid Admin Password");
        }

        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body("Error: No file uploaded.");
        }

        if (!CSVHelper.hasCSVFormat(file)) {
            return ResponseEntity.badRequest().body("Error: Invalid file format. Please upload a CSV file.");
        }

        questionService.saveQuestionsFromCSV(file);
        return ResponseEntity.ok("CSV file uploaded and questions saved successfully!");
    }

    @PostMapping("/add-questions")
    public ResponseEntity<String> addQuestions(
            @RequestBody List<Question> questions,
            @RequestParam("password") String password) {

        if (!password.equals(adminPassword)) {
            return ResponseEntity.status(403).body("Invalid Admin Password");
        }

        questionRepository.saveAll(questions);
        return ResponseEntity.ok("Questions added successfully!");
    }
}