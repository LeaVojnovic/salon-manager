package hr.salonmanager.repository;

import hr.salonmanager.model.Service;

import java.util.List;
import java.util.Optional;

public interface ServiceRepository {
    Service save(Service service);
    Service update(Service service);
    Optional<Service> findById(int id);
    List<Service> findAll();
    void delete(int id);
}
