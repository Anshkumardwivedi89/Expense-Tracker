package com.example.Expense.Tracker.config;

import com.example.Expense.Tracker.entity.Role;
import com.example.Expense.Tracker.repo.RoleRepo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSeeder {

    @Bean
    public CommandLineRunner seedRoles(RoleRepo roleRepo) {
        return args -> {
            roleRepo.findByName("ROLE_USER").orElseGet(() -> {
                Role r = new Role();
                r.setName("ROLE_USER");
                return roleRepo.save(r);
            });

            roleRepo.findByName("ROLE_ADMIN").orElseGet(() -> {
                Role r = new Role();
                r.setName("ROLE_ADMIN");
                return roleRepo.save(r);
            });
        };
    }
}
