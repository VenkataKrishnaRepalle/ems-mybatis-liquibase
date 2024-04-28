package com.learning.emsmybatisliquibase.dao;

import com.learning.emsmybatisliquibase.entity.Profile;
import com.learning.emsmybatisliquibase.entity.ProfileStatus;
import org.apache.ibatis.annotations.Param;

import java.util.UUID;

public interface ProfileDao {
    int insert(@Param("profile") Profile profile);

    Profile get(@Param("uuid") UUID uuid);

    int updateProfileStatus(@Param("employeeUuid") UUID employeeUuid, @Param("status") ProfileStatus status);

    int update(@Param("profile") Profile profile);
}
