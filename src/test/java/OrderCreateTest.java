import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.Before;
import io.qameta.allure.Epic;
import io.qameta.allure.junit4.DisplayName;
import yandex.praktikum.User;
import yandex.praktikum.Order;
import yandex.praktikum.UserClient;
import yandex.praktikum.OrderClient;
import yandex.praktikum.GenerateUser ;
import org.apache.commons.lang3.StringUtils;
import io.restassured.response.ValidatableResponse;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Epic("Создание заказа")
public class OrderCreateTest {
    private static final String MESSAGE_BAD_REQUEST = "Ingredient ids must be provided";
    private ValidatableResponse response;
    private User user;
    private Order order;
    private UserClient userClient;
    private OrderClient orderClient;

    @Before
    public void setUp() {
        user = GenerateUser .getRandomUser ();
        order = new Order();
        userClient = new UserClient();
        orderClient = new OrderClient();
    }

    @Test
    @DisplayName("Создание заказа с авторизацией")
    public void orderCreateByAuthorization() {
        fillListIngredients();

        // Создаем пользователя
        if (user.getEmail() == null || user.getPassword() == null || user.getName() == null) {
            throw new RuntimeException("Не все обязательные поля пользователя заполнены");
        }

        response = userClient.createUser (user);
        if (response.extract().statusCode() != SC_OK) {
            throw new RuntimeException("Не удалось создать пользователя: " + response.extract().asString());
        }

        String accessToken = response.extract().path("accessToken");
        if (accessToken == null) {
            throw new RuntimeException("accessToken не был получен из ответа: " + response.extract().asString());
        }

        // Логинимся с полученным токеном
        response = userClient.loginUser (user, accessToken);
        if (response.extract().statusCode() != SC_OK) {
            throw new RuntimeException("Не удалось войти в систему: " + response.extract().asString());
        }

        // Создаем заказ
        response = orderClient.createOrderByAuthorization(order, accessToken);
        int statusCode = response.extract().statusCode();
        boolean isCreate = response.extract().path("success");
        int orderNumber = response.extract().path("order.number");
        String orderId = response.extract().path("order._id");

        // Удаляем пользователя
        response = userClient.deleteUser (StringUtils.substringAfter(accessToken, " "));

        // Проверяем результаты
        assertThat("Код не равен ожидаемому", statusCode, equalTo(SC_OK));
        assertThat("Заказ создан некорректно", isCreate, equalTo(true));
        assertThat("Номер заказа равен null", orderNumber, notNullValue());
        assertThat("ID заказа равен null", orderId, notNullValue());
    }

    @Test
    @DisplayName("Создание заказа без авторизации")
    public void orderCreateWithoutAuthorization() {
        fillListIngredients();
        response = orderClient.createOrderWithoutAuthorization(order);
        int statusCode = response.extract().statusCode();
        boolean isCreate = response.extract().path("success");
        int orderNumber = response.extract().path("order.number");

        assertThat("Код не равен ожидаемому", statusCode, equalTo(SC_OK));
        assertThat("Заказ создан некорректно", isCreate, equalTo(true));
        assertThat("Номер заказа равен null", orderNumber, notNullValue());
    }

    @Test
    @DisplayName("Создание заказа без авторизации и ингредиентов")
    public void orderCreateWithoutAuthorizationAndIngredients() {
        response = orderClient.createOrderWithoutAuthorization(order);
        int statusCode = response.extract().statusCode();
        String message = response.extract().path("message");
        boolean isCreate = response.extract().path("success");

        assertThat("Код не равен ожидаемому", statusCode, equalTo(SC_BAD_REQUEST));
        assertThat("Сообщение не равно ожидаемому", message, equalTo(MESSAGE_BAD_REQUEST));
        assertThat("Заказ создан корректно", isCreate, equalTo(false));
    }

    @Test
    @DisplayName("Создание заказа без авторизации и изменение хеша ингредиента")
    public void orderCreateWithoutAuthorizationAndChangeHashIngredient() {
        // Получаем все ингредиенты
        response = orderClient.getAllIngredients();
        List<String> list = response.extract().path("data._id");

        // Создаем список ингредиентов
        List<String> ingredients = new ArrayList<>();
        ingredients.add(list.get(0));
        ingredients.add(list.get(5).replaceAll("a", "l")); // Изменение символа "a" на "l"
        ingredients.add(list.get(0));

        // Устанавливаем ингредиенты в заказ
        order.setIngredients(ingredients);

        // Создаем заказ без авторизации
        response = orderClient.createOrderWithoutAuthorization(order);
        int statusCode = response.extract().statusCode();

        // Проверяем статус код
        assertThat("Код не равен ожидаемому", statusCode, equalTo(SC_INTERNAL_SERVER_ERROR));
    }

    private void fillListIngredients() {
        response = orderClient.getAllIngredients();
        List<String> list = response.extract().path("data._id");

        // Создаем список ингредиентов
        List<String> ingredients = new ArrayList<>();
        ingredients.add(list.get(0));
        ingredients.add(list.get(5));
        ingredients.add(list.get(0));

        // Устанавливаем ингредиенты в заказ
        order.setIngredients(ingredients);
    }
}
