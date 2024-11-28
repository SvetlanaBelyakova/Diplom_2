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
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Epic("Получение пользователя")
public class UserGetTest {
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
    @DisplayName("Получение пользователя с корректными данными")
    public void userGetByValidCredentials() {
        ValidatableResponse response = userClient.createUser (user);
        accessToken = response.extract().path("accessToken");
        response = userClient.getUser (accessToken);
        int statusCode = response.extract().statusCode();
        boolean isGet = response.extract().path("success");
        String email = response.extract().path("user.email");
        String name = response.extract().path("user.name");

        assertThat("Код не равен ожидаемому", statusCode, equalTo(SC_OK));
        assertThat("Пользователь получен некорректно", isGet, equalTo(true));
        assertThat("Email не соответствует ожидаемому", email, equalTo(user.getEmail()));
        assertThat("Имя не соответствует ожидаемому", name, equalTo(user.getName()));
    }
}

