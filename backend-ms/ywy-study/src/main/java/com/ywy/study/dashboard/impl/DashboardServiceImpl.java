package com.ywy.study.dashboard.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ywy.study.dashboard.DashboardService;
import com.ywy.study.recite.ReciteCard;
import com.ywy.study.recite.ReciteCardMapper;
import com.ywy.study.session.StudySession;
import com.ywy.study.session.StudySessionMapper;
import com.ywy.study.wrongbook.WrongQuestion;
import com.ywy.study.wrongbook.WrongQuestionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据大盘服务实现：聚合学习时长/签到/错题/背诵/遗忘曲线。直接读库 + Redis。
 */
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final StudySessionMapper sessionMapper;
    private final WrongQuestionMapper wrongMapper;
    private final ReciteCardMapper reciteMapper;
    private final StringRedisTemplate redis;

    @Override
    public Map<String, Object> summary(Long userId, String period) {
        LocalDate start = switch (period == null ? "week" : period) {
            case "day" -> LocalDate.now();
            case "month" -> LocalDate.now().minusDays(29);
            default -> LocalDate.now().minusDays(6);
        };

        List<StudySession> sessions = sessionMapper.selectList(
                new LambdaQueryWrapper<StudySession>()
                        .eq(StudySession::getUserId, userId)
                        .ge(StudySession::getStartTime, start.atStartOfDay()));
        int studySeconds = sessions.stream()
                .mapToInt(s -> s.getDurationSeconds() == null ? 0 : s.getDurationSeconds()).sum();

        Map<String, Integer> subjectShare = new LinkedHashMap<>();
        sessions.forEach(s -> subjectShare.merge(
                s.getSubject() == null ? "other" : s.getSubject(),
                s.getDurationSeconds() == null ? 0 : s.getDurationSeconds(), Integer::sum));

        int wrongTotal = wrongMapper.selectCount(new LambdaQueryWrapper<WrongQuestion>()
                .eq(WrongQuestion::getUserId, userId)).intValue();
        int reciteTotal = reciteMapper.selectCount(new LambdaQueryWrapper<ReciteCard>()
                .eq(ReciteCard::getUserId, userId)).intValue();

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("userId", userId);
        map.put("period", period == null ? "week" : period);
        map.put("studySeconds", studySeconds);
        map.put("wrongTotal", wrongTotal);
        map.put("reciteTotal", reciteTotal);
        map.put("subjectShare", subjectShare);
        map.put("heatmap", lastWeekHeatmap(userId));
        return map;
    }

    private Map<String, Object> lastWeekHeatmap(Long userId) {
        Map<String, Object> out = new LinkedHashMap<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate day = LocalDate.now().minusDays(i);
            out.put(day.toString(), statsSeconds(userId, day));
        }
        return out;
    }

    private Long statsSeconds(Long userId, LocalDate day) {
        Object v = redis.opsForValue().get("stats:seconds:" + day + ":" + userId);
        return v == null ? 0L : Long.parseLong(v.toString());
    }
}