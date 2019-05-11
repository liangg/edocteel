package com.css.kitchen.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.css.kitchen.Order;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import java.util.Optional;
import org.junit.Test;

public class OrderUtilTest {
    @Test
    public void testJsonObjectToOrder() {
        final JSONObject orderJson = new JSONObject();
        orderJson.put(Order.ORDER_NAME, "Ramen");
        orderJson.put(Order.ORDER_TEMP, "hot");
        orderJson.put(Order.ORDER_SHELFLIFE, 600);
        orderJson.put(Order.ORDER_DECAYRATE, 0.45);
        Optional<Order> orderOptional = OrderReader.toOrder(orderJson);
        assertTrue(orderOptional.isPresent());
        final Order order = orderOptional.get();
        assertEquals(order.getName(), "Ramen");
        assertEquals(order.getTemperature(), Order.Temperature.Hot);
        assertEquals(order.getShelfLife(), 600);
        assertEquals(Double.valueOf(order.getDecayRate()), Double.valueOf(0.45));

        final JSONObject orderJson2 = new JSONObject();
        orderJson2.put(Order.ORDER_NAME, "Sushi dragonroll");
        orderJson2.put(Order.ORDER_TEMP, "Cold");
        orderJson2.put(Order.ORDER_SHELFLIFE, 900);
        orderJson2.put(Order.ORDER_DECAYRATE, 0.15);
        orderOptional = OrderReader.toOrder(orderJson2);
        assertTrue(orderOptional.isPresent());
        final Order order2 = orderOptional.get();
        assertEquals(order2.getName(), "Sushi dragonroll");
        assertEquals(order2.getTemperature(), Order.Temperature.Cold);
        assertEquals(order2.getShelfLife(), 900);
        assertEquals(Double.valueOf(order2.getDecayRate()), Double.valueOf(0.15));

        // should be empty due to missing temperature
        final JSONObject orderJson3 = new JSONObject();
        orderJson3.put(Order.ORDER_NAME, "Mochi icecream");
        orderJson3.put(Order.ORDER_SHELFLIFE, 300);
        orderJson3.put(Order.ORDER_DECAYRATE, 0.5);
        orderOptional = OrderReader.toOrder(orderJson3);
        assertFalse(orderOptional.isPresent());

        // should be empty due to missing shelfLife
        final JSONObject orderJson4 = new JSONObject();
        orderJson4.put(Order.ORDER_NAME, "Mochi icecream");
        orderJson4.put(Order.ORDER_TEMP, "FROZEN");
        orderJson4.put(Order.ORDER_DECAYRATE, 0.5);
        orderOptional = OrderReader.toOrder(orderJson4);
        assertFalse(orderOptional.isPresent());
    }
}
