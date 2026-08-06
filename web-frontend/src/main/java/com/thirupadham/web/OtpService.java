package com.thirupadham.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class OtpService {

    private static final Logger log = LoggerFactory.getLogger(OtpService.class);

    private final RestTemplate restTemplate;

    @Value("${twilio.account-sid:}")
    private String accountSid;

    @Value("${twilio.auth-token:}")
    private String authToken;

    @Value("${twilio.verify-service-sid:}")
    private String verifyServiceSid;

    public OtpService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private boolean configured() {
        return !accountSid.isBlank() && !authToken.isBlank() && !verifyServiceSid.isBlank();
    }

    private HttpHeaders authHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(accountSid, authToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        return headers;
    }

    public boolean sendCode(String phoneE164) {
        if (!configured()) {
            log.warn("Twilio Verify not configured - cannot send OTP.");
            return false;
        }
        String url = "https://verify.twilio.com/v2/Services/" + verifyServiceSid + "/Verifications";

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("To", phoneE164);
        // SMS, not WhatsApp - guests haven't joined any WhatsApp sandbox,
        // so SMS is the only channel that reaches an arbitrary phone number.
        body.add("Channel", "sms");

        try {
            restTemplate.postForObject(url, new HttpEntity<>(body, authHeaders()), Map.class);
            return true;
        } catch (Exception e) {
            log.warn("Could not send OTP via Twilio Verify: {}", e.getMessage());
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    public boolean checkCode(String phoneE164, String code) {
        if (!configured()) {
            return false;
        }
        String url = "https://verify.twilio.com/v2/Services/" + verifyServiceSid + "/VerificationCheck";

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("To", phoneE164);
        body.add("Code", code);

        try {
            Map<String, Object> response = restTemplate.postForObject(url, new HttpEntity<>(body, authHeaders()), Map.class);
            return response != null && "approved".equals(response.get("status"));
        } catch (Exception e) {
            log.warn("Could not check OTP via Twilio Verify: {}", e.getMessage());
            return false;
        }
    }
}
