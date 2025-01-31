package com.example.scm.services;

import org.springframework.web.multipart.MultipartFile;

public interface ImageService {
    
    public String uploadImage(MultipartFile imageFile,String fileName);
    String getUrlByPublicId(String publicId);
}
