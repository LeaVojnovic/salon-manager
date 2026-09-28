package hr.salonmanager.model;

import java.time.LocalTime;

/** Osnovne postavke salona i njegovo radno vrijeme. */
public class SalonSettings {
    private int id;
    private String salonName;
    private String address;
    private String phone;
    private String email;
    private LocalTime openingTime;
    private LocalTime closingTime;

    public SalonSettings() {
    }

    public SalonSettings(int id, String salonName, String address, String phone, String email,
                         LocalTime openingTime, LocalTime closingTime) {
        this.id = id;
        this.salonName = salonName;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getSalonName() { return salonName; }
    public void setSalonName(String salonName) { this.salonName = salonName; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public LocalTime getOpeningTime() { return openingTime; }
    public void setOpeningTime(LocalTime openingTime) { this.openingTime = openingTime; }
    public LocalTime getClosingTime() { return closingTime; }
    public void setClosingTime(LocalTime closingTime) { this.closingTime = closingTime; }
}
