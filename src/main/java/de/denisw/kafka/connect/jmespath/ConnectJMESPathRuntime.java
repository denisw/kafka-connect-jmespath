package de.denisw.kafka.connect.jmespath;

import io.burt.jmespath.BaseRuntime;
import io.burt.jmespath.JmesPathType;
import io.burt.jmespath.jcf.JsonParser;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Struct;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A {@link io.burt.jmespath.Adapter JMESPath runtime adapter} for the
 * Kafka Connect data types.
 */
public class ConnectJMESPathRuntime extends BaseRuntime<Object> {

    @Override
    public Object parseString(String str) {
        return JsonParser.fromString(str, this);
    }

    @Override
    public JmesPathType typeOf(Object value) {
        if (value == null) {
            return JmesPathType.NULL;
        } else if (value instanceof Boolean) {
            return JmesPathType.BOOLEAN;
        } else if (value instanceof Number) {
            return JmesPathType.NUMBER;
        } else if (value instanceof String) {
            return JmesPathType.STRING;
        } else if (value instanceof List) {
            return JmesPathType.ARRAY;
        } else if (value instanceof Map || value instanceof Struct) {
            return JmesPathType.OBJECT;
        } else {
            throw new IllegalStateException("Unexpected value type:" + value.getClass());
        }
    }

    @Override
    public boolean isTruthy(Object value) {
        if (value == null) {
            return false;
        } else if (value instanceof Boolean) {
            return Boolean.TRUE.equals(value);
        } else if (value instanceof Number) {
            return true;
        } else if (value instanceof String) {
            return !((String) value).isEmpty();
        } else if (value instanceof List) {
            return !((Collection<?>) value).isEmpty();
        } else if (value instanceof Map) {
            return !((Map<?, ?>) value).isEmpty();
        } else if (value instanceof Struct) {
            return true;
        } else {
            throw new IllegalStateException("Unexpected value type:" + value.getClass());
        }
    }

    @Override
    public Number toNumber(Object value) {
        if (value instanceof Number) {
            return (Number) value;
        } else {
            return null;
        }
    }

    @Override
    public String toString(Object value) {
        if (value instanceof String) {
            return (String) value;
        } else {
            return toJson(value);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Object> toList(Object value) {
        if (value instanceof List) {
            return (List<Object>) value;
        } else if (value instanceof Map) {
            Map<Object, Object> map = (Map<Object, Object>) value;
            return new ArrayList<>(map.values());
        } else if (value instanceof Struct) {
            Struct struct = (Struct) value;
            List<Object> fieldValues = new ArrayList<>();
            for (Field field : struct.schema().fields()) {
                fieldValues.add(struct.get(field));
            }
            return fieldValues;
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public Object createNull() {
        return null;
    }

    @Override
    public Object createBoolean(boolean b) {
        return b;
    }

    @Override
    public Object createNumber(double n) {
        return n;
    }

    @Override
    public Object createNumber(long n) {
        return n;
    }

    @Override
    public Object createString(String str) {
        return str;
    }

    @Override
    public Object createArray(Collection<Object> elements) {
        return new ArrayList<>(elements);
    }

    @Override
    public Object createObject(Map<Object, Object> obj) {
        return obj;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object getProperty(Object value, Object name) {
        if (value instanceof Map) {
            return ((Map<Object, Object>) value).get(name);
        } else if (value instanceof Struct) {
            return ((Struct) value).get((String) name);
        } else {
            return null;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<Object> getPropertyNames(Object value) {
        if (value instanceof Map) {
            return ((Map<Object, Object>) value).keySet();
        } else if (value instanceof Struct) {
            return ((Struct) value).schema()
                    .fields()
                    .stream()
                    .map(Field::name)
                    .collect(Collectors.toList());
        } else {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private String toJson(Object value) {
        if (value == null) {
            return "null";
        } else if (value instanceof String) {
            String escaped = ((String) value).replace("\"", "\\\"");
            return String.format("\"%s\"", escaped);
        } else if (value instanceof List) {
            return toArrayJson((List<Object>) value);
        } else if (value instanceof Map) {
            return toObjectJson((Map<String, Object>) value);
        } else {
            return value.toString();
        }
    }

    private String toArrayJson(List<Object> list) {
        StringBuilder sb = new StringBuilder();
        sb.append('[');

        Iterator<Object> it = list.iterator();
        while (it.hasNext()) {
            sb.append(toJson(it.next()));
            if (it.hasNext()) {
                sb.append(",");
            }
        }

        sb.append(']');
        return sb.toString();
    }

    private String toObjectJson(Map<String, Object> list) {
        StringBuilder sb = new StringBuilder();
        sb.append('{');

        Iterator<Map.Entry<String, Object>> it = list.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Object> entry = it.next();
            sb.append(toJson(entry.getKey()));
            sb.append(':');
            sb.append(toJson(entry.getValue()));
            if (it.hasNext()) {
                sb.append(",");
            }
        }

        sb.append('}');
        return sb.toString();
    }
}
