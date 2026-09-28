package hr.salonmanager.repository;

import hr.salonmanager.model.SalonSettings;

import java.util.Optional;

public interface SettingsRepository {
    SalonSettings save(SalonSettings settings);
    Optional<SalonSettings> findById(int id);
}
