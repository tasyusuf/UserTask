package services;

import io.restassured.response.Response;
import models.CreateUserModel;
import models.UpdateUserModel;


public class GoRestService extends BaseService {

    public static Response createUser(final CreateUserModel createUserModel){
        return defaultRequestSpecification()
                .body(createUserModel)
                .when()
                .post("/public/v2/users");
    }

    public static Response getUserInfo(int userId){
        return defaultRequestSpecification()
                .when()
                .get("/public/v2/users/" + userId);
    }

    public static Response updateUserInfo(final UpdateUserModel updateUserModel, int userId){
        return defaultRequestSpecification()
                .body(updateUserModel)
                .when()
                .put("/public/v2/users/" + userId);
    }

    public static Response updateUserStatus(final UpdateUserModel updateUserModel, int userId){
        return defaultRequestSpecification()
                .body(updateUserModel)
                .when()
                .patch("/public/v2/users/" + userId);
    }

    public static Response deleteUser(int userId){
        return defaultRequestSpecification()
                .when()
                .delete("/public/v2/users/" + userId);
    }
}
