package com.example.gallery.controllers;

import com.example.gallery.models.Image;
import com.example.gallery.models.User;
import com.example.gallery.services.ImageService;
import com.example.gallery.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Set;

@Controller
public class ApplicationController {

    @Autowired
    private ImageService imageService;

    @Autowired
    private UserService userService;

    @GetMapping("/index")
    public String index(Model model) {
        User user = userService.getCurrentLoggedInUser();
        Set<Image> likedImageSet = user.getImages();

        List<Image> imageList = imageService.getImages();
        imageList.removeIf(Image::isHidden);
        List<String> fileList = new ArrayList<>();

        List<Image> finalImageList = new ArrayList<>();
        for (Image image : imageList) {
            if (likedImageSet.contains(image)) {
                image.setLikedByCurrentUser(true);
            }
            finalImageList.add(image);

            if (image.getFile() != null) {
                fileList.add(Base64.getEncoder().encodeToString(image.getFile()));
            } else {
                fileList.add("");
            }
        }

        model.addAttribute("images", finalImageList);
        model.addAttribute("files", fileList);

        return "index";
    }

    @GetMapping("/admin/dashboard")
    public String dashboard() {
        return "admin/dashboard";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/logout")
    public String logout() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

}
