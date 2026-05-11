package com.doosan.dframe.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "employee")
@Getter @Setter
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    private String englishName;

    private String jobTitle;

    private boolean enabled = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "working_department_id")
    private Department workingDepartment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dispatched_department_id")
    private Department dispatchedDepartment;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "employee_role",
        joinColumns = @JoinColumn(name = "employee_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private java.util.Set<Role> roles = new java.util.HashSet<>();

    private LocalDateTime createdAt = LocalDateTime.now();
}
