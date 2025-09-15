package com.smartlinker.services.impl;

import java.io.IOException;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import com.smartlinker.helpers.AppConstants;
import com.smartlinker.services.ImageService;

@Service
public class ImageServiceImpl implements ImageService {

    private final Cloudinary cloudinary;

    public ImageServiceImpl(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    @Override
    public String uploadImage(MultipartFile contactImage, String filename) throws IOException {
        try {
            // Validate input file
            if (contactImage == null || contactImage.isEmpty()) {
                throw new IllegalArgumentException("Image file is empty");
            }

            // Upload to Cloudinary
            Map<?, ?> uploadResult = cloudinary.uploader().upload(
                contactImage.getBytes(),
                ObjectUtils.asMap(
                    "public_id", filename,
                    "folder", "smartlinker/contacts"  
                )
            );

            // Return secure HTTPS URL
            return uploadResult.get("secure_url").toString();
            
        } catch (IOException e) {
            throw new IOException("Failed to upload image to Cloudinary", e);
        }
    }

    @Override
    public String getUrlFromPublicId(String publicId) {
        try {
            return cloudinary.url()
                .transformation(new Transformation<>()
                    .width(AppConstants.CONTACT_IMAGE_WIDTH)
                    .height(AppConstants.CONTACT_IMAGE_HEIGHT)
                    .crop(AppConstants.CONTACT_IMAGE_CROP))
                .secure(true)  
                .generate(publicId);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate image URL", e);
        }
    }
}
