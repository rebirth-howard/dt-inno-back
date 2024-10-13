package com.deutschmotors.moduledata.entity.user;

import com.deutschmotors.moduledata.audit.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "ROLE_PRIVILEGE", indexes = {
        @Index(name = "IDX_ROLE_PRIVILEGE_ROLE_ID", columnList = "ROLE_ID"),
        @Index(name = "IDX_ROLE_PRIVILEGE_PRIVILEGE_ID", columnList = "PRIVILEGE_ID")
})
@Entity
public class RolePrivilegeEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ROLE_ID", nullable = false)
    private RoleEntity role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRIVILEGE_ID", nullable = false)
    private PrivilegeEntity privilege;

}
