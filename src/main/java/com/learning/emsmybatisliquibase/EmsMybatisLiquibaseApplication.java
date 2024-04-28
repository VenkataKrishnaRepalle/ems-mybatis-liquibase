package com.learning.emsmybatisliquibase;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.learning.emsmybatisliquibase.dao")
public class EmsMybatisLiquibaseApplication {

    public static void main(String[] args) {
        SpringApplication.run(EmsMybatisLiquibaseApplication.class, args);
    }

}
