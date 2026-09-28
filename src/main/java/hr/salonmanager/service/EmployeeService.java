package hr.salonmanager.service;

import hr.salonmanager.model.Employee;
import hr.salonmanager.repository.EmployeeRepository;

import java.util.List;

/** Poslovne operacije nad djelatnicima. */
public class EmployeeService {
    private final EmployeeRepository repository;

    public EmployeeService(EmployeeRepository repository) {
        this.repository = repository;
    }

    public Employee save(Employee employee) {
        validate(employee);
        return employee.getId() == 0 ? repository.save(employee) : repository.update(employee);
    }

    public List<Employee> findAll() { return repository.findAll(); }

    public Employee findById(int id) {
        return repository.findById(id).orElseThrow(() -> new ValidationException("Djelatnik ne postoji."));
    }

    public void delete(int id) { repository.delete(id); }

    private void validate(Employee employee) {
        if (employee == null || employee.getName() == null || employee.getName().isBlank()) {
            throw new ValidationException("Ime djelatnika je obavezno.");
        }
    }
}
