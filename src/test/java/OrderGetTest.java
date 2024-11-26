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
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Epic("Получение заказа и ингредиентов")
public class OrderGetTest {
    private static final String MESSAGE_UNAUTHORIZED = "You should be authorised";
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
        fillListIngredients();
    }

    @Test
    @DisplayName("Получить все ингредиенты")
    public void getAllIngredients() {
        response = orderClient.getAllIngredients();
        int statusCode = response.extract().statusCode();
        boolean isGet = response.extract().path("success");

        assertThat("Код не равен ожидаемому", statusCode, equalTo(SC_OK));
        assertThat("Ингредиенты получены некорректно", isGet, equalTo(true));
    }

    @Test
    @DisplayName("Получить все заказы")
    public void getAllOrders() {
        response = orderClient.createOrderWithoutAuthorization(order);
        response = orderClient.getAllOrders();
        int statusCode = response.extract().statusCode();
        boolean isGet = response.extract().path("success");

        assertThat("Код не равен ожидаемому", statusCode, equalTo(SC_OK));
        assertThat("Заказы получены некорректно", isGet, equalTo(true));
    }

    @Test
    @DisplayName("Получить заказ авторизованного пользователя")
    public void getOrderByAuthorizationUser () {
        response = userClient.createUser (user);
        String accessToken = response.extract().path("accessToken");
        response = userClient.loginUser (user, accessToken);
        response = orderClient.createOrderByAuthorization(order, accessToken);
        response = orderClient.getOrdersByAuthorization(accessToken);
        int statusCode = response.extract().statusCode();
        boolean isGet = response.extract().path("success");
        response = userClient.deleteUser (StringUtils.substringAfter(accessToken, " "));

        assertThat("Код не равен ожидаемому", statusCode, equalTo(SC_OK));
        assertThat("Заказ получен некорректно", isGet, equalTo(true));
    }

    @Test
    @DisplayName("Получить заказ без авторизации")
    public void getOrderWithoutAuthorizationUser () {
        response = orderClient.createOrderWithoutAuthorization(order);
        response = orderClient.getOrdersWithoutAuthorization();
        int statusCode = response.extract().statusCode();
        String message = response.extract().path("message");
        boolean isGet = response.extract().path("success");

        assertThat("Код не равен ожидаемому", statusCode, equalTo(SC_UNAUTHORIZED));
        assertThat("Сообщение не равно ожидаемому", message, equalTo(MESSAGE_UNAUTHORIZED));
        assertThat("Заказ получен корректно", isGet, equalTo(false));
    }

    private void fillListIngredients() {
        response = orderClient.getAllIngredients();
        List<String> list = response.extract().path("data._id");
        List<String> ingredients = order.getIngredients();
        ingredients.add(list.get(0));
        ingredients.add(list.get(5));
        ingredients.add(list.get(0));
    }
}