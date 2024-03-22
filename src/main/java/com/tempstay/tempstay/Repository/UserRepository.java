package com.tempstay.tempstay.Repository;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tempstay.tempstay.Models.UserModel;

public interface UserRepository extends JpaRepository<UserModel,UUID> {
    UserModel findByEmail(String email);
}

