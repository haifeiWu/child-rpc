package cn.whforever.core.serialize.impl;

import cn.whforever.core.serialize.Serializer;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

public class JacksonSerializer extends Serializer {
    private final static ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public <T> byte[] serialize(T obj) {
        try {
            return objectMapper.writeValueAsBytes(obj);
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    /** string --> bean、Map、List(array) */
    @Override
    public <T> Object deserialize(byte[] bytes, Class<T> clazz) {
        try {
            return objectMapper.readValue(bytes,clazz);
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }
}
