package com.example.Rams.config;

import com.example.Rams.models.Role;
import com.example.Rams.models.User;
import com.example.Rams.repositories.RoleRepository;
import com.example.Rams.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserService userService;

    @Override
    public void run(String... args) throws Exception {
        logger.info("🔄 Запуск инициализации данных...");

        try {
            initializeRoles();
            initializeAdminUser();
            logger.info("✅ Инициализация данных завершена успешно!");

        } catch (Exception e) {
            logger.error("❌ Ошибка инициализации данных: {}", e.getMessage());
            logger.debug("Детали:", e);
            // Не прерываем запуск приложения
        }
    }

    private void initializeRoles() {
        List<String> roles = Arrays.asList("ADMIN", "MANAGER", "USER");

        for (String roleName : roles) {
            try {
                if (!roleRepository.existsByName(roleName)) {
                    Role role = new Role(roleName, getRoleDescription(roleName));
                    roleRepository.save(role);
                    logger.info("✅ Создана роль: {}", roleName);
                }
            } catch (Exception e) {
                logger.warn("⚠️ Не удалось создать роль {}: {}", roleName, e.getMessage());
            }
        }
    }

    private void initializeAdminUser() {
        try {
            if (userService.getUserByUsername("admin").isEmpty()) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setEmail("admin@rams.ru");
                admin.setPassword("admin123");
                admin.setFirstName("Администратор");
                admin.setLastName("Системы");

                userService.createUser(admin, "ADMIN");
                logger.info("✅ Создан администратор (admin/admin123)");
            }
        } catch (Exception e) {
            logger.warn("⚠️ Не удалось создать администратора: {}", e.getMessage());
        }
    }

    private String getRoleDescription(String roleName) {
        switch (roleName) {
            case "ADMIN": return "Администратор системы";
            case "MANAGER": return "Менеджер";
            case "USER": return "Пользователь";
            default: return "Роль";
        }
    }
}
