package com.example.Rams.controller;
import com.example.Rams.models.ContactRequest;
import com.example.Rams.services.EmailService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class MainController {
    private static final Logger logger = LoggerFactory.getLogger(MainController.class);
    private final EmailService emailService;

    // Внедрение зависимости через конструктор
    public MainController(EmailService emailService) {
        this.emailService = emailService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("pageTitle", "Главная");
        model.addAttribute("activePage", "home");
        // Добавляем объект для формы обратной связи
        if (!model.containsAttribute("contactRequest")) {
            model.addAttribute("contactRequest", new ContactRequest());
        }
        return "layout";
    }

    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("pageTitle", "О нас");
        model.addAttribute("activePage", "about");
        return "layout";
    }

    @GetMapping("/contacts")
    public String contacts(Model model) {
        model.addAttribute("pageTitle", "Контакты");
        model.addAttribute("activePage", "contacts");
        // Добавляем объект для формы
        if (!model.containsAttribute("contactRequest")) {
            model.addAttribute("contactRequest", new ContactRequest());
        }
        return "layout";
    }

    @GetMapping("/services")
    public String services(Model model) {
        model.addAttribute("pageTitle", "Услуги");
        model.addAttribute("activePage", "services");
        return "layout";
    }

    // Обработчик формы обратной связи - ТЕПЕРЬ АСИНХРОННЫЙ
    @PostMapping("/send-contact")
    public String handleContactForm(
            @Valid @ModelAttribute ContactRequest contactRequest,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            // Возвращаем ошибки на страницу
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.contactRequest", bindingResult);
            redirectAttributes.addFlashAttribute("contactRequest", contactRequest);
            return "redirect:/contacts";
        }

        try {
            // ⚡ АСИНХРОННАЯ ОТПРАВКА - пользователь не ждет
            emailService.sendContactRequest(contactRequest);

            logger.info("📋 Заявка принята (асинхронно): {} - {}",
                    contactRequest.getName(), contactRequest.getPhone());

            redirectAttributes.addFlashAttribute("successMessage",
                    "✅ Заявка успешно отправлена! Мы свяжемся с вами в течение 15 минут.");

        } catch (Exception e) {
            logger.error("❌ Ошибка при асинхронной отправке заявки от {}: {}",
                    contactRequest.getName(), e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage",
                    "⚠️ Заявка принята, но возникла техническая ошибка. Мы свяжемся с вами по телефону.");
        }

        return "redirect:/contacts";
    }

    // REST endpoint для AJAX (обновлен под асинхронность)
    @PostMapping("/api/contact")
    @ResponseBody
    public String handleAjaxContact(@Valid @RequestBody ContactRequest contactRequest) {
        try {
            // Асинхронная отправка для AJAX
            emailService.sendContactRequest(contactRequest);

            logger.info("🌐 AJAX заявка принята: {} - {}",
                    contactRequest.getName(), contactRequest.getPhone());

            return "success";
        } catch (Exception e) {
            logger.error("❌ Ошибка AJAX отправки: {}", e.getMessage());
            return "error";
        }
    }

    // Тестовый endpoint для проверки почты
    @GetMapping("/test-email-setup")
    @ResponseBody
    public String testEmailSetup() {
        try {
            logger.info("=== 🧪 ТЕСТИРОВАНИЕ ПОЧТЫ ===");

            // Создаем тестовую заявку
            ContactRequest testRequest = new ContactRequest();
            testRequest.setName("Тестовое Имя");
            testRequest.setPhone("+79991234567");
            testRequest.setEmail("test@example.com");
            testRequest.setMessage("Это тестовое сообщение для проверки почты");

            // ⚡ Асинхронная отправка теста
            emailService.sendContactRequest(testRequest);

            return "✅ Тестовое письмо отправляется асинхронно! " +
                    "Проверьте почту lordselebros@yandex.ru в течение 1-2 минут.<br><br>" +
                    "💡 <strong>Обработка теперь асинхронная - ответ мгновенный!</strong>";

        } catch (Exception e) {
            logger.error("❌ Ошибка тестирования почты:", e);
            return "❌ Ошибка настройки: " + e.getMessage() +
                    "<br><br>🔧 Проверьте:<br>" +
                    "- Пароль приложения Yandex<br>" +
                    "- Настройки SMTP в application.properties<br>" +
                    "- Конфигурацию AsyncConfig";
        }
    }

    // Дополнительный endpoint для быстрой проверки
    @GetMapping("/quick-test")
    @ResponseBody
    public String quickTest() {
        try {
            ContactRequest quickRequest = new ContactRequest();
            quickRequest.setName("Быстрый тест");
            quickRequest.setPhone("+79990000000");
            quickRequest.setMessage("Быстрая проверка скорости отправки");

            long startTime = System.currentTimeMillis();
            emailService.sendContactRequest(quickRequest);
            long endTime = System.currentTimeMillis();

            return String.format("⚡ Ответ за %d мс! Письмо отправляется в фоне.",
                    (endTime - startTime));

        } catch (Exception e) {
            return "❌ Ошибка: " + e.getMessage();
        }
    }
}