package hr.salonmanager.model;

/** Usluga s cijenom i trajanjem u minutama. */
public class Service {
    private int id;
    private String name;
    private double price;
    private int durationMinutes;

    public Service() {
    }

    public Service(String name, double price, int durationMinutes) {
        this(0, name, price, durationMinutes);
    }

    public Service(int id, String name, double price, int durationMinutes) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.durationMinutes = durationMinutes;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }

    @Override
    public String toString() {
        return name;
    }
}
