import io.restassured.response.Response;
import models.CreateUserModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import services.GoRestService;

import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_UNPROCESSABLE_ENTITY;
import static org.hamcrest.CoreMatchers.*;

public class CreateUserTests {

    int userId;
    String userName;
    String userGender;
    String userEmail;
    String userStatus;

    @Test
    public void Users_CreateUsers_Success(){

        final CreateUserModel createUserModel = new CreateUserModel("Gino Paloma", "Male", "qatest37@test.com", "Active");
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

    //Data preparation for create user scenarios
    @Test
    public void Users_CreateUsers_GetData(){
        Response response = GoRestService.createUser(new CreateUserModel("Jessica Newman", "Female", "qatest38@test.com", "Active"));

        response.prettyPrint();
        userId = response.path("id");
        userName = response.path("name");
        userGender = response.path("gender");
        userEmail = response.path("email");
        userStatus = response.path("status");

    }

    //User can not be created with same email
    @Test
    public void Users_CreateUser_existingEmail(){
        final CreateUserModel createUserModel = new CreateUserModel("Gino Paloma", "Male", "qatest38@test.com", "Active");
        GoRestService.createUser(createUserModel)
                .then()
                .log().all()
                .statusCode(SC_UNPROCESSABLE_ENTITY)
                .body("[0].field", equalTo("email"))
                .body("[0].message", equalTo("has already been taken"));

    }

}
