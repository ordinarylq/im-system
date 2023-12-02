package com.lq.im.tcp.server;

import com.lq.im.codec.config.BootstrapConfig;
import com.lq.im.codec.config.MessageDecoder;
import com.lq.im.codec.config.MessageEncoder;
import com.lq.im.tcp.server.handler.HeartBeatHandler;
import com.lq.im.tcp.server.handler.NettyServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ImServer {

    private BootstrapConfig.TcpConfig config;
    private NioEventLoopGroup bossGroup;
    private NioEventLoopGroup workerGroup;
    private ServerBootstrap serverBootstrap;

    public ImServer(BootstrapConfig.TcpConfig config) {
        this.config = config;
        this.bossGroup = new NioEventLoopGroup(config.getBossThreadSize());
        this.workerGroup = new NioEventLoopGroup(config.getWorkerThreadSize());
        this.serverBootstrap = new ServerBootstrap();
        this.serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                /*
                存放已完成三次握手的请求的队列的最大长度
                The backlog parameter is the maximum number of pending connections on the socket.
                如果未设置或者设置的值小于1，则为默认值200(windows), 128(Linux)
                如果队列已满，则新的连接请求将被拒绝
                 */
                .option(ChannelOption.SO_BACKLOG, 10240)
                /*
                TCP断开连接的四次挥手，主动关闭连接的一方的状态从FIN_WAIT_1->FIN_WAIT_2->TIME_WAIT--一段时间-->CLOSED
                在这一段时间内，对应的socket是没有办法完成bind()的。
                设置SO_REUSEADDR后，不需要等待一段时间也可以完成bind().
                PS: 一段时间指2个MSL(Max Segment Lifetime)
                 */
                .option(ChannelOption.SO_REUSEADDR, true)
                /*
                TCP默认启用了Nagel算法，该算法会通过将小包累积，到达threshold后才会发送出去，这样提高了网络利用率，但是增加了延时。
                对于即时通信系统来说，低延时是有必要的。
                开启TCP_NODELAY,会禁用Nagel算法.
                 */
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new MessageDecoder());
                        pipeline.addLast(new MessageEncoder());
//                        pipeline.addLast(new IdleStateHandler(0, 0, 10));
                        pipeline.addLast(new HeartBeatHandler(config.getTimeout()));
                        pipeline.addLast(new NettyServerHandler(config.getBrokerId()));
                    }
                });
    }

    public void start() {
        this.serverBootstrap.bind(this.config.getTcpPort());
    }
}
