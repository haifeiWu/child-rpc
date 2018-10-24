package cn.whforever.core.coedc.netty;

import cn.whforever.core.serialize.AbstractSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author wuhf
 * @Date 2018/8/31 19:18
 **/
public class NettyEncoder extends MessageToByteEncoder<Object> {

    private Class<?> genericClass;
    private AbstractSerializer serializer;

    public NettyEncoder(Class<?> genericClass,AbstractSerializer serializer) {
        this.serializer = serializer;
        this.genericClass = genericClass;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object object, ByteBuf byteBuf) throws Exception {
        if (genericClass.isInstance(object)) {
            byte[] data = serializer.serialize(object);
            byteBuf.writeInt(data.length);
            byteBuf.writeBytes(data);
        }
    }
}
