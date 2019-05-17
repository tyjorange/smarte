package com.rogy.smarte.fsu;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

import java.time.LocalDateTime;

public class TimeoutHandler extends ChannelDuplexHandler {
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (evt instanceof IdleStateEvent) {
			LocalDateTime now = LocalDateTime.now();
			IdleStateEvent e = (IdleStateEvent) evt;
			if (e.state() == IdleState.READER_IDLE) {
				CollectorInfo collectorInfo = CollectorChannelHandler.getCollectorInfoOfCtx(ctx);
				if(collectorInfo != null) {
					// 添加离线记录
					CollectorChannelHandler.collectorOnline(collectorInfo.getCollectID(), false, now, -5);
				}
				ctx.close();
			}
		}
	}
}
