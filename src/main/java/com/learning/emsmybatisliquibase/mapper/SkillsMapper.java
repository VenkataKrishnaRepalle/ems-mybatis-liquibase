package com.learning.emsmybatisliquibase.mapper;

import com.learning.emsmybatisliquibase.dto.SkillsDto;
import com.learning.emsmybatisliquibase.entity.Skills;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SkillsMapper {
    Skills skillsDtoToSkills(SkillsDto skillsDto);
}
