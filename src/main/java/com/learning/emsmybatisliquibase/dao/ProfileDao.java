package com.learning.emsmybatisliquibase.dao;

import com.learning.emsmybatisliquibase.entity.Profile;
import org.apache.ibatis.annotations.Param;

import java.util.UUID;

public interface ProfileDao {
    int insert(@Param("profile") Profile profile);

    Profile get(@Param("uuid") UUID uuid);

    int update(@Param("profile") Profile profile);
}
