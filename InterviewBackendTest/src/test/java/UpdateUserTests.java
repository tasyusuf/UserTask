import io.restassured.response.Response;
import models.CreateUserModel;
import models.UpdateUserModel;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.ValueSource;
import services.GoRestService;

import java.util.ArrayList;
import java.util.List;

import static org.apache.http.HttpStatus.*;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UpdateUserTests {

    static List<Integer> userIds = new ArrayList<>();

    @DisplayName("User can be created with CSV resource")
    @ParameterizedTest
    @Order(1)
    @CsvFileSource(resources = "/MOCK_DATA.csv", numLinesToSkip = 1)
    public void Users_CreateUser_MultipleOneShot(String name, String gender, String email, String status){
        Response response = GoRestService.createUser(new CreateUserModel(name, gender, email, status));

        assertEquals(response.statusCode(), SC_CREATED);
        assertEquals(response.contentType(), "application/json; charset=utf-8");
        userIds.add(response.path("id"));
        //System.out.println(userIds.toString());

    }

    @DisplayName("User can update information with complete data")
    @Test
    @Order(2)
    public void updateUser_updateInfo() {
        GoRestService.updateUserInfo(new UpdateUserModel.Builder().name("Sally Maffioletti").gender("Female").email("nmaffioletti120@flickr.com").status("inactive").build(), userIds.get(0))
                .then()
                .log().all()
                .statusCode(SC_OK)
                .body("id", equalTo(userIds.get(0)))
                .body("status", equalTo("inactive"));
    }

    @DisplayName("User can not update with missing data")
    @ParameterizedTest
    @Order(3)
    @ValueSource(ints = {6,7,8,9})
    public void updateUser_updateStatus(int number){
        GoRestService.updateUserStatus(new UpdateUserModel.Builder().status("inactive").build(), userIds.get(number))
                .then()
                .log().all()
                .statusCode(SC_UNPROCESSABLE_ENTITY)
                .body("[0].field", equalTo("email"))
                .body("[0].message", containsString("can't be blank"));
    }

    @DisplayName("User delete created users")
    @ParameterizedTest
    @Order(4)
    @ValueSource(ints = {0,1,2,3,4,5,6,7,8,9})
    public void Users_CreateUser_deleteCreated(int number){
        GoRestService.deleteUser(userIds.get(number))
                .then()
                .statusCode(SC_NO_CONTENT);

        GoRestService.getUserInfo(userIds.get(number))
                .then()
                .statusCode(SC_NOT_FOUND)
                .body("message", equalTo("Resource not found"));
    }
}
