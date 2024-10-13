package com.deutschmotors.moduledata.entity.user;

import com.deutschmotors.moduledata.audit.MutableBaseEntity;
import com.deutschmotors.moduledata.entity.user.enums.UserStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.JdbcType;
import org.hibernate.type.descriptor.jdbc.CharJdbcType;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@NamedEntityGraph(
        name = "UserEntity.userRoles",
        attributeNodes = @NamedAttributeNode("userRoles")
)
@Entity
@Table(name = "USER", indexes = {
        @Index(name = "IDX_USER_LOGIN_ID", columnList = "LOGIN_ID"),
        @Index(name = "IDX_USER_LOGIN_PASSWORD", columnList = "LOGIN_ID, PASSWORD"),
        @Index(name = "IDX_USER_LOGIN_STATUS", columnList = "LOGIN_ID, STATUS")
})
public class UserEntity extends MutableBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcType(CharJdbcType.class)
    @Column(name = "ID")
    private UUID id;

    @Column(name = "LOGIN_ID", length = 50, nullable = false, unique = true)
    @Comment("로그인 ID")
    private String loginId;

    @Column(name = "USERNAME", length = 50, nullable = false)
    @Comment("사용자 이름")
    private String username;

    @Column(name = "PASSWORD", length = 255, nullable = false)
    @Comment("비밀번호")
    private String password;

    @Column(name = "EMAIL", length = 100, nullable = false, unique = true)
    @Comment("이메일 주소")
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", length = 20, nullable = false)
    @Comment("승인 상태")
    private UserStatus status;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Comment("사용자 역할들")
    private Set<UserRoleEntity> userRoles = new HashSet<>();
}
