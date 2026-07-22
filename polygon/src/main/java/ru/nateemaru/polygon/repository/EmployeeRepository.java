package ru.nateemaru.polygon.repository;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.nateemaru.polygon.dto.projection.EmployeeProjection;
import ru.nateemaru.polygon.entity.Employee;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends CrudRepository<Employee, Long> {

    @Query("""
            SELECT concat_ws(' ', e.first_name, e.last_name) AS full_name,
                   e.position,
                   d.name AS department_name
            FROM employees e
            JOIN departments d ON d.id = e.department_id
            WHERE e.id = :id
            """)
    Optional<EmployeeProjection> findProjectionById(@Param("id") Long id);

    @Modifying
    @Query("DELETE FROM employees WHERE id = :id")
    int deleteAndCountById(@Param("id") Long id);
}
