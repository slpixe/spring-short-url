package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "URL")
@Data // Combines @Getter, @Setter, @ToString, @EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class UrlModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "short_url", nullable = false, length = 255)
    private String shortUrl;

    @Column(name = "full_url", nullable = false, columnDefinition = "TEXT")
    private String fullUrl;
}
