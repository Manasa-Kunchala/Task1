package com.task08.util;

import com.amazonaws.services.lambda.runtime.Context;

public class LambdaFunctionHandler {
	 public String handleRequest(Object input, Context context) {
	        OpenMeteoAPI openMeteoAPI = new OpenMeteoAPI();
	        return openMeteoAPI.getWeatherForecast();
	    }

}
