package cn.promptness.rpt.client.handler;

import cn.promptness.rpt.base.coder.HttpEncoder;
import cn.promptness.rpt.base.config.ClientConfig;
import cn.promptness.rpt.base.utils.Constants;
import cn.promptness.rpt.base.protocol.Message;
import cn.promptness.rpt.base.protocol.MessageType;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.internal.EmptyArrays;

import java.util.Arrays;
import java.util.List;

public class ReceiveHandler extends SimpleChannelInboundHandler<FullHttpResponse> {

    private static final List<Integer> REDIRECT_STATUS = Arrays.asList(HttpResponseStatus.FOUND.code(), HttpResponseStatus.SEE_OTHER.code());

    private final Channel clientChannel;
    private final ClientConfig clientConfig;

    public ReceiveHandler(Channel clientChannel, ClientConfig clientConfig) {
        this.clientChannel = clientChannel;
        this.clientConfig = clientConfig;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.channel().config().setAutoRead(false);
        send(MessageType.TYPE_CONNECTED, EmptyArrays.EMPTY_BYTES);
        ctx.channel().config().setAutoRead(true);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.channel().close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ctx.channel().config().setAutoRead(true);
        send(MessageType.TYPE_DISCONNECTED, EmptyArrays.EMPTY_BYTES);
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpResponse response) throws Exception {

        HttpResponseStatus status = response.status();
        if (REDIRECT_STATUS.contains(status.code())) {
            String location = String.valueOf(response.headers().get(HttpHeaderNames.LOCATION));
            int index = location.indexOf("/", 8);
            location = "http" + "://" + clientConfig.getConfig().get(0).getDomain() + location.substring(index);
            response.headers().set(HttpHeaderNames.LOCATION, location);
        }
        response.headers().set(HttpHeaderNames.SERVER, Constants.RPT);
        List<Object> encode = HttpEncoder.encode(ctx, response);
        for (Object obj : encode) {
            ByteBuf buf = (ByteBuf) obj;
            byte[] data = new byte[buf.readableBytes()];
            buf.readBytes(data);
            buf.release();

            send(MessageType.TYPE_DATA, data);
        }
    }

    private void send(MessageType type, byte[] data) {
        Message message = new Message();
        message.setType(type);
        message.setClientConfig(clientConfig);
        message.setData(data);
        // 收到内网服务器响应后返回给服务器端
        clientChannel.writeAndFlush(message);
    }
}