package com.rogy.smarte.fsu;

import io.netty.channel.ChannelHandlerContext;

/**
 * 集中器定时心跳。
 */
public class CollectorHeartbeatRunnable implements Runnable {
	private ChannelHandlerContext ctx = null;
	
	public CollectorHeartbeatRunnable(ChannelHandlerContext ctx) {
		this.ctx = ctx;
	}

	@Override
	public void run() {
		try {
			VirtualFsuController.collectorHeartbeat(ctx);
		} catch (Exception e) {
			//e.printStackTrace();
		}
	}

}
