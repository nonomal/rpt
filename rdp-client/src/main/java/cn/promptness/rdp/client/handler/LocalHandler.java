package cn.promptness.rdp.client.handler;

import cn.promptness.rdp.base.config.ClientConfig;
import cn.promptness.rdp.base.config.RemoteConfig;
import cn.promptness.rdp.base.protocol.Message;
import cn.promptness.rdp.base.protocol.MessageType;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 实际内网连接处理器
 */
public class LocalHandler extends SimpleChannelInboundHandler<byte[]> {

    private static final Logger logger = LoggerFactory.getLogger(LocalHandler.class);

    private final Channel channel;
    private final ClientConfig clientConfig;

    public LocalHandler(Channel channel, ClientConfig clientConfig) {
        this.channel = channel;
        this.clientConfig = clientConfig;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        RemoteConfig remoteConfig = clientConfig.getConfig().get(0);
        logger.info("客户端建立本地连接成功,{}:{}", remoteConfig.getLocalIp(), remoteConfig.getLocalPort());
        ctx.channel().config().setAutoRead(false);
        send(MessageType.TYPE_CONNECTED, new byte[0]);
        ctx.channel().config().setAutoRead(true);
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, byte[] bytes) throws Exception {
        RemoteConfig remoteConfig = clientConfig.getConfig().get(0);
        logger.debug("收到本地{}:{}的数据,数据量为:{}字节", remoteConfig.getLocalIp(), remoteConfig.getLocalPort(), bytes.length);
        send(MessageType.TYPE_DATA, bytes);
    }


    /**
     * 连接断开
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        RemoteConfig remoteConfig = clientConfig.getConfig().get(0);
        logger.info("客户端本地连接断开,{}:{}", remoteConfig.getLocalIp(), remoteConfig.getLocalPort());
        ctx.channel().config().setAutoRead(true);
        send(MessageType.TYPE_DISCONNECTED, new byte[0]);
    }

    /**
     * 连接异常
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.channel().close();
    }

    private void send(MessageType type, byte[] data) {
        Message message = new Message();
        message.setType(type);
        message.setClientConfig(clientConfig);
        message.setData(data);
        // 收到内网服务器响应后返回给服务器端
        channel.writeAndFlush(message);
    }

}
