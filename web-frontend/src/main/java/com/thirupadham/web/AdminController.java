package com.thirupadham.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
public class AdminController {

    private final PhotoStorage photoStorage;

    public AdminController(PhotoStorage photoStorage) {
        this.photoStorage = photoStorage;
    }

    @GetMapping("/admin")
    public String dashboard(Model model) {
        model.addAttribute("photos", photoStorage.listLatest(50));
        return "admin";
    }

    @PostMapping("/admin/upload")
    public String upload(@RequestParam("photo") MultipartFile photo, Model model) {
        if (!photo.isEmpty()) {
            try {
                photoStorage.save(photo);
            } catch (IOException e) {
                model.addAttribute("uploadError", "Couldn't save that photo - please try again.");
            }
        }
        model.addAttribute("photos", photoStorage.listLatest(50));
        return "admin";
    }
}
