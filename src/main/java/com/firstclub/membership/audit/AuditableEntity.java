package com.firstclub.membership.audit;

import jakarta.persistence.Version;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public abstract class AuditableEntity extends BaseEntity {
    @Version
    private Long version;
}