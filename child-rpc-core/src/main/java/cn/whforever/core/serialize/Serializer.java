package cn.whforever.core.serialize;

import cn.whforever.core.serialize.impl.HessianSerializer;
import cn.whforever.core.serialize.impl.JacksonSerializer;
import cn.whforever.core.serialize.impl.ProtostuffSerializer;

/**
 * @author wuhf
 * @Date 2018/8/31 19:46
 **/
public abstract class Serializer {
    public abstract <T> byte[] serialize(T obj);

    public abstract <T> Object deserialize(byte[] bytes, Class<T> clazz);

    public enum SerializeEnum {

        /**
         * hession 序列化.
         */
        HESSIAN(new HessianSerializer()),
        PROTOSTUFF(new ProtostuffSerializer()),

        /**
         * json 序列化.
         */
        JSON(new JacksonSerializer());

        public final Serializer serializer;

        private SerializeEnum(Serializer serializer) {
            this.serializer = serializer;
        }

        public static SerializeEnum match(String name, SerializeEnum defaultSerializer) {
            for (SerializeEnum item : SerializeEnum.values()) {
                if (item.name().equals(name)) {
                    return item;
                }
            }
            return defaultSerializer;
        }
    }
}
