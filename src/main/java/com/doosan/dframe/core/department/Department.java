package com.example.baseb.common.department;

import com.example.baseb.common.config.audit.BaseEntity;
import com.example.baseb.common.employee.Employee;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Department extends BaseEntity {

    @Id
    private String code;

    private String name;

    private String englishName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_code")
    private Department parent;

    @OneToMany(mappedBy = "parent")
    @Builder.Default
    private List<Department> children = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private Employee manager;

    @Column(name = "sort_order")
    private Integer sortOrder;

    public void update(String name, String englishName, Department parent, Integer sortOrder) {
        if (name != null)
            this.name = name;
        if (englishName != null)
            this.englishName = englishName;
        this.parent = parent;
        if (sortOrder != null)
            this.sortOrder = sortOrder;
    }

}
