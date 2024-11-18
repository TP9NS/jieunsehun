package com.example.ourapp.ImageTest;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.core.io.Resource;


@Controller  
public class ImageController {  
@Autowired  
ImageService imageService;

@GetMapping("/upload")  
public String getUploadPage(){  
return "upload";  
}

@GetMapping("/view")
public String getViewPage(Model model) {
    Optional<String> imgPath = imageService.findOne();
    if (imgPath.isPresent()) {
        String fileName = imgPath.get();
        System.out.println("Image path retrieved from database: " + fileName);
        model.addAttribute("img", "/images/" + fileName); // 웹 경로로 설정
    } else {
        System.out.println("No image found in database.");
        model.addAttribute("img", null);
    }
    return "view2";
}



@ResponseBody
@GetMapping("/images/{filename}")
public ResponseEntity<Resource> showImage(@PathVariable String filename) throws MalformedURLException {
    String path = "C:/Users/82104/git/jieunsehun/src/main/resources/static/images/";
    UrlResource resource = new UrlResource("file:" + path + filename);

    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.CONTENT_TYPE, "image/png"); // PNG 타입 설정

    return ResponseEntity.ok()
                         .headers(headers)
                         .body(resource);
}

@PostMapping("/save")
public String postSaveFile(@RequestParam MultipartFile image) throws IOException {
    if (!image.isEmpty()) {
        imageService.saveImage(image);
    }
    return "redirect:/view"; // 리다이렉트 시 데이터는 데이터베이스에서 조회
}

}