package hr.salonmanager.repository;

import hr.salonmanager.model.Service;

import java.util.List;
import java.util.Optional;

public interface ServiceRepository {
    /** Sprema novu uslugu i dodjeljuje joj identifikator. */
    Service save(Service service);
    /** Ažurira postojeću uslugu. */
    Service update(Service service);
    /** Dohvaća uslugu prema identifikatoru. */
    Optional<Service> findById(int id);
    /** Dohvaća sve usluge sortirane po nazivu. */
    List<Service> findAll();
    /** Fizički briše uslugu ako nije povezana s terminom. */
    void delete(int id);
}
