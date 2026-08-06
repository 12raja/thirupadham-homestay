package com.thirupadham.web;

// Thymeleaf's th:field form binding needs a plain mutable class with
// getters/setters - not a record - so the framework can read values back
// out of the submitted form fields into this object.
public class BookingFormModel {

    private String guestName;
    private String phone;
    private String checkIn;
    private String checkOut;
    private String message;

    public String getGuestName() { return guestName; }
    public void setGuestName(String guestName) { this.guestName = guestName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getCheckIn() { return checkIn; }
    public void setCheckIn(String checkIn) { this.checkIn = checkIn; }

    public String getCheckOut() { return checkOut; }
    public void setCheckOut(String checkOut) { this.checkOut = checkOut; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
