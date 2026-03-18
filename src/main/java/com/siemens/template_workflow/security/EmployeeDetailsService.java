package com.siemens.template_workflow.security;

import com.siemens.template_workflow.model.Employee;
import com.siemens.template_workflow.repository.EmployeeRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class EmployeeDetailsService implements UserDetailsService {
    private final EmployeeRepository repo;

    public EmployeeDetailsService(EmployeeRepository repo) { this.repo = repo; }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Employee e = repo.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        return new EmployeeUserDetails(e);
    }
}

