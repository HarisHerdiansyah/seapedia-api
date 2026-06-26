package com.github.harisherdiansyah.seapediaapi.core.utils;

import org.springframework.data.domain.Slice;

import java.util.List;

public record SliceResponseBuilder<T>(
        List<T> content,
        int number,
        boolean hasNext
) {
    public static <T> SliceResponseBuilder of(Slice<T> slice) {
        return new SliceResponseBuilder<>(
                slice.getContent(),
                slice.getNumber(),
                slice.hasNext()
        );
    }
}
