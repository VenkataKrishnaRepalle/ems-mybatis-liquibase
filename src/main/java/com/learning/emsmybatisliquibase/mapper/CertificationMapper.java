package com.learning.emsmybatisliquibase.mapper;

import com.learning.emsmybatisliquibase.dto.CertificationRequestDto;
import com.learning.emsmybatisliquibase.dto.CertificationResponseDto;
import com.learning.emsmybatisliquibase.entity.Certification;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CertificationMapper {

    Certification addCertificationDtoToCertification(CertificationRequestDto addCertificationDto);

    CertificationResponseDto certificationToCertificationResponseDto(Certification certification);
}
