package com.learning.emsmybatisliquibase.bootstrap;

import com.learning.emsmybatisliquibase.dao.EmployeeDao;
import com.learning.emsmybatisliquibase.dao.ProfileDao;
import com.learning.emsmybatisliquibase.entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class Bootstrap implements CommandLineRunner {

    private final EmployeeDao employeeDao;

    private final ProfileDao profileDao;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public void run(String... args) throws Exception {
        if (employeeDao.count() < 3) {
            var employee1 = Employee.builder()
                    .uuid(UUID.randomUUID())
                    .firstName("venky")
                    .lastName("repalle")
                    .email("rvkrishna13052001@gmail.com")
                    .phoneNumber("9059712824")
                    .dateOfBirth(Date.valueOf("2001-05-13").toLocalDate())
                    .joiningDate(Date.valueOf("2022-07-04").toLocalDate())
                    .gender(Gender.MALE)
                    .isManager(Boolean.TRUE)
                    .password(passwordEncoder.encode("venky123"))
                    .createdTime(Instant.now())
                    .updatedTime(Instant.now())
                    .build();
            employeeDao.insert(employee1);

            var profile1 = Profile.builder()
                    .employeeUuid(employee1.getUuid())
                    .jobTitle(JobTitleType.SENIOR_PROJECT_MANAGER)
                    .profileStatus(ProfileStatus.ACTIVE)
                    .updatedTime(Instant.now())
                    .build();

            profileDao.insert(profile1);

            var employee2 = Employee.builder()
                    .uuid(UUID.randomUUID())
                    .firstName("chandu")
                    .lastName("raya")
                    .email("chandu.raya@gmail.com")
                    .phoneNumber("6305177093")
                    .dateOfBirth(Date.valueOf("2000-11-01").toLocalDate())
                    .joiningDate(Date.valueOf("2022-07-04").toLocalDate())
                    .gender(Gender.MALE)
                    .isManager(Boolean.FALSE)
                    .password(passwordEncoder.encode("chandu123"))
                    .managerUuid(employee1.getUuid())
                    .createdTime(Instant.now())
                    .updatedTime(Instant.now())
                    .build();
            employeeDao.insert(employee2);

            var profile2 = Profile.builder()
                    .employeeUuid(employee2.getUuid())
                    .jobTitle(JobTitleType.ENGINEER_TRAINEE)
                    .profileStatus(ProfileStatus.ACTIVE)
                    .updatedTime(Instant.now())
                    .build();

            profileDao.insert(profile2);

            var employee3 = Employee.builder()
                    .uuid(UUID.randomUUID())
                    .firstName("sujith")
                    .lastName("bikki")
                    .email("sujith.bikki@gmail.com")
                    .phoneNumber("1234567890")
                    .dateOfBirth(Date.valueOf("2000-05-14").toLocalDate())
                    .joiningDate(Date.valueOf("2022-07-04").toLocalDate())
                    .gender(Gender.MALE)
                    .isManager(Boolean.FALSE)
                    .password(passwordEncoder.encode("sujith123"))
                    .managerUuid(employee1.getUuid())
                    .createdTime(Instant.now())
                    .updatedTime(Instant.now())
                    .build();
            employeeDao.insert(employee3);

            var profile3 = Profile.builder()
                    .employeeUuid(employee3.getUuid())
                    .jobTitle(JobTitleType.SOFTWARE_ENGINEER)
                    .profileStatus(ProfileStatus.ACTIVE)
                    .updatedTime(Instant.now())
                    .build();
            profileDao.insert(profile3);

            var employee4 = Employee.builder()
                    .uuid(UUID.randomUUID())
                    .firstName("veeranji")
                    .lastName("katari")
                    .email("veeranji.katari@gmail.com")
                    .phoneNumber("9490903106")
                    .dateOfBirth(Date.valueOf("2000-11-18").toLocalDate())
                    .joiningDate(Date.valueOf("2022-07-04").toLocalDate())
                    .gender(Gender.MALE)
                    .password(passwordEncoder.encode("veeranji123"))
                    .isManager(Boolean.TRUE)
                    .managerUuid(employee1.getUuid())
                    .createdTime(Instant.now())
                    .updatedTime(Instant.now())
                    .build();
            employeeDao.insert(employee4);

            var profile4 = Profile.builder()
                    .employeeUuid(employee4.getUuid())
                    .jobTitle(JobTitleType.PROJECT_MANAGER)
                    .profileStatus(ProfileStatus.ACTIVE)
                    .updatedTime(Instant.now())
                    .build();
            profileDao.insert(profile4);

            var employee5 = Employee.builder()
                    .uuid(UUID.randomUUID())
                    .firstName("lakshman")
                    .lastName("jampani")
                    .email("lakshman.jampani@gmail.com")
                    .phoneNumber("6303537771")
                    .dateOfBirth(Date.valueOf("2001-02-28").toLocalDate())
                    .joiningDate(Date.valueOf("2022-07-04").toLocalDate())
                    .gender(Gender.MALE)
                    .isManager(Boolean.FALSE)
                    .managerUuid(employee4.getUuid())
                    .password(passwordEncoder.encode("lakshman123"))
                    .createdTime(Instant.now())
                    .updatedTime(Instant.now())
                    .build();
            employeeDao.insert(employee5);

            var profile5 = Profile.builder()
                    .employeeUuid(employee5.getUuid())
                    .jobTitle(JobTitleType.TECHNICAL_LEAD)
                    .profileStatus(ProfileStatus.ACTIVE)
                    .updatedTime(Instant.now())
                    .build();
            profileDao.insert(profile5);
        }
    }
}
