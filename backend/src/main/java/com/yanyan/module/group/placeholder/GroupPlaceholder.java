package com.yanyan.module.group.placeholder;

/**
 * 二期占位包：考研搭子小组（组队打卡 / 共享文档）。
 *
 * <p>一期预埋：权限点 group:join / group:create；表 group / group_member / group_doc 空表；
 * 领域事件（StudyCompletedEvent、CheckinEvent）一期已发布，二期可直接挂排行榜/组队刷新监听器。</p>
 */
public final class GroupPlaceholder {
    private GroupPlaceholder() {}
}