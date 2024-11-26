package yandex.praktikum;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import static io.restassured.RestAssured.given;

public class UserClient extends Burger {

    @Step("Отправить GET-запрос на /api/auth/user")
    public ValidatableResponse getUser (String accessToken) {
        return given()
                .spec(getBaseSpec())
                .header("Authorization", accessToken)
                .log().all()
                .get(EndPoints.USER_PATH + "user")
                .then()
                .log().all();
    }

    @Step("Отправить POST-запрос на /api/auth/register")
    public ValidatableResponse createUser (User user) {
        return given()
                .spec(getBaseSpec())
                .body(user)
                .log().all()
                .post(EndPoints.USER_PATH + "register")
                .then()
                .log().all();
    }

    @Step("Отправить POST-запрос на /api/auth/login")
    public ValidatableResponse loginUser (User user, String accessToken) {
        return given()
                .spec(getBaseSpec())
                .auth().oauth2(accessToken)
                .body(user)
                .log().all()
                .post(EndPoints.USER_PATH + "login")
                .then()
                .log().all();
    }

    @Step("Отправить POST-запрос на /api/auth/logout")
    public ValidatableResponse logoutUser (String refreshToken) {
        return given()
                .spec(getBaseSpec())
                .body(refreshToken)
                .log().all()
                .post(EndPoints.USER_PATH + "logout")
                .then()
                .log().all();
    }

    @Step("Отправить DELETE-запрос на /api/auth/user")
    public ValidatableResponse deleteUser (String accessToken) {
        return given()
                .spec(getBaseSpec())
                .auth().oauth2(accessToken)
                .log().all()
                .delete(EndPoints.USER_PATH + "user")
                .then()
                .log().all();
    }

    @Step("Отправить PATCH-запрос на /api/auth/user с авторизацией")
    public ValidatableResponse updateUserByAuthorization(User user, String accessToken) {
        return given()
                .spec(getBaseSpec())
                .header("Authorization", accessToken)
                .body(user)
                .log().all()
                .patch(EndPoints.USER_PATH + "user")
                .then()
                .log().all();
    }

    @Step("Отправить PATCH-запрос на /api/auth/user без авторизации")
    public ValidatableResponse updateUserWithoutAuthorization(User user) {
        return given()
                .spec(getBaseSpec())
                .body(user)
                .log().all()
                .patch(EndPoints.USER_PATH + "user")
                .then()
                .log().all();
    }
}

