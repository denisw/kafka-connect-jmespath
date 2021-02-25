package de.denisw.kafka.connect.jmespath;

import io.burt.jmespath.Expression;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JMESPathTest {

    @Test
    void basic() {
        ConnectJMESPathRuntime runtime = new ConnectJMESPathRuntime();

        Expression<Object> expression = runtime.compile(
                "locations[?state == 'WA'].name | sort(@) | {WashingtonCities: join(', ', @)}");

        Schema locationSchema = SchemaBuilder.struct()
                .name("Location")
                .field("name", Schema.STRING_SCHEMA)
                .field("state", Schema.STRING_SCHEMA)
                .build();

        List<Object> locations = Arrays.asList(
                new Struct(locationSchema)
                        .put("name", "Seattle")
                        .put("state", "WA"),
                new Struct(locationSchema)
                        .put("name", "New York")
                        .put("state", "NY"),
                new Struct(locationSchema)
                        .put("name", "Bellevue")
                        .put("state", "WA"),
                new Struct(locationSchema)
                        .put("name", "Olympia")
                        .put("state", "WA"));

        Map<Object, Object> input = new HashMap<>();
        input.put("locations", locations);

        Object result = expression.search(input);

        Map<Object, Object> expected = new HashMap<>();
        expected.put("WashingtonCities", "Bellevue, Olympia, Seattle");
        assertEquals(expected, result);
    }
}
