package com.doosan.dframe.core.grid;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GridLayoutRepository extends JpaRepository<GridLayout, Long> {

    Optional<GridLayout> findByGridId(String gridId);
}
