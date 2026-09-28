package hr.salonmanager.repository;

/** Konfiguracija JDBC veze; vrijednosti se mogu zadati system propertyjem ili environment varijablom. */
public record DatabaseConfig(String url, String username, String password) {
    public static DatabaseConfig fromEnvironment() {
        String url = firstNonBlank(System.getProperty("salon.db.url"), System.getenv("SALON_DB_URL"),
                "jdbc:h2:file:./data/salonmanager;AUTO_SERVER=TRUE");
        String username = firstNonBlank(System.getProperty("salon.db.username"),
                System.getenv("SALON_DB_USERNAME"), "sa");
        String password = firstNonBlank(System.getProperty("salon.db.password"),
                System.getenv("SALON_DB_PASSWORD"), "");
        return new DatabaseConfig(url, username, password);
    }

    private static String firstNonBlank(String first, String second, String fallback) {
        if (first != null && !first.isBlank()) return first;
        if (second != null && !second.isBlank()) return second;
        return fallback;
    }
}
