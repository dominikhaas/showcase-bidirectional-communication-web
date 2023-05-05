

package de.qaware.ec2023.bciw;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class CounterResourceTest {

    @Test
    public void testHelloEndpoint() {
        given()
                .when().get("/counter")
                .then()
                .statusCode(200)
                .body("value", equalTo(0));
    }

}
