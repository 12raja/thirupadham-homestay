package com.thirupadham.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
public class WhatsAppNotifier {

    private static final Logger log = LoggerFactory.getLogger(WhatsAppNotifier.class);

    private final RestTemplate restTemplate;

    @Value("${twilio.account-sid:}")
    private String accountSid;

    @Value("${twilio.auth-token:}")
    private String authToken;

    // Twilio's sandbox number - stays the same for every sandbox account.
    @Value("${twilio.whatsapp-from:whatsapp:+14155238886}")
    private String fromNumber;

    // The owner's own phone - where enquiry alerts actually land.
    @Value("${owner.whatsapp-number:}")
    private String ownerNumber;

    public WhatsAppNotifier(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void send(String message) {
        if (accountSid.isBlank() || authToken.isBlank() || ownerNumber.isBlank()) {
            log.info("Twilio not configured yet - skipping WhatsApp send (console log above still has the details).");
            return;
        }

        String url = "https://api.twilio.com/2010-04-01/Accounts/" + accountSid + "/Messages.json";

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(accountSid, authToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // Twilio's API expects a standard form-encoded POST body, not JSON.
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("From", fromNumber);
        body.add("To", "whatsapp:" + ownerNumber);
        body.add("Body", message);

        try {
            restTemplate.postForObject(url, new HttpEntity<>(body, headers), String.class);
            log.info("WhatsApp alert sent to owner via Twilio.");
        } catch (Exception e) {
            // A failed WhatsApp send should never break the booking flow -
            // the enquiry is already saved; this is just an extra alert.
            log.warn("Could not send WhatsApp alert via Twilio: {}", e.getMessage());
        }
    }
}
