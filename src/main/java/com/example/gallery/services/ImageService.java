package com.example.gallery.services;

import com.example.gallery.models.Image;
import com.example.gallery.models.User;
import com.example.gallery.repositories.ImageRepository;
import com.example.gallery.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ImageService {

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    public List<Image> getImages() {
        return imageRepository.findAll();
    }

    public String add(Image image) {
        String username = userService.getCurrentLoggedInUser().getUsername();

        image.setTitle(image.getTitle().trim());

        if (image.getTitle().length() < 3 || image.getTitle().length() > 255) {
            return "Tytuł musi zawierać od 3 do 255 znaków";
        }

        Date d = new Date();
        Date date = new Date(d.getTime());
        image.setDate(date);

        byte[] empty = new byte[0];
        image.setFile(empty);

        image.setWhoAdded(username);

        imageRepository.save(image);

        return "Pomyślnie dodano obraz";
    }

    public String update(Image image) {
        String username = userService.getCurrentLoggedInUser().getUsername();

        if (username.equals(image.getWhoAdded())) {

            image.setTitle(image.getTitle().trim());

            if (image.getTitle().length() < 3 || image.getTitle().length() > 255) {
                return "Tytuł musi zawierać od 3 do 255 znaków";
            }

            Date d = new Date();
            Date date = new Date(d.getTime());
            image.setDate(date);

            Image oldImage = imageRepository.getById(image.getId());

            image.setWhoAdded(oldImage.getWhoAdded());

            byte[] empty = new byte[0];
            if (!Arrays.equals(image.getFile(), empty)) {

                if (oldImage.getFile() != image.getFile()) {
                    image.setFile(oldImage.getFile());
                }

                image.setHidden(false);
                imageRepository.save(image);

                return "Pomyślnie zaktualizowano obraz";
            }
            
            return "Nie dodano obrazu";
        }

        return "Nie można zmodyfikować obrazu nienależącego do ciebie";
    }


    public Optional<Image> findById(Integer id) {
        return imageRepository.findById(id);
    }

    public String delete(Integer id) {
        Image image = imageRepository.findById(id).orElse(null);
        if (image != null) {
            cancelAllLikes(id);
            imageRepository.deleteById(id);
            return "Obraz został usunięty";

        }
        return "Wskazany obraz nie istnieje";
    }

    public String delete_(Integer id) {
        Image image = imageRepository.findById(id).orElse(null);
        if (image != null) {
            String username = userService.getCurrentLoggedInUser().getUsername();
            if (username.equals(image.getWhoAdded())) {
                cancelAllLikes(id);
                imageRepository.deleteById(id);
                return "Obraz został usunięty";
            }

            return "Nie możesz usunąć obrazu nienależącego do ciebie";
        }

        return "Wskazany obraz nie istnieje";
    }

    public String like(Integer imageId) {
        Image image = imageRepository.findById(imageId).orElse(null);
        User user = userService.getCurrentLoggedInUser();

        if (image != null && !image.isHidden()) {
            Set<Image> userImages = user.getImages();

            if (userImages.contains(image)) {
                userImages.remove(image);
                user.setImages(userImages);

                image.setLikes(image.getLikes() - 1);

                userRepository.save(user);
                imageRepository.save(image);

                return "Cofnięto polubienie";
            }

            userImages.add(image);
            user.setImages(userImages);

            image.setLikes(image.getLikes() + 1);

            userRepository.save(user);
            imageRepository.save(image);

            return "Polubiono obraz";
        }

        return "Nie udało się polubić obrazu";
    }

    public void cancelAllLikes(Integer imageId) {

        List<User> users = userRepository.findAll();

        for (User user: users) {
            Set<Image> userImages = user.getImages();
            userImages.removeIf(x -> x.getId().equals(imageId));

            userRepository.save(user);
        }

    }

}
