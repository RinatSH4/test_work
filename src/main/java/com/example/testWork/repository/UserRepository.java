package com.example.testWork.repository;

import com.example.testWork.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByName(String username);
    User findByEmail(String email);
    User findByPhone(String phone);

    User findUserByName(String name);

    @Query("SELECT u FROM User u WHERE u.firstName LIKE %:searchTerm% OR u.lastName LIKE %:searchTerm% OR u.fathersName LIKE %:searchTerm% ORDER BY u.lastName, u.firstName, u.fathersName")
    List<User> searchUsers(@Param("searchTerm") String searchTerm);
}
