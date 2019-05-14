package com.css.kitchen.util;

import com.css.kitchen.Order;

import java.io.FileReader;
import java.io.IOException;
import java.lang.Integer;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An order util that convert json data to Order
 */
public class OrderReader {
    private static Logger logger = LoggerFactory.getLogger(OrderReader.class);

    public static List<Order> toOrderList(JSONArray orderJsonArray) {
        Stream<Optional<Order>> ss = orderJsonArray.stream()
                .map(jo -> readOrder((JSONObject) jo));
        List<Optional<Order>> orderOptionals = ss.collect(Collectors.toList());
        List<Order> result = orderOptionals.stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
        return result;
    }

    // Validate order json data format, and create Order only if all parameters are valid
    public static Optional<Order> readOrder(JSONObject orderJson) {
        final Object name = orderJson.get(Order.ORDER_NAME);
        final Object temp = orderJson.get(Order.ORDER_TEMP);
        final Object life = orderJson.get(Order.ORDER_SHELFLIFE); // should be Long
        final Object decay = orderJson.get(Order.ORDER_DECAYRATE); // should be Double
        if (name == null || temp == null || life == null || decay == null ||
                !(name instanceof String) || !(temp instanceof String) ||
                !(life instanceof Long) || !(decay instanceof Double))
        {
            // FIXME: log metrics
            logger.error("invalid order attributes");
            return Optional.empty();
        }
        Order.Temperature temperature = Order.temperatureMap.get(((String) temp).toLowerCase());
        if (temperature == null) {
            logger.error("invalid order temperature value");
            return Optional.empty();
        }
        // FIXME: count valid order
        return Optional.of(Order.builder()
                .name((String) name)
                .temperature(temperature)
                .shelfLife(((Long) life).intValue())
                .decayRate(((Double) decay).doubleValue())
                .build());
    }

    // Given a file path, read the orders json file into a list of Orders
    public static List<Order> readOrdersJson(String orderJsonFile) {
        JSONArray ordersJsonArray = null;
        try {
            JSONParser parser = new JSONParser();
            FileReader reader = new FileReader(orderJsonFile);
            ordersJsonArray = (JSONArray) parser.parse(reader);
        } catch (IOException ex) {
            logger.error("error reading orders json file");
            ex.printStackTrace();
        } catch (ParseException e) {
            logger.error("error parsing json file");
            e.printStackTrace();
        }
        return ordersJsonArray != null ? toOrderList(ordersJsonArray) : Collections.<Order>emptyList();
    }
}
