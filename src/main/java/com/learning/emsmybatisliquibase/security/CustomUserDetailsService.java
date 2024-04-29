package com.learning.emsmybatisliquibase.security;

import com.learning.emsmybatisliquibase.dao.EmployeeDao;
import com.learning.emsmybatisliquibase.dao.EmployeeRoleDao;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final EmployeeDao employeeDao;

    private final EmployeeRoleDao employeeRoleDao;

    private static final String ROLE_PREFIX = "ROLE_";

    private static final String MANAGER = "MANAGER";


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        var employee = employeeDao.getByEmail(email);
        if (employee == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }

        var employeeRoles = employeeRoleDao.getByEmployeeUuid(employee.getUuid());

        Set<GrantedAuthority> authorities = employeeRoles.stream().map(role ->
                new SimpleGrantedAuthority(ROLE_PREFIX + role.getRole().toString())).collect(Collectors.toSet());
        if (employee.getIsManager().equals(Boolean.TRUE)) {
            authorities.add(new SimpleGrantedAuthority(ROLE_PREFIX + MANAGER));
        }
        System.out.println(authorities);

        return new org.springframework.security.core.userdetails.User(
                email,
                employee.getPassword(),
                authorities
        );
    }

}
