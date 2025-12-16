package com.skypay.hotel.entity.domain;

import jakarta.annotation.Nullable;

interface Persistable<ID> {
    @Nullable
    ID getId();
}