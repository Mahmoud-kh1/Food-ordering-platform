package com.foodapp.foodhub.entity;
import com.foodapp.foodhub.enums.RestaurantApplicationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Optional;

@Entity
@Table(name = "restaurant_application")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantApplication
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User owner;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by")
    private User reviewedBy;
    @Column( name = "restaurant_name", nullable = false)
    private String restaurantName;
    @Column( name = "phone", nullable = false)
    private String phone;
    @Column( name = "city", nullable = false)
//    private String city;
//    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private RestaurantApplicationStatus status;

    @Column(name = "license_document_url", nullable = false)
    private String licenseDocumentUrl;

    @Column(name = "rejection_reason", nullable = true)
    private String rejectionReason;
    @Column(name = "social_media_url", nullable = false)
    private String socialMediaUrl;

    @Column(name = "social_media_url_opt", nullable = true)
    private String socialMediaUrlOpt;//Optional
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
