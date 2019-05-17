package com.rogy.smarte.fsu;

import io.netty.channel.ChannelHandlerContext;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 集中器信息。
 * 一个集中器其对应的集中器信息对象应该只被创建一次并一直使用，以保证其属性和锁的唯一性。
 */
public class CollectorInfo {
	private static final AtomicLong ID_GENERATOR = new AtomicLong(0L);	// ID生成器。

	private long id;			// 本对象的ID
	private long codeValue;	// 集中器编码数值。
	private Integer collectID;	// 集中器记录ID。(我们认为集中器记录ID不会改变，所以缓存在此以避免频繁根据编码来查询记录ID的数据库操作)
	private int breakerCount;	// 集中器的断路器个数。
	private int encrypt;		// 加密方式。(加密方式是唯一的，在此是为了兼容以前没有加密的集中器)
	private byte[] key;		// 密钥。
	private AtomicReference<ChannelHandlerContext> ctx;	// 集中器当前对应的Socket连接。
	private Lock controllLock;	// 控制操作锁。(因为集中器对其断路器的控制操作不能并行，而且每个开关请求都需要对应几轮的重复操作，所以设置了排他锁)

	/**
	 * 流量。
	 */
	private AtomicLong byteRead, byteWrite;
	/**
	 * 数据包。
	 */
	private AtomicInteger packetRead, packetReadErr, packetWrite;
	/**
	 * 上线时长。
	 */
	private AtomicLong onlineSecs;			// 上线累积时长(秒)。
	private LocalDateTime onlineStart;		// 上线统计起始时间。
	/**
	 * 连线、断线次数。
	 */
	private AtomicInteger connectCount, disconnectCount;

	/**
	 * 获取配置时间。
	 */
	private LocalDateTime configTime;

	public CollectorInfo(long codeValue) {
		this(codeValue, 2);
	}

	public CollectorInfo(long codeValue, int encrypt) {
		this(codeValue, encrypt, null);
	}

	public CollectorInfo(long codeValue, int encrypt, Integer collectorID) {
		this.codeValue = codeValue;
		this.encrypt = encrypt;
		this.collectID = collectorID;
		breakerCount = 0;
		id = ID_GENERATOR.getAndIncrement();
		ctx = new AtomicReference<ChannelHandlerContext>();
		controllLock = new ReentrantLock(false);
		byteRead = new AtomicLong(0);
		byteWrite = new AtomicLong(0);
		packetRead = new AtomicInteger(0);
		packetReadErr = new AtomicInteger(0);
		packetWrite = new AtomicInteger(0);
		onlineStart = null;
		onlineSecs = new AtomicLong(0);
		connectCount = new AtomicInteger(0);
		disconnectCount = new AtomicInteger(0);
		configTime = null;
		genKey();
	}

	private void genKey() {
		key = new byte[16];
		Random random = new Random();
		random.nextBytes(key);
	}

	public long getId() {
		return id;
	}

	public long getCodeValue() {
		return codeValue;
	}
	public void setCodeValue(long codeValue) {
		this.codeValue = codeValue;
	}
	public Integer getCollectID() {
		return collectID;
	}
	public void setCollectID(Integer collectID) {
		this.collectID = collectID;
	}
	public int getBreakerCount() {
		return breakerCount;
	}

	public void setBreakerCount(int breakerCount) {
		this.breakerCount = breakerCount;
	}

	public int getEncrypt() {
		return encrypt;
	}
	public void setEncrypt(int encrypt) {
		this.encrypt = encrypt;
	}
	public final byte[] getKey() {
		return key;
	}

	public LocalDateTime getConfigTime() {
		return configTime;
	}

	public void setConfigTime(LocalDateTime configTime) {
		this.configTime = configTime;
	}

	/**
	 * 获取集中器控制锁。
	 * 如果获取锁失败，则调用线程进入等待状态。
	 * 对一个集中器的控制命令都是串行发送执行的，所以对集中器操作前要先调用本方法来进行排他处理。
	 * 命令执行完毕后调用controllUnlock释放锁。
	 */
	public void controllLock() {
		this.controllLock.lock();
	}
	/**
	 * 尝试获取集中器控制锁。
	 * @return 获取成功则返回true。否则返回false，调用线程不会进入等待状态。
	 */
	public boolean controllTryLock() {
		return this.controllLock.tryLock();
	}
	/**
	 * 尝试获取集中器控制锁。
	 * @return 获取成功则返回true。否则返回false，调用线程不会进入等待状态。
	 * @throws InterruptedException
	 */
	public boolean controllTryLock(long time, TimeUnit unit) throws InterruptedException {
		return this.controllLock.tryLock(time, unit);
	}
	/**
	 * 获取集中器控制锁。
	 * @throws InterruptedException
	 */
	public void controllLockInterruptible() throws InterruptedException {
		this.controllLock.lockInterruptibly();
	}

	/**
	 * 释放集中器控制锁(之前通过controllLock、controllLockInterruptible或controllTryLock调用获得的)。
	 */
	public void controllUnlock() {
		this.controllLock.unlock();
	}

	/**
	 * 设置CollectorInfo对应的网络连接。
	 * @param ctx 网络连接。
	 * @return CollectorInfo原来对应的网络连接。
	 */
	public ChannelHandlerContext setChannelHandlerContext(ChannelHandlerContext ctx) {
		LocalDateTime now = LocalDateTime.now();
		ChannelHandlerContext oldctx = this.ctx.getAndSet(ctx);
		if(ctx != null && oldctx == null) {	// 上线
			accOnlineStart(now);
		} else if(ctx == null && oldctx != null) {	// 下线
			accOnlineEnd(now);
		} else {	// Reconnect, 持续在线。
		}
		return oldctx;
	}

	/**
	 * 获取集中器对应的连接。
	 * @return 对应的连接。
	 */
	public ChannelHandlerContext getChannelHandlerContext() {
		return ctx.get();
	}

	/**
	 * 连接已中断，修改CollectorInfo对应的连接对象。
	 * 如果CollectorInfo当前对应的网络连接是该连接，则清除为null。
	 * 如果CollectorInfo当前对应的网络连接不是该连接，说明已重连并对应到了新的连接，则不能清除为null！
	 * @param ctx 中断的网络连接。
	 * @return 是否清除为null了。
	 */
	public boolean inactiveChannelHandlerContext(ChannelHandlerContext ctx) {
		boolean setnull = this.ctx.compareAndSet(ctx, null);
		if(setnull && ctx != null) {	// 下线
			accOnlineEnd(LocalDateTime.now());
		}
		return setnull;
	}

	/**
	 * 集中器上线，记录上线统计起始时间。
	 * @param startTime 上线起始时间。
	 */
	private void accOnlineStart(final LocalDateTime startTime) {
		synchronized(onlineSecs) {
			if(onlineStart != null) {
				System.out.printf("Collector(%012X) set online start(%s) but current(%s) not null.\n",
						codeValue, startTime, onlineStart);
			}
			onlineStart = startTime;
		}
	}

	/**
	 * 下线，累积计算在线时间。
	 * @param endTime 下线时间。
	 */
	private void accOnlineEnd(final LocalDateTime endTime) {
		synchronized(onlineSecs) {
			if(onlineStart == null) {
				System.out.printf("Collector(%012X) set online end(%s) but starttime is null.\n",
						codeValue, endTime);
			} else {
				onlineSecs.getAndAdd(Duration.between(onlineStart, endTime).getSeconds());
				onlineStart = null;
			}
		}
	}

	/**
	 * 阶段性累积计算在线时间。
	 * @param toTime 截止时间。
	 */
	private void accOnlinePeriod(final LocalDateTime toTime) {
		synchronized(onlineSecs) {
			if(onlineStart != null) {	// 上线了
				onlineSecs.getAndAdd(Duration.between(onlineStart, toTime).getSeconds());
				onlineStart = toTime;
			} else {	// 未上线
				// 没有在线时间累积。
			}
		}
	}

	/**
	 * 获取读字节数。
	 * @return 读字节数。
	 */
	public long getByteRead() {
		return byteRead.get();
	}
	/**
	 * 获取读字节数并清零。
	 * @return 读字节数。
	 */
	public long getAndClearByteRead() {
		return byteRead.getAndSet(0);
	}
	/**
	 * 增加读字节数。
	 * @param delta 变化值。
	 * @return 变化后的新值。
	 */
	public long addByteRead(long delta) {
		return byteRead.addAndGet(delta);
	}

	/**
	 * 获取写字节数。
	 * @return 读字节数。
	 */
	public long getByteWrite() {
		return byteWrite.get();
	}
	/**
	 * 获取写字节数并清零。
	 * @return 读字节数。
	 */
	public long getAndClearByteWrite() {
		return byteWrite.getAndSet(0);
	}
	/**
	 * 增加写字节数。
	 * @param delta 变化值。
	 * @return 变化后的新值。
	 */
	public long addByteWrite(long delta) {
		return byteWrite.addAndGet(delta);
	}

	/**
	 * 获取读包数。
	 * @return 读包数。
	 */
	public int getPacketRead() {
		return packetRead.get();
	}
	/**
	 * 获取读包数并清零。
	 * @return 读包数。
	 */
	public int getAndClearPacketRead() {
		return packetRead.getAndSet(0);
	}
	/**
	 * 增加读包数。
	 * @param delta 变化值。
	 * @return 变化后的新值。
	 */
	public int addPacketRead(int delta) {
		return packetRead.addAndGet(delta);
	}

	/**
	 * 获取读错误包数。
	 * @return 读错误包数。
	 */
	public int getPacketReadErr() {
		return packetReadErr.get();
	}
	/**
	 * 获取读错误包数并清零。
	 * @return 读错误包数。
	 */
	public int getAndClearPacketReadErr() {
		return packetReadErr.getAndSet(0);
	}
	/**
	 * 增加读错误包数。
	 * @param delta 变化值。
	 * @return 变化后的新值。
	 */
	public int addPacketReadErr(int delta) {
		return packetReadErr.addAndGet(delta);
	}

	/**
	 * 获取写包数。
	 * @return 写包数。
	 */
	public int getPacketWrite() {
		return packetWrite.get();
	}
	/**
	 * 获取写包数并清零。
	 * @return 写包数。
	 */
	public int getAndClearPacketWrite() {
		return packetWrite.getAndSet(0);
	}
	/**
	 * 增加写包数。
	 * @param delta 变化值。
	 * @return 变化后的新值。
	 */
	public int addPacketWrite(int delta) {
		return packetWrite.addAndGet(delta);
	}

	/**
	 * 获取在线时长。
	 * @return 在线时长。
	 */
	public long getOnlineSecs() {
		accOnlinePeriod(LocalDateTime.now());	// 统计到现在时刻。
		return onlineSecs.get();
	}

	/**
	 * 获取在线时长并清零。
	 * @return 在线时长。
	 */
	public long getAndClearOnlineSecs() {
		accOnlinePeriod(LocalDateTime.now());	// 统计到现在时刻。
		return onlineSecs.getAndSet(0L);
	}

	/**
	 * 连线次数递增一次。
	 * @return 变化后的新值。
	 */
	public int incConnectCount() {
		return connectCount.incrementAndGet();
	}
	/**
	 * 获取连线次数。
	 * @return 连线次数。
	 */
	public int getConnectCount() {
		return connectCount.get();
	}
	/**
	 * 获取连线次数并清零。
	 * @return 连线次数。
	 */
	public int getAndClearConnectCount() {
		return connectCount.getAndSet(0);
	}

	/**
	 * 断线次数递增一次。
	 * @return 变化后的新值。
	 */
	public int incDisconnectCount() {
		return disconnectCount.incrementAndGet();
	}
	/**
	 * 获取断线次数。
	 * @return 连线次数。
	 */
	public int getDisconnectCount() {
		return disconnectCount.get();
	}
	/**
	 * 获取断线次数并清零。
	 * @return 断线次数。
	 */
	public int getAndClearDisconnectCount() {
		return disconnectCount.getAndSet(0);
	}
}
