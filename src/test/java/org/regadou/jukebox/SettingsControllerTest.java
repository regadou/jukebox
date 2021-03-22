package org.regadou.jukebox;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;

@QuarkusTest
public class SettingsControllerTest {

    @Test
    public void testSettingsEndpointNoSettingIdGiven() {
        given()
            .when().get("/settings")
            .then().statusCode(400);
    }

    @Test
    public void testGreetingEndpointInvalidSettingIdGiven() {
        given()
            .when().get("/settings?settingId=invalid")
            .then().statusCode(404);
    }   
}
