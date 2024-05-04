package com.learning.emsmybatisliquibase.mapper;

import com.learning.emsmybatisliquibase.dto.SkillsDto;
import com.learning.emsmybatisliquibase.entity.Skills;
import org.mapstruct.Mapper;

@Mapper
public interface SkillsMapper {
    Skills skillsDtoToSkills(SkillsDto skillsDto);
}
