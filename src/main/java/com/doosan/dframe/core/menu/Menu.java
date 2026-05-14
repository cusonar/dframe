package com.example.baseb.common.menu;

import com.example.baseb.common.config.audit.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Menu extends BaseEntity {

    @Id
    private String code;

    @Column(nullable = false)
    private String name;

    private String url;

    private String icon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_code")
    private Menu parent;

    @OneToMany(mappedBy = "parent")
    @Builder.Default
    private List<Menu> children = new ArrayList<>();

    @OneToMany(mappedBy = "menu", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<MenuRole> menuRoles = new HashSet<>();

    @Builder.Default
    private boolean isVisible = true;

    @Column(name = "sort_order")
    private Integer sortOrder;
}
