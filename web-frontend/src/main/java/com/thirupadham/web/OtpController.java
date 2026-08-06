package com.thirupadham.web;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/otp")
public class OtpController {

    private final OtpService otpService;

    public OtpController(OtpService otpService) {
        this.otpService = otpService;
    }

    @PostMapping("/send")
    public Map<String, Object> send(@RequestBody Map<String, String> req) {
        String phone = req.get("phone");
        if (phone == null || !phone.matches("[0-9]{10}")) {
            return Map.of("success", false, "message", "Enter a valid 10-digit phone number first.");
        }
        // Assumes Indian numbers (+91) - matches the 10-digit-only phone
        // field already in place for this business.
        boolean sent = otpService.sendCode("+91" + phone);
        return Map.of("success", sent, "message", sent ? "Code sent - check your SMS." : "Could not send code - try again.");
    }

    @PostMapping("/check")
    public Map<String, Object> check(@RequestBody Map<String, String> req) {
        String phone = req.get("phone");
        String code = req.get("code");
        if (phone == null || code == null || phone.isBlank() || code.isBlank()) {
            return Map.of("success", false, "message", "Missing phone or code.");
        }
        boolean verified = otpService.checkCode("+91" + phone, code);
        return Map.of("success", verified, "message", verified ? "Phone verified!" : "Incorrect code - try again.");
    }
}
