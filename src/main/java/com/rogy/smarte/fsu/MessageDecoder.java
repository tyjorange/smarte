package com.rogy.smarte.fsu;

import com.rogy.smarte.fsu.message.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class MessageDecoder extends ByteToMessageDecoder {

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		int rbs;	// 可读数据长度。
		int rpos;	// 读指针位置。
		int startpos = -1;	// 起始标识的位置。
		int endpos = -1;	// 结束标识的位置。
		int len;			// 数据包长度。
		int bb;
		long readCount = 0;		// 总的处理字节数。
		Message message = null;
//		LocalDateTime now = LocalDateTime.now();
		while(true) {
			rpos = in.readerIndex();
			// 1.起始标识。
			bb = in.bytesBefore(Message.DELIMITER);
			if(bb < 0) {	// 没有起始标识。
				if((rbs = in.readableBytes()) > 0) {
//					printBuf(in, rpos, rbs);
					// 丢弃全部内容。
					readCount += rbs;
					in.skipBytes(rbs);
//					System.out.printf("[%s] MessageDecoder : %s Header DELIMITER miss. %s bytes drop.\n", 
//							now,
//							ctx.channel().remoteAddress().toString(),
//							rbs);
					message = new Message();
					message.setId(Message.ID_ERROR_MESSAGE);
					message.setBodyLength(rbs);
					out.add(message);
					traffic(ctx, readCount, message);
					return;
				}
				return;	
			} else if(bb > 0) {		// 有起始标识，其前面还有内容。
//				printBuf(in, rpos, bb);
				// 丢弃起始标识前面的内容。
				readCount += bb;
				in.skipBytes(bb);
				rpos = in.readerIndex();
//				System.out.printf("[%s] MessageDecoder : %s %s bytes drop.\n", 
//						now,
//						ctx.channel().remoteAddress().toString(),
//						bb);
				message = new Message();
				message.setId(Message.ID_ERROR_MESSAGE);
				message.setBodyLength(bb);
				out.add(message);
				traffic(ctx, readCount, message);
				return;
			}
			startpos = rpos;
			rbs = in.readableBytes();
			if(rbs < Message.getMinSizeAll())	// 数据内容不足。
				return;
			// 2.结束标识。
			bb = in.bytesBefore(startpos + 1, rbs - 1, Message.DELIMITER);
			if(bb < 0)
				return;		// 没有结束标识。
			endpos = startpos + 1 + bb;
			// 确认完整数据包。
			len = endpos - startpos + 1;
			if(len < Message.getMinSizeAll()) {	// 本包数据长度错误。
//				printBuf(in, startpos, len);
				// 尝试下一包。
				readCount += len - 1;
				in.readerIndex(endpos);
//				System.out.printf("[%s] MessageDecoder : %s Wrong size. %s bytes packet drop.\n", 
//						now,
//						ctx.channel().remoteAddress().toString(),
//						len - 1);
				message = new Message();
				message.setId(Message.ID_ERROR_MESSAGE);
				message.setBodyLength(len);
				out.add(message);
				traffic(ctx, readCount, message);
				return;
			}
			
			// 3.解析内容
			try {
				message = new Message();
				// 起始标示。
				if(in.readByte() != Message.DELIMITER) {
//					printBuf(in, startpos, 1);
					// 当前包解析失败，尝试下一包。
					readCount += len - 1;
					in.readerIndex(endpos);
//					System.out.printf("[%s] MessageDecoder : %s Header DELIMITER expected.\n", 
//							now,
//							ctx.channel().remoteAddress().toString());
					message = new Message();
					message.setId(Message.ID_ERROR_MESSAGE);
					message.setBodyLength(len - 1);
					out.add(message);
					traffic(ctx, readCount, message);
					return;
				}
				// 消息头。
				message.setId(MessageCodec.readWord(in, VirtualFsuUtil.BYTEORDER));	// ID。
				message.setProp(MessageCodec.readWord(in, VirtualFsuUtil.BYTEORDER));	// 属性。
				boolean isSplit = message.isSplit();	// 是否分包
				int bodyLen = message.getBodyLength();	// 消息体长度
				message.setPhoneno(MessageCodec.readWord(in, VirtualFsuUtil.BYTEORDER));	// 终端号。
				message.setSerialno(MessageCodec.readWord(in, VirtualFsuUtil.BYTEORDER));	// 消息流水号。
				if(isSplit) {
					message.setSplittotal(MessageCodec.readWord(in, VirtualFsuUtil.BYTEORDER));	// 分包总数。
					message.setSplitno(MessageCodec.readWord(in, VirtualFsuUtil.BYTEORDER));	// 分包序号。
				}
				// 消息体。
				message.wrapBody(MessageCodec.readBytes(in, bodyLen));
				// 校验码。
				message.setChecksum(MessageCodec.readByte(in));
				// 结束标示。
				if(in.readByte() != Message.DELIMITER) {
					// 当前包解析失败，尝试下一包。
					in.readerIndex(endpos);
					readCount += len - 1;
//					System.out.printf("[%s] MessageDecoder : %s Tail DELIMITER expected.\n", 
//							now,
//							ctx.channel().remoteAddress().toString());
					message = new Message();
					message.setId(Message.ID_ERROR_MESSAGE);
					message.setBodyLength(len - 1);
					out.add(message);
					traffic(ctx, readCount, message);
					return;
				}
				// 输出对象。
				readCount += len;
				out.add(message);
				traffic(ctx, readCount, message);
				return;
			} catch(Exception e) {
//				printBuf(in, startpos, len);
				// 当前包解析失败，尝试下一包。
				readCount += len - 1;
				in.readerIndex(endpos);
//				System.out.printf("[%s] MessageDecoder : %s decode failed. %s bytes packet drop.\n", 
//						now,
//						ctx.channel().remoteAddress().toString(),
//						len - 1);
				message = new Message();
				message.setId(Message.ID_ERROR_MESSAGE);
				message.setBodyLength(len - 1);
				out.add(message);
				traffic(ctx, readCount, message);
				return;
				//continue;
			}
		}
	}
	
	/**
	 * 流量统计。
	 * @param readCount 读字节数。
	 * @param message 结果Message。null表示没有解析得到Message。
	 */
	private void traffic(ChannelHandlerContext ctx, long readCount, Message message) {
		CollectorInfo collectorInfo = CollectorChannelHandler.getCollectorInfoOfCtx(ctx);
		if(collectorInfo != null) {
			collectorInfo.addByteRead(readCount);
			if(message != null) {
				collectorInfo.addPacketRead(1);
				if(message.getId() == Message.ID_ERROR_MESSAGE)
					collectorInfo.addPacketReadErr(1);
			}
		}
	}
}
