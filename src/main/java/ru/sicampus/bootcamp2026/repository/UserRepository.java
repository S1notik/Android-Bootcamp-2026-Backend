package ru.sicampus.bootcamp2026.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sicampus.bootcamp2026.model.entity.Users;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
}
