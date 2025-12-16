package com.skypay.hotel.entity.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder(toBuilder = true)
@RequiredArgsConstructor
@Getter
@Setter
@ToString
public class NumericAuditable<I extends Number> extends AbstractAuditable<I> {

    private I id;

}
