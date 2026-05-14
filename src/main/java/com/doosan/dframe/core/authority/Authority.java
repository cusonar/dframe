package com.doosan.dframe.admin.authority;

import com.example.baseb.common.config.audit.BaseEntity;
import com.example.baseb.common.role.RoleAuthority;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Authority extends BaseEntity {

    @Id
    private String code;

    private String description;

    @OneToMany(mappedBy = "authority", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<RoleAuthority> roleAuthorities = new ArrayList<>();
}
