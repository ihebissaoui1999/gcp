package com.example.chatintell.Contrllercloudiriya;

import com.example.chatintell.service.FileUpload;
import com.example.chatintell.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.cloudinary.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cloud")
public class CloudiriyaController {

    private final FileUploadService fileUpload;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("image") MultipartFile multipartFile) {
        try {
            String imageURL = fileUpload.uploadFile(multipartFile);
            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("imageURL", imageURL);
            return ResponseEntity.ok(jsonResponse.toString());
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Une erreur s'est produite lors du téléchargement de l'image.");
        }
    }}
