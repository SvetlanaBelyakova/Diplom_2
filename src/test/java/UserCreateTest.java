import org.junit.Test;
import org.junit.Before;
import io.qameta.allure.Epic;
import io.qameta.allure.junit4.DisplayName;
import yandex.praktikum.User;
import yandex.praktikum.UserClient;
import yandex.praktikum.GenerateUser ;
import org.apache.commons.lang3.StringUtils;
import io.restassured.response.ValidatableResponse;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Epic("Создание пользователя")
public class UserCreateTest {
    private static final String MESSAGE_FORBIDDEN = "User already exists";
    private static final String MESSAGE_FORBIDDEN_EMPTY_FIELD = "Email, password and name are required fields";
    private ValidatableResponse response;
    private UserClient userClient;
    private User user;

    @Before
    public void setUp() {
        user = GenerateUser .getRandomUser ();
        userClient = new UserClient();
    }

    @Test
    @DisplayName("Создание пользователя с корректными данными")
    public void userCreateByValidCredentials() {
        response = userClient.createUser (user);
        int statusCode = response.extract().statusCode();
        boolean isCreate = response.extract().path("success");
        String accessToken = response.extract().path("accessToken");
        response = userClient.deleteUser (StringUtils.substringAfter(accessToken, " "));

        assertThat("Код не равен ожидаемому", statusCode, equalTo(SC_OK));
        assertThat("Пользователь создан некорректно", isCreate, equalTo(true));
    }

    @Test
    @DisplayName("Создание пользователя с пустым email")
    public void userCreateIsEmptyEmail() {
        user.setEmail(null);
        response = userClient.createUser (user);
        int statusCode = response.extract().statusCode();
        String message = response.extract().path("message");
        boolean isCreate = response.extract().path("success");

        assertThat("Код не равен ожидаемому", statusCode, equalTo(SC_FORBIDDEN));
        assertThat("Сообщение не равно ожидаемому", message, equalTo(MESSAGE_FORBIDDEN_EMPTY_FIELD));
        assertThat("Пользователь создан корректно", isCreate, equalTo(false));
    }

    @Test
    @DisplayName("Создание пользователя с пустым паролем")
    public void userCreateIsEmptyPassword() {
        user.setPassword(null);
        response = userClient.createUser (user);
        int statusCode = response.extract().statusCode();
        String message = response.extract().path("message");
        boolean isCreate = response.extract().path("success");

        assertThat("Код не равен ожидаемому", statusCode, equalTo(SC_FORBIDDEN));
        assertThat("Сообщение не равно ожидаемому", message, equalTo(MESSAGE_FORBIDDEN_EMPTY_FIELD));
        assertThat("Пользователь создан корректно", isCreate, equalTo(false));
    }

    @Test
    @DisplayName("Создание пользователя с пустым именем")
    public void userCreateIsEmptyName() {
        user.setName(null);
        response = userClient.createUser (user);
        int statusCode = response.extract().statusCode();
        String message = response.extract().path("message");
        boolean isCreate = response.extract().path("success");

        assertThat("Код не равен ожидаемому", statusCode, equalTo(SC_FORBIDDEN));
        assertThat("Сообщение не равно ожидаемому", message, equalTo(MESSAGE_FORBIDDEN_EMPTY_FIELD));
        assertThat("Пользователь создан корректно", isCreate, equalTo(false));
    }

    @Test
    @DisplayName("Повторный запрос на создание пользователя")
    public void repeatedRequestByCreateUser () {
        userClient.createUser (user);
        response = userClient.createUser (user);
        int statusCode = response.extract().statusCode();
        String message = response.extract().path("message");
        boolean isCreate = response.extract().path("success");

        assertThat("Код не равен ожидаемому", statusCode, equalTo(SC_FORBIDDEN));
        assertThat("Сообщение не равно ожидаемому", message, equalTo(MESSAGE_FORBIDDEN));
        assertThat("Пользователь создан корректно", isCreate, equalTo(false));
    }
}
