package com.construction.cms.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseCleanup implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        try {
            // Attempt to drop the unique constraint on username
            // The constraint name was obtained from the error logs
            String sql = "ALTER TABLE users DROP INDEX UK_r43af9ap4edm43mmtq01oddj6";
            jdbcTemplate.execute(sql);
            System.out.println("SUCCESSFULLY DROPPED UNIQUE CONSTRAINT ON USERS TABLE");
        } catch (Exception e) {
            // It might already be dropped, or name is different (unlikely given logs), or table doesn't exist yet
            System.out.println("Could not drop index (might not exist): " + e.getMessage());
        }
        try {
            jdbcTemplate.execute("ALTER TABLE employees MODIFY COLUMN status VARCHAR(20)");
        } catch (Exception e) {
            // Column might already be wide enough or table not created yet
        }
    }
}
