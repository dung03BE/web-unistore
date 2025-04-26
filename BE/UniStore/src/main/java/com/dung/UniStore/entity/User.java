package com.dung.UniStore.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.BatchSize;


import java.util.Date;
import java.util.List;
import java.util.Set;

@Entity
@Table(name="users")
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    @Column(name = "fullname", length = 100)
    String fullName;
    @Column(name = "phone_number", length = 50,unique = true,columnDefinition = "VARCHAR(255) COLLATE utf8mb4_unicode_ci")
    String phoneNumber;
    @Column(name = "address", length = 100)
    String address;
    @Column(name = "`password`", length = 100, nullable = false)
    String password;
    @Column(name = "is_active")
    boolean active;
    @Column(name="email")
    private String email;
    @Column(name = "date_of_birth")
    Date dateOfBirth;
    @Column(name = "facebook_account_id")
    int facebookAccountId;
    @Column(name = "google_account_id")
    int googleAccountId;
    @ManyToOne
    @JoinColumn(name = "`role_id`",referencedColumnName = "id")
    Role role;
//    Set<String> roles;
    @ToString.Exclude
    @OneToOne(mappedBy = "user",cascade = {CascadeType.PERSIST,CascadeType.MERGE},orphanRemoval = true)
    private Cart cart;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Counpons> counpons;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orders;
}
