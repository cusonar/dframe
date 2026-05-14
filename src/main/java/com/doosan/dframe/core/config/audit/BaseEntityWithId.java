package com.doosan.dframe.core.config.audit;

import com.github.f4b6a3.ulid.UlidCreator;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import lombok.Getter;

@Getter
@MappedSuperclass
public abstract class BaseEntityWithId extends BaseEntity {

    @Id
    @Column(updatable = false, nullable = false, length = 26)
    private String id;

    @PrePersist
    public void prePersistId() {
        if (this.id == null) {
            this.id = UlidCreator.getMonotonicUlid().toString();
        }
    }
}
