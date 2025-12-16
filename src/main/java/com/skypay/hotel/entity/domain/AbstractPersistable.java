package com.skypay.hotel.entity.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.Objects;

/**
 * Base class for entities that provides default implementations for
 * ID handling and persistence state tracking.
 *
 * @param <I> the type of the identifier
 */
@SuperBuilder(toBuilder = true)
@RequiredArgsConstructor
@ToString
abstract class AbstractPersistable<I extends Serializable> implements Persistable<I> {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Override
    public abstract I getId();

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractPersistable<?> that)) return false;
        return getId() != null && Objects.equals(getId(), that.getId());
    }
}
