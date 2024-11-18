package com.example.ourapp.ImageTest;

import java.io.File;
import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public class ImageHandler {

    public String save(MultipartFile image) throws IOException {
        // 올바른 경로 설정
        String baseDir = "C:/Users/82104/git/jieunsehun/src/main/resources/static/images/";
        File directory = new File(baseDir);

        // 디렉토리가 없으면 생성
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // 파일 저장
        String fileName = image.getOriginalFilename();
        String fullPath = baseDir + fileName;

        image.transferTo(new File(fullPath));
        System.out.println("File saved to: " + fullPath); // 로그 출력

        return fileName; // 반환 값은 파일 이름만
    }
}

