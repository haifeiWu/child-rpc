package cn.whforever.core.coedc.netty;

import cn.whforever.core.serialize.AbstractSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class NettyDecoder extends ByteToMessageDecoder {

    private Class<?> genericClass;
    private AbstractSerializer serializer;

    public NettyDecoder(Class<?> genericClass, AbstractSerializer serializer) {
        this.genericClass = genericClass;
        this.serializer = serializer;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        if (byteBuf.readableBytes() < 4) {
            return;
        }

        byteBuf.markReaderIndex();
        int dataLength = byteBuf.readInt();
        if (dataLength < 0) {
            channelHandlerContext.close();
        }

        if (byteBuf.readableBytes() < dataLength) {
            byteBuf.resetReaderIndex();
            return;
        }

        try {
            byte[] data = new byte[dataLength];
            byteBuf.readBytes(data);
            Object object = serializer.deserialize(data,genericClass);
            list.add(object);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
