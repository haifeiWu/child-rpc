package cn.whforever.core.serialize;

import cn.whforever.core.serialize.impl.HessianAbstractSerializer;
import cn.whforever.core.serialize.impl.JacksonAbstractSerializer;

/**
 * @author wuhf
 * @Date 2018/8/31 19:46
 **/
public abstract class AbstractSerializer {
    public abstract <T> byte[] serialize(T obj);
    public abstract <T> Object deserialize(byte[] bytes, Class<T> clazz);

    public enum SerializeEnum {

        /**
         * hession 序列化.
         */
        HESSIAN(new HessianAbstractSerializer()),
//        PROTOSTUFF(new ProtostuffSerializer()),

        /**
         * json 序列化.
         */
        JSON(new JacksonAbstractSerializer());
//
        public final AbstractSerializer serializer;
        private SerializeEnum (AbstractSerializer serializer) {
            this.serializer = serializer;
        }
        public static SerializeEnum match(String name, SerializeEnum defaultSerializer){
            for (SerializeEnum item : SerializeEnum.values()) {
                if (item.name().equals(name)) {
                    return item;
                }
            }
            return defaultSerializer;
        }
    }
}
