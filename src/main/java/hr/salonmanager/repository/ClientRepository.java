package hr.salonmanager.repository;

import hr.salonmanager.model.Client;

import java.util.List;
import java.util.Optional;

public interface ClientRepository {
    Client save(Client client);
    Client update(Client client);
    Optional<Client> findById(int id);
    List<Client> findAll();
    void delete(int id);
}
