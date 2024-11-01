package com.example.ourapp.map;

import com.example.ourapp.entity.MyMapPoint;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.List;

@Controller
public class MapController {

    @Autowired
    private MapRepository mapRepository;

    @GetMapping("/mymap")
    public String getMyMapPage(HttpSession session) {
    	System.out.println(session.getAttribute("user_id"));
        return "mymap";  // HTML 파일의 확장자는 생략합니다.
    }

    @GetMapping("/api/mymap")
    @ResponseBody
    public List<MyMapPoint> getMyMapPoints(HttpSession session) {
    	Long user_id = (Long)session.getAttribute("user_id");
    	System.out.println(user_id+"asasd");
    	List <MyMapPoint> a = mapRepository.findByUserId(user_id);
    	for (MyMapPoint point : a) {
            System.out.println(point.getLocationDesc());
        }
        return mapRepository.findByUserId(user_id);
    }
    
   
}
