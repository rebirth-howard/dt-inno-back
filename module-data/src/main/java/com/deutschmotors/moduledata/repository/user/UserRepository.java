package com.deutschmotors.moduledata.repository.user;

import com.deutschmotors.moduledata.entity.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    Optional<UserEntity> findFirstByLoginId(String loginId);
}
