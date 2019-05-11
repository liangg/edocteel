package com.css.kitchen.util;

import com.css.kitchen.Order;
import java.util.List;
import java.util.Optional;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * An order util that convert json data to Order
 */
public class OrderReader {

    public static List<Order> toOrder(String orderJsonArray) {
        // JSONParser parser = new JSONParser();
        //JSONArray jsonArray = new JSONArray(orderJsonArray);
        return null;
    }

    // Validate order json data format, and create Order only if all parameters are valid
    public static Optional<Order> toOrder(JSONObject orderJson) {
        final String name = (String) orderJson.get(Order.ORDER_NAME);
        final String temp = (String) orderJson.get(Order.ORDER_TEMP);
        final Integer life = (Integer) orderJson.get(Order.ORDER_SHELFLIFE);
        final Double decay = (Double) orderJson.get(Order.ORDER_DECAYRATE);
        if (name == null || temp == null || life == null || decay == null) {
            // FIXME: log metrics
            return Optional.empty();
        }
        Order.Temperature temperature = Order.temperatureMap.get(temp.toLowerCase());
        if (temperature == null) {
            return Optional.empty();
        }
        return Optional.of(Order.builder()
                .name(name)
                .temperature(temperature)
                .shelfLife(life.intValue())
                .decayRate(decay.doubleValue())
                .build());
    }
}
