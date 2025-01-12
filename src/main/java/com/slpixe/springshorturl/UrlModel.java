package com.slpixe.springshorturl;

import jakarta.persistence.*;
import lombok.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "url")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UrlModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Short URL cannot be blank")
    @Column(name = "short_url", nullable = false, unique = true)
    private String shortUrl;

    @NotBlank(message = "Full URL cannot be blank")
    @Column(name = "full_url", nullable = false, columnDefinition = "TEXT")
    private String fullUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserModel user;
}


