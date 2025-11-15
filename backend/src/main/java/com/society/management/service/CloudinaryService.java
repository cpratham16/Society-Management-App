package com.society.management.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class CloudinaryService {

    @Autowired
    private Cloudinary cloudinary;

    // Upload Image
    public String uploadImage(MultipartFile file, String folder) {
        try {
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

            Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap(
                            "public_id", fileName,
                            "folder", "society-management/" + folder,
                            "resource_type", "auto"
                    ));

            String secureUrl = (String) uploadResult.get("secure_url");
            log.info("Image uploaded to Cloudinary: {}", secureUrl);

            return secureUrl;
        } catch (IOException e) {
            log.error("Failed to upload image: {}", e.getMessage());
            throw new RuntimeException("Image upload failed", e);
        }
    }

    // Delete Image from Cloudinary
    public void deleteImage(String imageUrl) {
        try {
            // Extract public_id from URL
            String publicId = extractPublicIdFromUrl(imageUrl);

            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            log.info("Image deleted from Cloudinary: {}", publicId);
        } catch (IOException e) {
            log.error("Failed to delete image: {}", e.getMessage());
        }
    }

    // Extract public_id from Cloudinary URL
    private String extractPublicIdFromUrl(String url) {
        // URL format: https://res.cloudinary.com/cloud-name/image/upload/...public_id.ext
        // Extract the part after /upload/
        int uploadIndex = url.indexOf("/upload/");
        if (uploadIndex != -1) {
            String path = url.substring(uploadIndex + 8); // Skip "/upload/"
            // Remove version number if present
            if (path.startsWith("v")) {
                path = path.substring(path.indexOf("/") + 1);
            }
            // Remove file extension
            return path.substring(0, path.lastIndexOf("."));
        }
        return url;
    }
}