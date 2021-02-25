package de.denisw.kafka.connect.jmespath;

import io.burt.jmespath.Expression;
import io.burt.jmespath.parser.ParseException;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigException;
import org.apache.kafka.connect.connector.ConnectRecord;
import org.apache.kafka.connect.transforms.predicates.Predicate;

import java.util.Map;

/**
 * A {@link Predicate Kafka Connect predicate} which applies a JMESPath
 * query to the record key or value, and returns true if the query
 * evaluates to a JSON value that is "truthy" (every value other then
 * null, false, or an empty string, array or object).
 *
 * @see Key
 * @see Value
 * @see <a href="https://jmespath.org/">JMESPath</a>
 */
public abstract class MatchesJMESPath<R extends ConnectRecord<R>> implements Predicate<R> {

    public static final ConfigDef CONFIG_DEF = new ConfigDef().define(
            "query",
            ConfigDef.Type.STRING,
            ConfigDef.NO_DEFAULT_VALUE,
            new ConfigDef.NonEmptyString(),
            ConfigDef.Importance.HIGH,
            "The JMESPath query to evaluate for each record.");

    private final ConnectJMESPathRuntime runtime = new ConnectJMESPathRuntime();
    private Expression<Object> expression;

    @Override
    public ConfigDef config() {
        return CONFIG_DEF;
    }

    @Override
    public void configure(Map<String, ?> configs) {
        String query = (String) configs.get("query");
        try {
            expression = runtime.compile(query);
        } catch (ParseException e) {
            throw new ConfigException("query", query, e.getMessage());
        }
    }

    @Override
    public boolean test(R record) {
        Object result = expression.search(dataToMatch(record));
        return runtime.isTruthy(result);
    }

    @Override
    public void close() {
        // Nothing to do
    }

    protected abstract Object dataToMatch(R record);

    /**
     * A {@link MatchesJMESPath} predicate that applies the query to
     * the record's key.
     */
    public static class Key<R extends ConnectRecord<R>> extends MatchesJMESPath<R> {
        @Override
        protected Object dataToMatch(R record) {
            return record.key();
        }
    }

    /**
     * A {@link MatchesJMESPath} predicate that applies the query to
     * the record's value.
     */
    public static class Value<R extends ConnectRecord<R>> extends MatchesJMESPath<R> {
        @Override
        protected Object dataToMatch(R record) {
            return record.value();
        }
    }
}
