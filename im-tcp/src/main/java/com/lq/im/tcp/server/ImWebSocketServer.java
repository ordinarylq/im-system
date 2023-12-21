package com.lq.im.tcp.server;

import com.lq.im.codec.config.BootstrapConfig;
import com.lq.im.tcp.server.handler.NettyServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ImWebSocketServer {
    private BootstrapConfig.TcpConfig config;
    private NioEventLoopGroup bossGroup;
    private NioEventLoopGroup workerGroup;
    private ServerBootstrap serverBootstrap;

    public ImWebSocketServer(BootstrapConfig.TcpConfig config) {
        this.config = config;
        this.bossGroup = new NioEventLoopGroup(config.getBossThreadSize());
        this.workerGroup = new NioEventLoopGroup(config.getWorkerThreadSize());
        this.serverBootstrap = new ServerBootstrap();
        this.serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 10240)
                .option(ChannelOption.SO_REUSEADDR, true)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        // http请求解码、响应编码器
                        pipeline.addLast("http-codec", new HttpServerCodec());
                        // 写大数据流的支持
                        pipeline.addLast("http-chunked", new ChunkedWriteHandler());
                        pipeline.addLast("aggregator", new HttpObjectAggregator(65535));
                        pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));
                        // todo WebScoketMessageDecoder, WebSocketMessageEncoder
                        pipeline.addLast(new NettyServerHandler(config.getBrokerId(), config.getLogicUrl()));
                    }
                });
    }

    public void start() {
        this.serverBootstrap.bind(this.config.getWebsocketPort());
    }

}
