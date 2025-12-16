package com.skypay.hotel.entity;

import com.skypay.hotel.entity.domain.NumericAuditable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Room extends NumericAuditable<Integer> {

    private RoomType type;

    private Integer roomPricePerNight;

    /**
     * Convenience method to get room number (alias for getId)
     */
    public Integer getRoomNumber() {
        return this.getId();
    }
}