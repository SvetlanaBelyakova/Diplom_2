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
import static org.hamcrest.Matchers.equalTo;

@Epic("Обновление пользователя")
public class UserUpdateTest {
    private static final String MESSAGE_UNAUTHORIZED = "You should be authorised";
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
    @DisplayName("Обновление пользователя с авторизацией")
    public void updateUserByAuthorization() {
        response = userClient.createUser (user);
        accessToken = response.extract().path("accessToken");
        response = userClient.loginUser (user, accessToken);
        response = userClient.updateUserByAuthorization(GenerateUser .getRandomUser (), accessToken);
        int statusCode = response.extract().statusCode();
        boolean isUpdate = response.extract().path("success");

        assertThat("Код не равен ожидаемому", statusCode, equalTo(SC_OK));
        assertThat("Пользователь не обновлён корректно", isUpdate, equalTo(true));
    }

    @Test
    @DisplayName("Обновление пользователя без авторизации")
    public void updateUserWithoutAuthorization() {
        response = userClient.createUser (user);
        accessToken = response.extract().path("accessToken");
        response = userClient.updateUserWithoutAuthorization(GenerateUser .getRandomUser ());
        int statusCode = response.extract().statusCode();
        String message = response.extract().path("message");
        boolean isUpdate = response.extract().path("success");

        assertThat("Код не равен ожидаемому", statusCode, equalTo(SC_UNAUTHORIZED));
        assertThat("Сообщение не равно ожидаемому", message, equalTo(MESSAGE_UNAUTHORIZED));
        assertThat("Пользователь обновлён корректно", isUpdate, equalTo(false));
    }
}
