package com.vivo.peter.serializer;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SerializerCode {
    JSON(1),
    KRYO(0);

    private final int code;
}
