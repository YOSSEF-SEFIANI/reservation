package com.skypay.hotel.entity.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.LocalDateTime;


@SuperBuilder(toBuilder = true)
@RequiredArgsConstructor
@Getter
@Setter
@ToString(callSuper = true)
abstract class AbstractAuditable<I extends Serializable> extends AbstractPersistable<I> {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime createdDate;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String createdBy;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime lastModifiedDate;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String lastModifiedBy;
}
