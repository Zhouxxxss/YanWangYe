package com.yanyan.module.school.placeholder;

/**
 * 二期占位包：择校库（院校 / 专业 / 复试线 / 目标绑定）。
 *
 * <p>一期预埋：
 * <ul>
 *   <li>表 school / major / admission_line / user_target 空表（user_target 为二期统计衔接表）</li>
 *   <li>user 表预留 target_school_id 字段（可空）</li>
 *   <li>权限点 school:view</li>
 *   <li>研招网低频同步（半月一刷 + 旺季加密）待二期接入采集器</li>
 * </ul>
 */
public final class SchoolPlaceholder {
    private SchoolPlaceholder() {}
}