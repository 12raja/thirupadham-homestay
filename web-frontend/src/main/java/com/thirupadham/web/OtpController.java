package com.thirupadham.web;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/api/otp")
public class OtpController {

    private final OtpService otpService;
    private final MessageSource messageSource;

    public OtpController(OtpService otpService, MessageSource messageSource) {
        this.otpService = otpService;
        this.messageSource = messageSource;
    }

    @PostMapping("/send")
    public Map<String, Object> send(@RequestBody Map<String, String> req) {
        Locale locale = LocaleContextHolder.getLocale();
        String phone = req.get("phone");
        if (phone == null || !phone.matches("[0-9]{10}")) {
            return Map.of("success", false, "message", message("otp.phone.invalid", locale));
        }
        // Assumes Indian numbers (+91) - matches the 10-digit-only phone
        // field already in place for this business.
        boolean sent = otpService.sendCode("+91" + phone);
        return Map.of("success", sent, "message", message(sent ? "otp.send.success" : "otp.send.failure", locale));
    }

    @PostMapping("/check")
    public Map<String, Object> check(@RequestBody Map<String, String> req) {
        Locale locale = LocaleContextHolder.getLocale();
        String phone = req.get("phone");
        String code = req.get("code");
        if (phone == null || code == null || phone.isBlank() || code.isBlank()) {
            return Map.of("success", false, "message", message("otp.check.missing", locale));
        }
        boolean verified = otpService.checkCode("+91" + phone, code);
        return Map.of("success", verified, "message", message(verified ? "otp.check.success" : "otp.check.failure", locale));
    }

    private String message(String key, Locale locale) {
        return messageSource.getMessage(key, null, locale);
    }
}
