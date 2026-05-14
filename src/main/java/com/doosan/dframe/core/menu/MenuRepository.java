package com.example.baseb.common.menu;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuRepository extends JpaRepository<Menu, String> {
    List<Menu> findByParentIsNullOrderBySortOrderAsc();

    // Fetch all visible menus
    List<Menu> findByIsVisibleTrueOrderBySortOrderAsc();
}
