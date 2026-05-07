package com.doosan.dframe.repository;

import com.doosan.dframe.domain.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MenuRepository extends JpaRepository<Menu, Long> {
    List<Menu> findAllByOrderBySortOrderAsc();
}
