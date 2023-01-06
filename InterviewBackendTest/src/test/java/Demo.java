import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Demo {

    @Test
    public void newLoginReq(){
        Map<String, Object> putRequestMap = new HashMap<>();
        putRequestMap.put("email", "Developer5@gmail.com");
        putRequestMap.put("password", 123456);


        Response response = RestAssured.given().header("Content-Type", "application/json")
                .header("Accept", "application/json").body(putRequestMap).when()
                .post("http://restapi.adequateshop.com/api/authaccount/login");

        assertEquals(response.statusCode(), 200);
        assertEquals(response.contentType(), "application/json; charset=utf-8");

        response.prettyPrint();
        String email = putRequestMap.get("email").toString();
        String email2 = response.path("data.Email").toString();

        assertEquals(email, email2);
    }
}
