package com.example.ourapp.map;


import com.example.ourapp.entity.UserSearchHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
@Controller
public class MapController {

    @Autowired
    private MapRepository mapRepository;

    @GetMapping("/my-map")
    public String getMyMapPage(@RequestParam(name = "userId") Long userId, Model model) {
        List<UserSearchHistory> myMapLocations = mapRepository.findByUserIdAndSaveType(userId, UserSearchHistory.SaveType.MY_MAP);
        model.addAttribute("myMapLocations", myMapLocations);
        return "mymap";
    }
}