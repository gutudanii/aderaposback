package com.adera.aderapos.telegram.bot;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

public class TelegramBotUtils {
    public static ReplyKeyboardMarkup getContactKeyboard() {
        KeyboardButton contactButton = new KeyboardButton("Share phone");
        contactButton.setRequestContact(true);
        KeyboardRow row = new KeyboardRow();
        row.add(contactButton);
        List<KeyboardRow> keyboard = new ArrayList<>();
        keyboard.add(row);
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setKeyboard(keyboard);
        markup.setOneTimeKeyboard(true);
        markup.setResizeKeyboard(true);
        return markup;
    }
}

