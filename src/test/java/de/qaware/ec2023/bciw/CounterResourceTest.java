

package de.qaware.ec2023.bciw;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class CounterResourceTest {

    @Test
    public void testGetCounter() {
        given()
                .when().get("/counter")
                .then()
                .statusCode(200)
                .body("value", equalTo(0));
    }


    @Test
    public void testIncrementAndDecrement() {
        given()
                .when().get("/counter")
                .then()
                .statusCode(200)
                .body("value", equalTo(0));

        given().when().post("/counter/increment").then().statusCode(204);

        given()
                .when().get("/counter")
                .then()
                .statusCode(200)
                .body("value", equalTo(1));

        given().when().post("/counter/decrement").then().statusCode(204);

        given()
                .when().get("/counter")
                .then()
                .statusCode(200)
                .body("value", equalTo(0));
    }

}
