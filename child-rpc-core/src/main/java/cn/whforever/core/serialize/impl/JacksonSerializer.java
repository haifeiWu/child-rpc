package cn.whforever.core.serialize.impl;

import cn.whforever.core.serialize.Serializer;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * json serialize
 *
 * @author haifeiwugm.@gmail.com
 */
public class JacksonSerializer extends Serializer {
    private final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public <T> byte[] serialize(T obj) {
        try {
            return OBJECT_MAPPER.writeValueAsBytes(obj);
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    /**
     * string --> bean、Map、List(array)
     */
    @Override
    public <T> Object deserialize(byte[] bytes, Class<T> clazz) {
        try {
            OBJECT_MAPPER.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
            return OBJECT_MAPPER.readValue(bytes, clazz);
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }
}
