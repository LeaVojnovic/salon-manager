package hr.salonmanager.service;

import hr.salonmanager.model.Client;
import hr.salonmanager.repository.ClientRepository;

import java.util.List;

/** Poslovne operacije nad klijentima. */
public class ClientService {
    private final ClientRepository repository;

    public ClientService(ClientRepository repository) {
        this.repository = repository;
    }

    public Client save(Client client) {
        validate(client);
        return client.getId() == 0 ? repository.save(client) : repository.update(client);
    }

    public List<Client> findAll() { return repository.findAll(); }

    public Client findById(int id) {
        return repository.findById(id).orElseThrow(() -> new ValidationException("Klijent ne postoji."));
    }

    public void delete(int id) { repository.delete(id); }

    private void validate(Client client) {
        if (client == null || client.getName() == null || client.getName().isBlank()) {
            throw new ValidationException("Ime klijenta je obavezno.");
        }
    }
}
