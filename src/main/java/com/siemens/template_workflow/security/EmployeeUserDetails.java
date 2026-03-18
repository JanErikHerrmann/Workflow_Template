package com.siemens.template_workflow.security;

import com.siemens.template_workflow.model.Employee;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class EmployeeUserDetails implements UserDetails {
    private final Employee employee;

    public EmployeeUserDetails(Employee employee) { this.employee = employee; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + employee.getRole().toUpperCase()));
    }

    @Override
    public String getPassword() { return employee.getPasswordHash(); }

    @Override
    public String getUsername() { return employee.getUsername(); }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }

    public Employee getEmployee() { return employee; }
}

