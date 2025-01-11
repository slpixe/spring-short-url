package com.slpixe.springshorturl;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Data // Generates getters, setters, toString, etc.
@NoArgsConstructor
@AllArgsConstructor
public class UserModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 20)
    private String role; // e.g., "USER" or "ADMIN"
}
