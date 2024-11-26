package yandex.praktikum;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import static io.restassured.http.ContentType.JSON;

public class Burger {
    // Базовый URL для API
    public static final String BASE_URL = "https://stellarburgers.nomoreparties.site/";

    // Метод для получения базовой спецификации запроса
    protected static RequestSpecification getBaseSpec() {
        return new RequestSpecBuilder()
                .setContentType(JSON) // Устанавливаем тип контента
                .setBaseUri(BASE_URL) // Устанавливаем базовый URI
                .build(); // Создаём спецификацию запроса
    }
}
