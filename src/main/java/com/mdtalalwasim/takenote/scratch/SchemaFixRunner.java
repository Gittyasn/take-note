package com.mdtalalwasim.takenote.scratch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class SchemaFixRunner implements CommandLineRunner {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("--- SCHEMA FIX START ---");
        try {
            // Ensure password column is long enough for BCrypt
            jdbcTemplate.execute("ALTER TABLE user MODIFY COLUMN password VARCHAR(255)");
            // Ensure email is unique
            try {
                jdbcTemplate.execute("ALTER TABLE user ADD UNIQUE (email)");
            } catch (Exception e) {
                System.out.println("Email unique constraint might already exist or duplicates found.");
            }
            // Drop outdated unique index on tag name (per-user tags can overlap in name)
            try {
                jdbcTemplate.execute("ALTER TABLE tag DROP INDEX UK_1wdpsed5kna2y38hnbgrnhi5b");
            } catch (Exception e) {}
            try {
                jdbcTemplate.execute("ALTER TABLE tag DROP INDEX name");
            } catch (Exception e) {}
            System.out.println("--- SCHEMA FIX SUCCESS ---");
        } catch (Exception e) {
            System.out.println("--- SCHEMA FIX FAILED: " + e.getMessage());
        }
    }
}
