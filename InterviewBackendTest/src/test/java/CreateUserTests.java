import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import models.CreateUserModel;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.ValueSource;
import services.GoRestService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CreateUserTests {

    static int userId;
    static String userName;
    static String userGender;
    static String userEmail;
    static String userStatus;

    static List<Integer> userIds = new ArrayList<>();

    @Test
    @Order(1)
    public void Users_CreateUsers_Success(){

        final CreateUserModel createUserModel = new CreateUserModel("Gino Paloma", "Male", "qatest68@test.com", "Active");
        GoRestService.createUser(createUserModel)
                .then()
                .log().all()
                .statusCode(SC_CREATED)
                .body("id", notNullValue())
                .body("name", equalTo(createUserModel.getName())) //I refactor according to v2 api version. (data.id or data.name paths are belong to v1 api)
                .body("gender", equalTo(createUserModel.getGender().toLowerCase()))
                .body("email", containsString("@test.com"))
                .body("status", equalTo(createUserModel.getStatus().toLowerCase()));

    }


    @DisplayName("Data preparation for create user scenarios")
    @Test
    @Order(2)
    public void Users_CreateUsers_GetData(){
        Response response = GoRestService.createUser(new CreateUserModel("Jessica Newman", "Female", "qatest69@test.com", "Active"));

        response.prettyPrint();
        userId = response.path("id");
        //System.out.println(userId);
        userName = response.path("name");
        userGender = response.path("gender");
        userEmail = response.path("email");
        userStatus = response.path("status");

    }


    @DisplayName("User can get created user info")
    @Test
    @Order(3)
    public void Users_CreateUser_GetCreated(){
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

    @DisplayName("Schema Validation for User Response")
    @Test
    @Order(4)
    public void Users_CreateUser_GetCreatedSchema(){
        GoRestService.getUserInfo(userId)
                .then()
                .statusCode(SC_OK)
                .and().body(JsonSchemaValidator.matchesJsonSchemaInClasspath("jsonSchemaGetReq.json"));

    }

    @DisplayName("User can be created with CSV resource")
    @ParameterizedTest
    @Order(5)
    @CsvFileSource(resources = "/MOCK_DATA.csv", numLinesToSkip = 1)
    public void Users_CreateUser_MultipleOneShot(String name, String gender, String email, String status){
        Response response = GoRestService.createUser(new CreateUserModel(name, gender, email, status));

        assertEquals(response.statusCode(), SC_CREATED);
        assertEquals(response.contentType(), "application/json; charset=utf-8");
        userIds.add(response.path("id"));
        //System.out.println(userIds.toString());

    }

    @DisplayName("User can not be created with same email")
    @ParameterizedTest
    @Order(6)
    @CsvFileSource(resources = "/MOCK_DATA_same_email.csv", numLinesToSkip = 1)
    public void Users_CreateUser_existingEmail(String name, String gender, String email, String status){
        GoRestService.createUser(new CreateUserModel(name, gender, email, status))
                .then()
                .log().all()
                .statusCode(SC_UNPROCESSABLE_ENTITY)
                .body("[0].field", equalTo("email"))
                .body("[0].message", equalTo("has already been taken"));

    }

    @DisplayName("User delete created users")
    @ParameterizedTest
    @Order(7)
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
