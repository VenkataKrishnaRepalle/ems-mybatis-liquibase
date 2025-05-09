package com.learning.emsmybatisliquibase;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.learning.emsmybatisliquibase.dao")
@EnableScheduling
@EnableAsync
public class EmsMybatisLiquibaseApplication {

    public static void main(String[] args) {
        SpringApplication.run(EmsMybatisLiquibaseApplication.class, args);
    }

}
