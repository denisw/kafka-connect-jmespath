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

    @Test
    void readmeExample() {
        ConnectJMESPathRuntime runtime = new ConnectJMESPathRuntime();

        Expression<Object> expression = runtime.compile(
                "publishDate.year < `2000`");

        Schema dateSchema = SchemaBuilder.struct()
                .name("Date")
                .field("day", Schema.INT32_SCHEMA)
                .field("month", Schema.INT32_SCHEMA)
                .field("year", Schema.INT32_SCHEMA)
                .build();

        Schema bookSchema = SchemaBuilder.struct()
                .name("Book")
                .field("name", Schema.STRING_SCHEMA)
                .field("author", Schema.STRING_SCHEMA)
                .field("publishDate", dateSchema)
                .build();

        Object book1 = new Struct(bookSchema)
                .put("name", "Design Patterns: Elements of Reusable Object-Oriented Software")
                .put("author", "Erich Gamma, Ralph Johnson, John Vlissides, Richard Helm")
                .put("publishDate", new Struct(dateSchema)
                        .put("day", 31)
                        .put("month", 10)
                        .put("year", 1994));
        Object book2 =  new Struct(bookSchema)
                .put("name", "Designing Data-Intensive Applications")
                .put("author", "Martin Kleppmann")
                .put("publishDate", new Struct(dateSchema)
                        .put("day", 25)
                        .put("month", 4)
                        .put("year", 2015));

        Object result1 = expression.search(book1);
        Object result2 = expression.search(book2);

        assertEquals(true, result1, "1994 < 2000");
        assertEquals(false, result2, "2015 > 2000");
    }
}
