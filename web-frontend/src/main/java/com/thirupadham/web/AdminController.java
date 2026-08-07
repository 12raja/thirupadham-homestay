package com.thirupadham.web;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
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
    private final MessageSource messageSource;

    public AdminController(PhotoStorage photoStorage, MessageSource messageSource) {
        this.photoStorage = photoStorage;
        this.messageSource = messageSource;
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
                model.addAttribute("uploadError", messageSource.getMessage("admin.upload.error", null, LocaleContextHolder.getLocale()));
            }
        }
        model.addAttribute("photos", photoStorage.listLatest(50));
        return "admin";
    }
}
