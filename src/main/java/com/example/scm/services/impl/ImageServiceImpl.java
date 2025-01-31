package com.example.scm.services.impl;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import com.example.scm.helper.AppConstants;
import com.example.scm.services.ImageService;

@Service
public class ImageServiceImpl implements ImageService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Cloudinary cloudinary;

    public ImageServiceImpl(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    @Override
    public String uploadImage(MultipartFile imageFile, String fileName) {
        logger.info("upload image called");

        try {
            byte[] data = imageFile.getBytes(); // 

            // Upload image to Cloudinary
            var response = cloudinary.uploader().upload(data,
                    ObjectUtils.asMap("public_id", fileName));

            logger.info("Image uploaded successfully: {}", response.get("url"));

            // Return the image URL
            return response.get("url").toString();
        } catch (IOException e) {
            logger.error("Error uploading file: {}", fileName, e);
            return null;
        }
    }

    @Override
    public String getUrlByPublicId(String publicId) {
        return cloudinary
                .url()
                .transformation(
                        new Transformation<>()
                                .height(AppConstants.CONTACT_IMAGE_HEIGHT)
                                .width(AppConstants.CONTACT_IMAGE_WIDTH)
                                .crop(AppConstants.CONTACT_IMAGE_CROP))
                .generate(publicId);
    }

}

// String picturePath = null;
// MultipartFile picture = contactForm.getPicture();
// if (picture != null && !picture.isEmpty()) {
// // Save the file to the server or process it as needed
// picturePath = "C:\\Users\\HP\\OneDrive\\" + picture.getOriginalFilename();
// picture.transferTo(new File(picturePath));
// }