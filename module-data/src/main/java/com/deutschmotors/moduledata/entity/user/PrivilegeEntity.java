package com.deutschmotors.moduledata.entity.user;

import com.deutschmotors.moduledata.audit.MutableBaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "PRIVILEGE", indexes = {
        @Index(name = "IDX_PRIVILEGE_NAME", columnList = "PRIVILEGE_NAME")
})
@Entity
public class PrivilegeEntity extends MutableBaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "PRIVILEGE_NAME", length = 100, nullable = false, unique = true)
    private String privilegeName;

    @OneToMany(mappedBy = "privilege", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RolePrivilegeEntity> rolePrivileges = new HashSet<>();
}
