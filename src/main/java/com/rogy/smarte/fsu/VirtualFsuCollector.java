package com.rogy.smarte.fsu;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.AdaptiveRecvByteBufAllocator;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

/**
 * 集中器采集服务类。
 */
public class VirtualFsuCollector implements Runnable {
    private final static Logger logger = LoggerFactory.getLogger(VirtualFsuCollector.class);

    public static final int FSU_PORT = 55555;

    private volatile boolean stopFlag = false; // 结束标记

    public void stop() {
        stopFlag = true;
    }

    /**
     * 服务监听端口
     **/
    private int port;

    public int getPort() {
        return port;
    }

    public VirtualFsuCollector() {
        this(FSU_PORT);
    }

    public VirtualFsuCollector(int port) {
        this.port = port;
    }

    @Override
    public void run() {
//        System.out.printf("[%s] Collector service start...\n", LocalDateTime.now());
        logger.info(" Collector service start...");
        // 稍微等待
        try {
            Thread.sleep(5 * 1000);
        } catch (InterruptedException ie) {
        }

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            // 设置启动辅助类
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class) // 设置channel类型
                    .childOption(ChannelOption.RCVBUF_ALLOCATOR, new AdaptiveRecvByteBufAllocator(128, 2048, 65536))
                    .childHandler(new ChildChannelHandler()); // 选择执行handler
            // 阻塞等待服务器完全启动
            // ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
            serverBootstrap.bind(port).sync();

            // 使用下面语句将使线程阻塞直至Socket服务线程结束
            // channelFuture.channel().closeFuture().sync();
            // 我们不阻塞线程，以检测停止标志
            while (!stopFlag) {
                try {
                    Thread.sleep(1 * 1000);
                } catch (InterruptedException ie) {
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
        //System.out.printf("[%s] Collector service stop\n", LocalDateTime.now());
        logger.info(" Collector service stop...");
    }
}
