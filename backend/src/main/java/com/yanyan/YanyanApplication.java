package com.yanyan;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 研王爷-考研伴学系统 后端启动入口
 * 一期：学习/错题Anki/计划模板/RAG答疑/背诵/数据大盘
 * 二期预留：社区/搭子小组/择校（包结构已占位）
 */
@EnableAsync
@EnableScheduling
@SpringBootApplication
@MapperScan("com.yanyan.**.mapper")
public class YanyanApplication {

    public static void main(String[] args) {
        SpringApplication.run(YanyanApplication.class, args);
    }
}