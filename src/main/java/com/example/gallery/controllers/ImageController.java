package com.example.gallery.controllers;

import com.example.gallery.models.Image;
import com.example.gallery.repositories.UserRepository;
import com.example.gallery.services.ImageService;
import com.example.gallery.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;

@Controller
public class ImageController {

    @Autowired
    private ImageService imageService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/images")
    public String getImages(Model model) {
        String username = userService.getCurrentLoggedInUser().getUsername();

        List<Image> imageList = imageService.getImages();
        Predicate<Image> doesNotBelongToUser = img->(!img.getWhoAdded().equals(username));
        imageList.removeIf(doesNotBelongToUser);

        List<String> fileList = new ArrayList<>();

        for (Image image : imageList) {
            if (image.getFile() != null) {
                fileList.add(Base64.getEncoder().encodeToString(image.getFile()));
            } else {
                fileList.add("");
            }
        }

        model.addAttribute("images", imageList);
        model.addAttribute("files", fileList);

        return "image";
    }

    @PostMapping("/images/addNew")
    public RedirectView addNew_(Image image, RedirectAttributes redirectAttributes) {
        String message = imageService.add(image);
        RedirectView redirectView = new RedirectView("/images", true);
        redirectAttributes.addFlashAttribute("message", message);
        return redirectView;
    }

    @RequestMapping(value="/images/update", method={RequestMethod.PUT, RequestMethod.GET})
    public RedirectView update_(Image image, RedirectAttributes redirectAttributes) {
        String message = imageService.update(image);
        RedirectView redirectView = new RedirectView("/images", true);
        redirectAttributes.addFlashAttribute("message", message);
        return redirectView;
    }

    @RequestMapping(value="/images/delete", method={RequestMethod.DELETE, RequestMethod.GET})
    public RedirectView delete_(Integer id, RedirectAttributes redirectAttributes) {
        String message = imageService.delete_(id);
        RedirectView redirectView = new RedirectView("/images", true);
        redirectAttributes.addFlashAttribute("message", message);
        return redirectView;
    }

    @RequestMapping("/images/findById")
    @ResponseBody
    public Optional<Image> findById_(Integer id) {
        return imageService.findById(id);
    }

    @RequestMapping(value="/images/file/{imageId}", method = RequestMethod.POST, consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public String file_(@RequestParam("file") MultipartFile file, @PathVariable Integer imageId) throws IOException {
        Image image = imageService.findById(imageId).orElse(null);

        if (image != null) {
            image.setFile(file.getBytes());
            imageService.update(image);
        }

        return "redirect:/images";
    }

    @GetMapping("/admin/images")
    public String getImagesAdmin(Model model) {

        List<Image> imageList = imageService.getImages();
        List<String> fileList = new ArrayList<>();

        for (Image image : imageList) {
            if (image.getFile() != null) {
                fileList.add(Base64.getEncoder().encodeToString(image.getFile()));
            } else {
                fileList.add("");
            }
        }

        model.addAttribute("images", imageList);
        model.addAttribute("files", fileList);

        return "admin/image";
    }

//    @RequestMapping(value="/admin/images/file/{imageId}", method = RequestMethod.POST, consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
//    public String file(@RequestParam("file") MultipartFile file, @PathVariable Integer imageId) throws IOException {
//        Image image = imageService.findById(imageId).orElse(null);
//
//        if (image != null) {
//            image.setHidden(false);
//            image.setFile(file.getBytes());
//            imageService.update(image);
//        }
//
//        return "redirect:/admin/images";
//    }

//    @PostMapping("/admin/images/addNew")
//    public RedirectView addNew(Image image, RedirectAttributes redirectAttributes) {
//        String message = imageService.add(image);
//        RedirectView redirectView = new RedirectView("/admin/images", true);
//        redirectAttributes.addFlashAttribute("message", message);
//        return redirectView;
//    }

//    @RequestMapping("/admin/images/findById")
//    @ResponseBody
//    public Optional<Image> findById(Integer id) {
//        return imageService.findById(id);
//    }

//    @RequestMapping(value="/admin/images/update", method={RequestMethod.PUT, RequestMethod.GET})
//    public RedirectView update(Image image, RedirectAttributes redirectAttributes) {
//        String message = imageService.update(image);
//        RedirectView redirectView = new RedirectView("/admin/images", true);
//        redirectAttributes.addFlashAttribute("message", message);
//        return redirectView;
//    }

    @RequestMapping(value="/admin/images/delete", method={RequestMethod.DELETE, RequestMethod.GET})
    public RedirectView delete(Integer id, RedirectAttributes redirectAttributes) {
        String message = imageService.delete(id);
        RedirectView redirectView = new RedirectView("/admin/images", true);
        redirectAttributes.addFlashAttribute("message", message);
        return redirectView;
    }

    @RequestMapping("/images/like/{imageId}")
    public RedirectView like(@PathVariable Integer imageId, RedirectAttributes redirectAttributes) {
        String message = imageService.like(imageId);
        RedirectView redirectView = new RedirectView("/index", true);
        redirectAttributes.addFlashAttribute("message", message);
        return redirectView;
    }

}
