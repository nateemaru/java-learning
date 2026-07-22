package ru.nateemaru.polygon.repository;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.nateemaru.polygon.entity.Department;

@Repository
public interface DepartmentRepository extends CrudRepository<Department, Long> {
    @Modifying
    @Query("DELETE FROM departments WHERE id = :id")
    int deleteAndCountById(@Param("id") Long id);
}
