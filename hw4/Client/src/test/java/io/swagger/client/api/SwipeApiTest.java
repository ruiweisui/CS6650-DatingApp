/*
 * twinder
 * CS6650 assignment API
 *
 * OpenAPI spec version: 1.0.0
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */

package io.swagger.client.api;

import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.model.ResponseMsg;
import io.swagger.client.model.SwipeDetails;
import org.junit.Test;
import org.junit.Ignore;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * API tests for SwipeApi
 */
@Ignore
public class SwipeApiTest {

    private final SwipeApi api = new SwipeApi();

    /**
     * 
     *
     * Swipe left or right
     *
     * @throws Exception
     *          if the Api call fails
     */
    @Test
    public void swipeTest() throws Exception {
        SwipeDetails body = new SwipeDetails(); // SwipeDetails | response details
        String leftorright = "leftorright_example"; // String | I like or dislike user

        // TODO: test validations
        String localUrl = "http://localhost:8080/server_war_exploded/hwSui/swipe/%7Bleftorright%7D/";
        String awsUrl = "http://35.87.19.154:8080/server_war/hwSui/swipe/{leftorright}/";
        ApiClient apiClient = new ApiClient();
        apiClient.setBasePath(localUrl);

        try {
            api.swipe(body, leftorright);
        } catch (ApiException e) {
            System.err.println("Exception when calling SwipeApi#swipe");
            e.printStackTrace();
        }
    }
}
