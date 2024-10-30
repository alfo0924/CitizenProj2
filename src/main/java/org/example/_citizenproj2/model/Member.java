package org.example._citizenproj2.model;
import java.util.List;
import java.util.ArrayList;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "members")
@Data  // 已有，但確保存在
@Getter // 明確添加
@Setter // 明確添加
@NoArgsConstructor
@AllArgsConstructor
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;

    private String firstName;
    private String lastName;
    private Date birthday;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(columnDefinition = "TEXT")
    private String address;

    private boolean isVerified = false;
    private boolean isActive = true;

    @CreationTimestamp
    private LocalDateTime registerDate;

    private LocalDateTime lastLoginTime;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // 關聯關係
    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL)
    private CitizenCard citizenCard;

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL)
    private Wallet wallet;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<VirtualCard> virtualCards;

    // 枚舉定義
    public enum Role {
        USER, ADMIN, STAFF
    }

    public enum Gender {
        M, F, OTHER
    }
}