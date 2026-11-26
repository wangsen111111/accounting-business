package com.greenfactory.gfecmp.common;

import java.util.List;

/**
 * 分页结果。
 */
public record PageResult<T>(List<T> records, long total, int page, int size, int pages) {

    public static <T> PageResult<T> of(List<T> all, int page, int size) {
        int safeSize = Math.max(1, size);
        int safePage = Math.max(1, page);
        int from = Math.min(all.size(), (safePage - 1) * safeSize);
        int to = Math.min(all.size(), from + safeSize);
        List<T> slice = all.subList(from, to);
        int pages = (int) Math.ceil(all.size() / (double) safeSize);
        return new PageResult<>(List.copyOf(slice), all.size(), safePage, safeSize, pages);
    }
}
