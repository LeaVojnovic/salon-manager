package hr.salonmanager.repository;

import hr.salonmanager.model.Client;

import java.util.List;
import java.util.Optional;

public interface ClientRepository {
    /** Sprema novog klijenta i dodjeljuje mu identifikator. */
    Client save(Client client);
    /** Ažurira postojeći zapis klijenta. */
    Client update(Client client);
    /** Dohvaća klijenta prema identifikatoru. */
    Optional<Client> findById(int id);
    /** Dohvaća sve klijente sortirane po imenu. */
    List<Client> findAll();
    /** Fizički briše klijenta ako nije povezan s terminom. */
    void delete(int id);
}
