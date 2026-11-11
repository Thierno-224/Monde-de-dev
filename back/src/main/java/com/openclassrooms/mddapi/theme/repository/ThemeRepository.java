package com.openclassrooms.mddapi.theme.repository;

import com.openclassrooms.mddapi.theme.entity.Theme;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ThemeRepository extends JpaRepository<Theme, Long> {
}
