package com.openclassrooms.mddapi.subscription.entity;

import com.openclassrooms.mddapi.theme.entity.Theme;
import com.openclassrooms.mddapi.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
        name = "subscriptions",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"user_id", "theme_id"}
        )
)
@Getter
@Setter
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "theme_id", nullable = false)
    private Theme theme;
}
