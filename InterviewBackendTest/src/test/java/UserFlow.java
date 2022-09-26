import io.restassured.response.Response;
import models.CreateUserModel;
import models.UpdateUserModel;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import services.GoRestService;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserFlow {

    static int userId;
    static String userName;
    static String userGender;
    static String userEmail;
    static String userStatus;

    @DisplayName("Create user")
    @Test
    @Order(1)
    public void Users_CreateUsers(){
        Response response = GoRestService.createUser(new CreateUserModel("Joseph Newman", "Male", "qatest1@test.com", "Active"));

        response.prettyPrint();
        userId = response.path("id");
        //System.out.println(userId);
        userName = response.path("name");
        userGender = response.path("gender");
        userEmail = response.path("email");
        userStatus = response.path("status");
    }

    @DisplayName("Get created User")
    @Test
    @Order(2)
    public void Users_GetCreated(){
        System.out.println("https://gorest.co.in/public/v2/users/" + userId);
        Response response = GoRestService.getUserInfo(userId);
        response.prettyPrint();

        Integer id = (Integer) response.path("id");
        String name = (String) response.path("name");
        String gender = (String) response.path("gender");
        String email = (String) response.path("email");
        String status = (String) response.path("status");

        assertEquals(response.statusCode(), SC_OK);
        assertEquals(response.contentType(), "application/json; charset=utf-8");
        assertEquals(id, userId);
        assertEquals(name, userName);
        assertEquals(gender, userGender);
        assertEquals(email, userEmail);
        assertEquals(status, userStatus);

    }

    @DisplayName("Update User")
    @Test
    @Order(3)
    public void Users_UpdateUser() {
        GoRestService.updateUserInfo(new UpdateUserModel.Builder().name("Joseph Newman").gender("Male").email("qatest1@test.com").status("inactive").build(), userId)
                .then()
                .log().all()
                .statusCode(SC_OK)
                .body("id", equalTo(userId))
                .body("status", equalTo("inactive"));
    }

    @DisplayName("Delete created user")
    @Test
    @Order(4)
    public void Users_deleteUser() {
        GoRestService.deleteUser(userId)
                .then()
                .statusCode(SC_NO_CONTENT);
    }

    @DisplayName("Call deleted user")
    @Test
    @Order(5)
    public void Users_getDeleted(){
        GoRestService.getUserInfo(userId)
                .then()
                .statusCode(SC_NOT_FOUND)
                .body("message", equalTo("Resource not found"));
    }
}
