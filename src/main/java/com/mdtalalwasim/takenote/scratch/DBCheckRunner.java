package com.mdtalalwasim.takenote.scratch;

import com.mdtalalwasim.takenote.entity.User;
import com.mdtalalwasim.takenote.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DBCheckRunner implements CommandLineRunner {
    @Autowired
    private UserService userService;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("--- DB CHECK START ---");
        try {
            User user = new User();
            user.setName("Test Diagnostics");
            user.setEmail("diag_" + System.currentTimeMillis() + "@test.com");
            user.setPassword("password123");
            user.setPhone("1234567890");
            user.setGender("Male");
            user.setAddress("Secret Location");
            
            User saved = userService.saveUser(user);
            System.out.println("Saved User ID: " + saved.getId());
            System.out.println("--- DB CHECK SUCCESS ---");
        } catch (Exception e) {
            System.out.println("--- DB CHECK FAILED ---");
            e.printStackTrace();
        }
    }
}
