package com.postmanagementapi.repository;

import com.postmanagementapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long>, JpaSpecificationExecutor<User> {
//    Viết 1 câu query để hiểu cái dưới hoạt động thế nào
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
}
