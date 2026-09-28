package hr.salonmanager.service;

import hr.salonmanager.model.SalonSettings;
import hr.salonmanager.repository.SettingsRepository;

import java.time.LocalTime;

/** Poslovne operacije nad postavkama salona. */
public class SalonSettingsService {
    private final SettingsRepository repository;

    public SalonSettingsService(SettingsRepository repository) {
        this.repository = repository;
    }

    public SalonSettings getSettings() {
        return repository.findById(1).orElseGet(() -> new SalonSettings(
                1, "SalonManager", "", "", "", LocalTime.of(8, 0), LocalTime.of(20, 0)));
    }

    public SalonSettings save(SalonSettings settings) {
        if (settings == null || settings.getSalonName() == null || settings.getSalonName().isBlank()) {
            throw new ValidationException("Naziv salona je obavezan.");
        }
        if (settings.getOpeningTime() == null || settings.getClosingTime() == null
                || !settings.getOpeningTime().isBefore(settings.getClosingTime())) {
            throw new ValidationException("Radno vrijeme mora imati ispravan početak i završetak.");
        }
        settings.setId(1);
        return repository.save(settings);
    }
}
