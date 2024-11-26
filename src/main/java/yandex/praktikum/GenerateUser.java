package yandex.praktikum;

import io.qameta.allure.Allure;
import org.apache.commons.lang3.RandomStringUtils;

public class GenerateUser  {

    // Метод для генерации случайного пользователя
    public static User getRandomUser () {
        // Генерация случайного имени длиной 8 символов
        String name = RandomStringUtils.randomAlphabetic(8);

        // Формирование адреса электронной почты на основе имени
        String email = name.toLowerCase() + "@yandex.ru";

        // Генерация случайного пароля длиной 8 символов
        String password = RandomStringUtils.randomAlphabetic(8);

        // Добавление информации о созданном пользователе в отчет Allure
        Allure.addAttachment("Email: ", email);
        Allure.addAttachment("Password: ", password);
        Allure.addAttachment("Name: ", name);

        // Возврат нового пользователя
        return new User(email, password, name);
    }
}