import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import io.qameta.allure.Epic;
import io.qameta.allure.junit4.DisplayName;
import yandex.praktikum.User;
import yandex.praktikum.UserClient;
import yandex.praktikum.GenerateUser ;
import org.apache.commons.lang3.StringUtils;
import io.restassured.response.ValidatableResponse;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Epic("Вход и выход пользователя")
public class UserLoginTest {
    private static final String MESSAGE_LOGOUT = "Successful logout";
    private static final String MESSAGE_UNAUTHORIZED = "email or password are incorrect";
    private ValidatableResponse response;
    private UserClient userClient;
    private User user;
    private String accessToken;

    @Before
    public void setUp() {
        user = GenerateUser .getRandomUser ();
        userClient = new UserClient();
    }

    @After
    public void clearState() {
        if (accessToken != null) {
            userClient.deleteUser (StringUtils.substringAfter(accessToken, " "));
        }
    }

    @Test
    @DisplayName("Вход пользователя с корректными данными")
    public void userLoginByValidCredentials() {
        response = userClient.createUser (user);
        accessToken = response.extract().path("accessToken");
        response = userClient.loginUser (user, accessToken);
        int statusCode = response.extract().statusCode();
        boolean isLogin = response.extract().path("success");

        assertThat("Токен равен null", accessToken, notNullValue());
        assertThat("Код не равен ожидаемому", statusCode, equalTo(SC_OK));
        assertThat("Пользователь не вошёл корректно", isLogin, equalTo(true));
    }

    @Test
    @DisplayName("Выход пользователя с корректными данными")
    public void userLogoutByValidCredentials() {
        response = userClient.createUser (user);
        accessToken = response.extract().path("accessToken");
        response = userClient.loginUser (user, accessToken);
        String refreshToken = response.extract().path("refreshToken");
        refreshToken = "{\"token\":\"" + refreshToken + "\"}";
        response = userClient.logoutUser (refreshToken);
        int statusCode = response.extract().statusCode();
        String message = response.extract().path("message");
        boolean isLogout = response.extract().path("success");

        assertThat("Токен равен null", refreshToken, notNullValue());
        assertThat("Код не равен ожидаемому", statusCode, equalTo(SC_OK));
        assertThat("Сообщение не равно ожидаемому", message, equalTo(MESSAGE_LOGOUT));
        assertThat("Пользователь не вышел корректно", isLogout, equalTo(true));
    }

    @Test
    @DisplayName("Вход пользователя с пустым email")
    public void userLoginByEmptyEmail() {
        response = userClient.createUser (user);
        accessToken = response.extract().path("accessToken");
        user.setEmail(null);
        response = userClient.loginUser (user, accessToken);
        int statusCode = response.extract().statusCode();
        String message = response.extract().path("message");
        boolean isLogin = response.extract().path("success");

        assertThat("Токен равен null", accessToken, notNullValue());
        assertThat("Код не равен ожидаемому", statusCode, equalTo(SC_UNAUTHORIZED));
        assertThat("Сообщение не равно ожидаемому", message, equalTo(MESSAGE_UNAUTHORIZED));
        assertThat("Пользователь вошёл корректно", isLogin, equalTo(false));
    }

    @Test
    @DisplayName("Вход пользователя с пустым паролем")
    public void userLoginByEmptyPassword() {
        response = userClient.createUser (user);
        accessToken = response.extract().path("accessToken");
        user.setPassword(null);
        response = userClient.loginUser (user, accessToken);
        int statusCode = response.extract().statusCode();
        String message = response.extract().path("message");
        boolean isLogin = response.extract().path("success");

        assertThat("Токен равен null", accessToken, notNullValue());
        assertThat("Код не равен ожидаемому", statusCode, equalTo(SC_UNAUTHORIZED));
        assertThat("Сообщение не равно ожидаемому", message, equalTo(MESSAGE_UNAUTHORIZED));
        assertThat("Пользователь вошёл корректно", isLogin, equalTo(false));
    }
}
