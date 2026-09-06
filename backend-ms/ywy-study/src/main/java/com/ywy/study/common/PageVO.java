package com.ywy.study.common;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 分页 VO：把 MyBatis-Plus Page 规整为前端友好的 {records, total, current, size}。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageVO<T> {

    private List<T> records;
    private long total;
    private long current;
    private long size;

    public static <T> PageVO<T> from(Page<T> page) {
        return new PageVO<>(page.getRecords(), page.getTotal(), page.getCurrent(), page.getSize());
    }
}