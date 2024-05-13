package com.task09;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.syndicate.deployment.annotations.events.DynamoDbTriggerEventSource;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.annotations.lambda.LambdaUrlConfig;
import com.syndicate.deployment.model.RetentionSetting;
import com.syndicate.deployment.model.TracingMode;
import com.syndicate.deployment.model.lambda.url.AuthType;
import com.syndicate.deployment.model.lambda.url.InvokeMode;
import com.task09.util.OpenMeteoAPI;

@LambdaHandler(lambdaName = "processor",
    roleName = "processor-role",
    isPublishVersion = false,
    logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED,
    tracingMode = TracingMode.Active
)

@LambdaUrlConfig(authType = AuthType.NONE, invokeMode = InvokeMode.BUFFERED)
@DynamoDbTriggerEventSource(batchSize = 10,targetTable = "Weather")
public class Processor implements RequestHandler<Object, Map<String, Object>> {
    public Map<String, Object> handleRequest(Object request, Context context) {
        // Retrieve weather data from the Open-Meteo API
        String weatherData = retrieveWeatherData();

        // Push the weather data to DynamoDB
        pushToDynamoDB(weatherData);

        // Prepare the response
        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", 200);
        response.put("body", "Weather data pushed to DynamoDB successfully");

        return response;
    }

    private String retrieveWeatherData() {
        // Logic to retrieve weather data from Open-Meteo API
        // This part of the code can be implemented based on your specific requirements.
        // For example, using HTTP client libraries to make a request to the API and retrieve the response.
        // Once implemented, return the retrieved weather data.
    	OpenMeteoAPI meteoAPI=new OpenMeteoAPI();
        return meteoAPI.getWeatherForecast();
    }

    private void pushToDynamoDB(String weatherData) {
        // Create DynamoDB client
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
        DynamoDB dynamoDB = new DynamoDB(client);

        // Get DynamoDB table
        Table table = dynamoDB.getTable("cmtr-9e564e24-Weather");


        // Generate UUID for the primary key
        String id = UUID.randomUUID().toString();

        // Create item to be inserted into DynamoDB table
        Item item = new Item()
            .withPrimaryKey("id", id)
            .withString("forecast", weatherData);

        // Insert item into DynamoDB table
        table.putItem(item);


    }
}
