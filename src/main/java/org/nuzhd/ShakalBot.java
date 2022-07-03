package org.nuzhd;

import org.nuzhd.service.ImageByFileIdParser;
import org.nuzhd.service.ImageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

@Component
public class ShakalBot extends TelegramLongPollingBot {

    private static final Logger logger = LoggerFactory.getLogger(ShakalBot.class);

    @Value("${bot.choose}")
    private String botChoose;
    @Value("${bot.start}")
    private String startPhrase;
    @Value("${bot.token}")
    private String botToken;
    @Value("${bot.username}")
    private String botUsername;

    @Autowired
    private ImageService imageService;

    @Autowired
    private ImageByFileIdParser parser;

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {

        if (! update.hasMessage()) {
            return;
        }

        Message msg = update.getMessage();
        String chatId = msg.getChatId().toString();

        try {
            if (msg.hasDocument()) {
                String fileId;
                logger.info("Пользователь с chatId " + chatId + " отправил документ/фото");

                fileId = msg.getDocument().getFileId();

                ByteArrayInputStream bais = new ByteArrayInputStream(parser.parseImageFromTgServers(fileId));

                BufferedImage image = ImageIO.read(bais);
                File f = new File("src/main/resources/photos/" + chatId);
                f.mkdir();

                imageService.saveImage(chatId, image);
                sendTextMsg(chatId, botChoose);

            } else if (msg.hasPhoto()) {
                sendTextMsg(chatId, "Для начала работы с ботом отправьте фото в виде файла");
            } else if (msg.isCommand()) {
                if (msg.getText().equals("/start")) {
                    sendTextMsg(chatId, startPhrase);
                }

            } else {
                if (msg.hasText()) {
                    Integer input = Integer.parseInt(msg.getText());
                    logger.info("Пользователь с chatId " + chatId + " написал: " + msg.getText());

                    if (input < 1 || input > 10) throw new NumberFormatException();

                    imageService.corruptImage(chatId, input > 1 && input < 10 ? input * 0.01f : input == 1 ? 0.1f : 0.01f);
                    sendCorruptedPhoto(chatId);
                }
            }

        } catch (InterruptedException | IOException e) {
            sendTextMsg(chatId, "Произошла ошибка при обработке фото");
            throw new RuntimeException(e);

        } catch (NumberFormatException e) {
            sendTextMsg(chatId, "Введено некорректное значение");
        }

    }


    private void sendTextMsg(String chatId, String text) {
        SendMessage msg = SendMessage.builder().chatId(chatId).text(text).build();

        try {
            execute(msg);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendCorruptedPhoto(String chatId) {

        InputFile photo = new InputFile(new File("src/main/resources/photos/" + chatId + "/compressed_image.jpg"));

        SendPhoto p = SendPhoto.builder().chatId(chatId).photo(photo).build();

        try {
            execute(p);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }


}
