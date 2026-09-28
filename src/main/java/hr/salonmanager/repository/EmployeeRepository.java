package hr.salonmanager.repository;

import hr.salonmanager.model.Employee;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository {
    /** Sprema novog djelatnika i dodjeljuje mu identifikator. */
    Employee save(Employee employee);
    /** Ažurira postojeći zapis djelatnika. */
    Employee update(Employee employee);
    /** Dohvaća djelatnika prema identifikatoru. */
    Optional<Employee> findById(int id);
    /** Dohvaća sve djelatnike sortirane po imenu. */
    List<Employee> findAll();
    /** Fizički briše djelatnika ako nije povezan s terminom. */
    void delete(int id);
}
