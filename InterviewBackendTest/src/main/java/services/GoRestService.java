package services;

import io.restassured.response.Response;
import models.CreateUserModel;


public class GoRestService extends BaseService {

    public static Response createUser(final CreateUserModel createUserModel){
        return defaultRequestSpecification()
                .body(createUserModel)
                .when()
                .post("/public/v2/users");
    }

    public static Response updateUserInfo(final CreateUserModel createUserModel, int userId){
        return defaultRequestSpecification()
                .body(createUserModel)
                .when()
                .put("/public/v2/users" + userId);
    }

    public static Response updateUserStatus(final CreateUserModel createUserModel, int userId){
        return defaultRequestSpecification()
                .body(createUserModel)
                .when()
                .patch("/public/v2/users" + userId);
    }
}
