package hr.salonmanager.service;

import hr.salonmanager.model.Service;
import hr.salonmanager.repository.ServiceRepository;

import java.util.List;

/** Poslovne operacije nad uslugama i cjenikom. */
public class ServiceCatalogService {
    private final ServiceRepository repository;

    public ServiceCatalogService(ServiceRepository repository) {
        this.repository = repository;
    }

    public Service save(Service service) {
        validate(service);
        return service.getId() == 0 ? repository.save(service) : repository.update(service);
    }

    public List<Service> findAll() { return repository.findAll(); }

    public Service findById(int id) {
        return repository.findById(id).orElseThrow(() -> new ValidationException("Usluga ne postoji."));
    }

    public void delete(int id) { repository.delete(id); }

    private void validate(Service service) {
        if (service == null || service.getName() == null || service.getName().isBlank()) {
            throw new ValidationException("Naziv usluge je obavezan.");
        }
        if (service.getPrice() < 0) {
            throw new ValidationException("Cijena usluge ne može biti negativna.");
        }
        if (service.getDurationMinutes() <= 0) {
            throw new ValidationException("Trajanje usluge mora biti veće od nule.");
        }
    }
}
