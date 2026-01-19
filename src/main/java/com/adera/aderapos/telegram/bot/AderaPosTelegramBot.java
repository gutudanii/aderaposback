package com.adera.aderapos.telegram.bot;

import com.adera.aderapos.product.dtos.ProductDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class AderaPosTelegramBot extends TelegramLongPollingBot {

    private static final Logger logger = LoggerFactory.getLogger(AderaPosTelegramBot.class);

    @Value("${aderapos.telegram.bot-token}")
    private String botToken;

    @Value("${aderapos.telegram.bot-username}")
    private String botUsername;

    @Value("${aderapos.api.base-url}")
    private String baseUrl;

    @Value("${aderapos.telegram.miniapp-url}")
    private String miniAppUrl;

    private final Map<Long, UserSession> sessions = new HashMap<>();
    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasCallbackQuery()) {
            handleCallback(update.getCallbackQuery());
            return;
        }
        if (!update.hasMessage() || !update.getMessage().hasText()) return;
        Message msg = update.getMessage();
        Long chatId = msg.getChatId();
        String text = msg.getText();
        UserSession session = sessions.computeIfAbsent(chatId, k -> new UserSession());
        switch (session.state) {
            case AWAITING_PHONE -> handlePhone(chatId, text, session);
            case AWAITING_PASSWORD -> handlePassword(chatId, text, session);
            case AWAITING_REGISTRATION -> handleRegistration(chatId, text, session);
            case ENTERING_QUANTITY -> handleQuantity(chatId, text);
            default -> handleCommand(chatId, text, session);
        }
    }

    // --- Main Menu ---
    private void sendMainMenu(Long chatId) {
        String welcome = "Welcome to AderaPOS!\nChoose an option:";
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        rows.add(Arrays.asList(
            InlineKeyboardButton.builder().text("Create Sale").callbackData("menu_create_sale").build(),
            InlineKeyboardButton.builder().text("View Invoices").callbackData("menu_invoices").build()
        ));
        rows.add(Arrays.asList(
            InlineKeyboardButton.builder().text("Products").callbackData("menu_products").build(),
            InlineKeyboardButton.builder().text("Help").callbackData("menu_help").build()
        ));
        // Add mobile launcher button using property
        rows.add(List.of(
            InlineKeyboardButton.builder()
                .text("Open Mobile POS")
                .url(miniAppUrl)
                .build()
        ));
        markup.setKeyboard(rows);
        SendMessage msg = new SendMessage(chatId.toString(), welcome);
        msg.setReplyMarkup(markup);
        try { execute(msg); } catch (TelegramApiException e) { logger.error("Telegram sendMainMenu failed", e); }
    }

    // --- Command Handlers ---
    private void handleCommand(Long chatId, String text, UserSession session) {
        switch (text) {
            case "/start" -> {
                if (session.jwt == null) {
                    session.state = BotState.AWAITING_PHONE;
                    sendText(chatId, "Welcome! Please enter your phone number:");
                } else {
                    sendMainMenu(chatId);
                }
            }
            case "/create_sale" -> {
                if (session.jwt == null) {
                    session.state = BotState.AWAITING_PHONE;
                    sendText(chatId, "Please login first. Enter your phone number:");
                } else {
                    startSale(chatId);
                }
            }
            case "/invoice" -> {
                if (session.jwt == null) {
                    session.state = BotState.AWAITING_PHONE;
                    sendText(chatId, "Please login first. Enter your phone number:");
                } else {
                    showInvoices(chatId);
                }
            }
            case "/products" -> {
                if (session.jwt == null) {
                    session.state = BotState.AWAITING_PHONE;
                    sendText(chatId, "Please login first. Enter your phone number:");
                } else {
                    listProducts(chatId);
                }
            }
            default -> sendText(chatId, "Unknown command. Use /start");
        }
    }

    // --- Callback Handler ---
    private void handleCallback(CallbackQuery cb) {
        Long chatId = cb.getMessage().getChatId();
        UserSession s = sessions.get(chatId);
        if (s == null) {
            s = new UserSession();
            sessions.put(chatId, s);
        }
        String data = cb.getData();
        if (data == null) return;
        if (data.startsWith("product:")) {
            s.productId = data.split(":")[1];
            s.state = BotState.ENTERING_QUANTITY;
            sendText(chatId, "Enter quantity:");
        } else if (data.startsWith("pay:")) {
            s.paymentMethod = data.split(":")[1];
            submitSale(chatId, s);
            s.state = BotState.IDLE;
        } else if (data.equals("menu_help")) {
            sendHelp(chatId);
        } else if (data.equals("menu_create_sale")) {
            if (s.jwt == null) {
                s.state = BotState.AWAITING_PHONE;
                sendText(chatId, "Please login first. Enter your phone number:");
            } else {
                startSale(chatId);
            }
        } else if (data.equals("menu_invoices")) {
            if (s.jwt == null) {
                s.state = BotState.AWAITING_PHONE;
                sendText(chatId, "Please login first. Enter your phone number:");
            } else {
                showInvoices(chatId);
            }
        } else if (data.equals("menu_products")) {
            if (s.jwt == null) {
                s.state = BotState.AWAITING_PHONE;
                sendText(chatId, "Please login first. Enter your phone number:");
            } else {
                listProducts(chatId);
            }
        }
        answerCallback(cb.getId());
    }

    private void sendHelp(Long chatId) {
        String help = """
Available commands:
/start - Login or show main menu
/create_sale - Create a new sale
/invoice - View your invoices
/products - List available products
/help - Show this help message

You can also use the menu buttons.
""";
        sendText(chatId, help);
    }

    private void startSale(Long chatId) {
        UserSession old = sessions.get(chatId);
        UserSession next = new UserSession(BotState.SELECTING_PRODUCT);
        if (old != null) {
            next.jwt = old.jwt;
            next.phone = old.phone;
        }
        sessions.put(chatId, next);
        showProductButtons(chatId);
    }

    private void showProductButtons(Long chatId) {
        ProductDTO[] products = null;
        try {
            products = restTemplate.getForObject(
                baseUrl + "/api/products", ProductDTO[].class);
        } catch (Exception e) {
            sendText(chatId, "Failed to fetch products. Please try again later.");
            return;
        }
        if (products == null || products.length == 0) {
            sendText(chatId, "No products found.");
            return;
        }
        InlineKeyboardMarkup kb = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        for (ProductDTO p : products) {
            rows.add(List.of(
                btn(p.getName() + " (" + p.getUnitPrice() + ")", "product:" + p.getId())
            ));
        }
        kb.setKeyboard(rows);
        sendText(chatId, "Select product:", kb);
    }

    private void handleQuantity(Long chatId, String text) {
        try {
            int qty = Integer.parseInt(text);
            UserSession s = sessions.get(chatId);
            s.quantity = qty;
            s.state = BotState.SELECTING_PAYMENT;
            showPaymentButtons(chatId);
        } catch (Exception e) {
            sendText(chatId, "Enter valid quantity.");
        }
    }

    private void showPaymentButtons(Long chatId) {
        InlineKeyboardMarkup kb = new InlineKeyboardMarkup();
        kb.setKeyboard(List.of(
            List.of(btn("CASH","pay:CASH"), btn("TELEBIRR","pay:TELEBIRR")),
            List.of(btn("MPESA","pay:MPESA"))
        ));
        sendText(chatId, "Select payment method:", kb);
    }


    private void answerCallback(String callbackId) {
        try { execute(new AnswerCallbackQuery(callbackId)); } catch (TelegramApiException ignored) {}
    }

    private void submitSale(Long chatId, UserSession s) {
        Map<String,Object> sale = Map.of(
            "paymentMethod", s.paymentMethod,
            "saleItems", List.of(
                Map.of(
                    "productId", s.productId,
                    "quantity", s.quantity
                )
            )
        );
        restTemplate.postForObject(
            baseUrl + "/api/telegram/sale",
            new HttpEntity<>(sale, telegramHeader(chatId)),
            Object.class
        );
        sendText(chatId, "✅ Sale created successfully!");
    }

    private void handlePhone(Long chatId, String text, UserSession session) {
        session.phone = text;
        session.state = BotState.AWAITING_PASSWORD;
        sendText(chatId, "Please enter your password:");
    }

    private void handlePassword(Long chatId, String text, UserSession session) {
        session.password = text;
        Map<String, Object> loginReq = Map.of("username", session.phone, "password", session.password);
        try {
            ResponseEntity<Map<String, Object>> resp = restTemplate.postForEntity(
                baseUrl + "/auth/login", loginReq, (Class<Map<String, Object>>)(Class<?>)Map.class);
            if (resp.getStatusCode().is2xxSuccessful() && resp.getBody() != null && resp.getBody().get("token") != null) {
                session.jwt = (String) resp.getBody().get("token");
                session.state = BotState.IDLE;
                sendText(chatId, "✅ Login successful! Welcome.");
                sendMainMenu(chatId);
            } else {
                session.state = BotState.AWAITING_REGISTRATION;
                sendText(chatId, "Login failed. Would you like to register? (yes/no)");
            }
        } catch (Exception e) {
            logger.error("Login failed", e);
            session.state = BotState.AWAITING_REGISTRATION;
            sendText(chatId, "Login failed. Would you like to register? (yes/no)");
        }
    }

    private void handleRegistration(Long chatId, String text, UserSession session) {
        if (text.equalsIgnoreCase("yes")) {
            Map<String, Object> user = Map.of(
                "username", session.phone,
                "password", session.password,
                "role", "MERCHANT_ADMIN"
            );
            try {
                restTemplate.postForEntity(baseUrl + "/api/users", user, Object.class);
                sendText(chatId, "Registration successful! Please /start to login.");
                session.state = BotState.IDLE;
            } catch (Exception e) {
                logger.error("Registration failed", e);
                String errorMsg = e.getMessage();
                sendText(chatId, "Registration failed: " + (errorMsg != null ? errorMsg : "Please try again or contact support."));
                session.state = BotState.IDLE;
            }
        } else {
            sendText(chatId, "Registration cancelled. Please /start to try again.");
            session.state = BotState.IDLE;
        }
    }

    // --- Utility: sendText ---
    private void sendText(Long chatId, String text) {
        sendText(chatId, text, null);
    }

    private void sendText(Long chatId, String text, InlineKeyboardMarkup kb) {
        SendMessage msg = new SendMessage(chatId.toString(), text);
        if (kb != null) msg.setReplyMarkup(kb);
        try {
            execute(msg);
        } catch (TelegramApiException e) {
            logger.error("Telegram sendText failed", e);
        }
    }

    private HttpHeaders telegramHeader(Long chatId) {
        UserSession session = sessions.get(chatId);
        HttpHeaders h = new HttpHeaders();
        h.set("X-Telegram-Id", chatId.toString());
        h.setContentType(MediaType.APPLICATION_JSON);
        if (session != null && session.jwt != null) h.set("Authorization", "Bearer " + session.jwt);
        return h;
    }

    private void showInvoices(Long chatId) {
        try {
            Object[] invoices = restTemplate.exchange(
                baseUrl + "/api/telegram/invoices",
                HttpMethod.GET,
                new HttpEntity<>(telegramHeader(chatId)),
                Object[].class
            ).getBody();
            sendText(chatId, "Invoices found: " + (invoices != null ? invoices.length : 0));
        } catch (Exception e) {
            logger.error("Failed to fetch invoices", e);
            sendText(chatId, "Failed to fetch invoices. Please login again.");
        }
    }

    private void listProducts(Long chatId) {
        try {
            ProductDTO[] products = restTemplate.exchange(
                    baseUrl + "/api/products",
                    HttpMethod.GET,
                    new HttpEntity<>(telegramHeader(chatId)),
                    ProductDTO[].class
            ).getBody();
            StringBuilder sb = new StringBuilder("Products:\n");
            if (products != null) {
                for (ProductDTO p : products) {
                    sb.append(p.getName()).append(" - ").append(p.getUnitPrice()).append("\n");
                }
            }
            sendText(chatId, sb.toString());
        } catch (Exception e) {
            logger.error("Failed to fetch products", e);
            sendText(chatId, "Failed to fetch products. Please login again.");
        }
    }

    enum BotState {
        IDLE,
        AWAITING_PHONE,
        AWAITING_PASSWORD,
        AWAITING_REGISTRATION,
        SELECTING_PRODUCT,
        ENTERING_QUANTITY,
        SELECTING_PAYMENT
    }

    static class UserSession {
        BotState state = BotState.IDLE;
        String phone;
        String password;
        String jwt;
        String productId;
        int quantity;
        String paymentMethod;

        UserSession() {}
        UserSession(BotState state) { this.state = state; }
    }

    private InlineKeyboardButton btn(String t, String d) {
        return InlineKeyboardButton.builder().text(t).callbackData(d).build();
    }
}
