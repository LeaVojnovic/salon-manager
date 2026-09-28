package hr.salonmanager.repository;

import hr.salonmanager.model.SalonSettings;

import java.util.Optional;

public interface SettingsRepository {
    /** Sprema ili ažurira postavke salona. */
    SalonSettings save(SalonSettings settings);
    /** Dohvaća postavke prema identifikatoru. */
    Optional<SalonSettings> findById(int id);
}
