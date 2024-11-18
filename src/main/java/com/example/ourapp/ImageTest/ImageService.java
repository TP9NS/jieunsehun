package com.example.ourapp.ImageTest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class ImageService {

    @Autowired
    private ImageRepository imageRepository;

    private final ImageHandler imageHandler = new ImageHandler();

    public boolean saveImage(MultipartFile image) throws IOException {
        String fileName = imageHandler.save(image); // 파일 이름만 반환
        System.out.println("File saved to: " + fileName);

        Image imageEntity = new Image();
        imageEntity.setPath(fileName); // 파일 이름만 저장
        imageRepository.save(imageEntity);
        System.out.println("Image saved in database with path: " + fileName);

        return true;
    }

    public Optional<String> findOne() {
        List<Image> list = imageRepository.findAll();
        if (list.isEmpty()) return Optional.empty();
        return Optional.of(list.get(0).getPath()); // 파일 이름 반환
    }

}
