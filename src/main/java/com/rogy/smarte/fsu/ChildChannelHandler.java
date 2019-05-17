package com.rogy.smarte.fsu;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 *
 */
public class ChildChannelHandler extends ChannelInitializer<SocketChannel> {
	private static final CollectorChannelHandler collectorChannelHandler = new CollectorChannelHandler();
	private static final int TIMEOUT_MINS = 3;
	
	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ch.pipeline().addLast("Idle", new IdleStateHandler(TIMEOUT_MINS, 0, 0, TimeUnit.MINUTES));	// Timeout
		ch.pipeline().addLast("Timeout", new TimeoutHandler());
		ch.pipeline().addLast("Decoder", new MessageDecoder());
		ch.pipeline().addLast("Handler", collectorChannelHandler);
	}

}
