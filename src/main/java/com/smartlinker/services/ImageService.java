package com.smartlinker.services;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public interface ImageService {

    String uploadImage(MultipartFile contactImage, String filename) throws IOException;

    String getUrlFromPublicId(String publicId);

}
