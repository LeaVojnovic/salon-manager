package hr.salonmanager.repository;

import hr.salonmanager.model.Employee;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository {
    Employee save(Employee employee);
    Employee update(Employee employee);
    Optional<Employee> findById(int id);
    List<Employee> findAll();
    void delete(int id);
}
