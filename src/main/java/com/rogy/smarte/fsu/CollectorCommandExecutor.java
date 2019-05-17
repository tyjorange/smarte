package com.rogy.smarte.fsu;

import com.rogy.smarte.entity.db1.Controller;
import com.rogy.smarte.entity.db1.Switch;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 执行集中器的断路器控制操作。
 * 对同一集中器要进行排他处理，因为同一时刻集中器只能控制其下的某一个断路器，不能并行处理。
 * 对断路器的控制，要进行多次操作，直到其状态变更为止。
 */
public class CollectorCommandExecutor implements Runnable {
	private static final int GET_TIMES = 5;	// 尝试读取记录的次数。

	private volatile boolean stopFlag = false;		// 结束标记。

	/**
	 * 等待执行的命令队列。
	 */
	private LinkedBlockingQueue<BreakerCommand> waitingQueue = new LinkedBlockingQueue<>();
	/**
	 * 命令执行线程池。
	 */

	private ExecutorService executorService;

	public CollectorCommandExecutor(ExecutorService executorService) {
		this.executorService = executorService;
	}

	public void stop() {
		stopFlag = true;
	}

	/**
	 * 加入断路器控制命令。
	 * @param codeValueBreaker 断路器地址数值。
	 * @param breakerId 断路器记录ID。
	 * @param codeValueCollector 所属集中器地址数值。
	 * @param collectorId 集中器记录ID。
	 * @param commandData 命令数据。
	 * @param controllerId 对应的Controller记录的ID。
	 * @return 加入成功与否。0=成功;其他=错误码。
	 */
	public int addCommand(long codeValueBreaker, int breakerId, long codeValueCollector, int collectorId, byte commandData, long controllerId) {
		if(stopFlag)
			return -1000;

		LocalDateTime now = LocalDateTime.now();
		BreakerCommand breakerCommand = new BreakerCommand();
		breakerCommand.setCodeValueBreaker(codeValueBreaker);
		breakerCommand.setBreakerId(breakerId);
		breakerCommand.setCodeValueCollector(codeValueCollector);
		breakerCommand.setCollectorId(collectorId);
		breakerCommand.setCommandData(commandData);
		breakerCommand.setControllerId(controllerId);
		breakerCommand.setRequestTime(now);
		try {
			waitingQueue.put(breakerCommand);
			return 0;
		} catch(Exception e) {
			return -100;
		}
	}

	/**
	 * 执行断路器命令。
	 * @param breakerCommand 要执行的断路器命令对象。
	 * @return 执行完毕的断路器命令对象。
	 */
	private BreakerCommand doBreakerCommand(BreakerCommand breakerCommand) {
		LocalDateTime now = LocalDateTime.now();
		breakerCommand.setRunTime(now);
		if(stopFlag) {
			breakerCommand.setErrCode(-1000);
			return breakerCommand;
		}

		long codeValueBreaker = breakerCommand.getCodeValueBreaker();
		long codeValueCollector = breakerCommand.getCodeValueCollector();
		Switch s = null;
		Controller controller = null;
		// 操作
		CollectorInfo collectorInfo = null;
		// 获取集中器信息对象
		collectorInfo = VirtualFsuCollectorInfo.getCollectorInfo(codeValueCollector);
		if(collectorInfo == null) {
			System.out.printf("[%s] CollectorCommandExecutor - Failed to get CollectorInfo of Collector(%012X)\n",
					now,
					codeValueCollector);
			breakerCommand.setErrCode(21);
			return breakerCommand;
		}
		breakerCommand.setCollectorInfoId(collectorInfo.getId());
		ChannelHandlerContext ctx = null;
		try {
			// 集中器操作排他锁
			while(!stopFlag) {
				// 集中器控制加锁
				if(collectorInfo.controllTryLock(5, TimeUnit.SECONDS)) {	// 成功
					break;
				} else {	// 失败
					// 继续尝试。
				}
			}
			// 获取对应的控制记录
			for(int i = 0; !stopFlag && i < GET_TIMES; i++) {	// 因为记录保存数据库可能有延迟，在此循环进行查询。
				try {
					controller = VirtualFsuUtil.virtualFsuService.findController(breakerCommand.getControllerId());
				} catch (Exception e) {
					e.printStackTrace();
					controller = null;
					break;
				}
				if(controller == null) {	// 对应记录没有查询到
					try{
						Thread.sleep(1000);
					} catch(Exception e) {
						break;
					}
				} else {	// 查询到了对应记录
					break;
				}
			}
			if(controller == null) {
				System.out.printf("[%s] doBreakerCommand - Failed to get Controller(%s).\n",
						now,
						breakerCommand.getControllerId());
				breakerCommand.setErrCode(10);
				return breakerCommand;
			}
			// 操作
			for(int i = 0;
					i < VirtualFsuController.BREAKER_CONTROL_TIMES;
					i++) {	// 多轮操作，因为集中器有时会不响应操作命令。
				// 1、向该连接发送控制命令
				if(stopFlag)
					break;
				ctx = collectorInfo.getChannelHandlerContext();	// 每次都重新获取连接对象，因为操作过程时间较长，可能断线重连了。
				if(ctx != null) {	// 集中器在线
					if(VirtualFsuController.switchOnOff(
							breakerCommand.getCommandData(),
							codeValueBreaker,
							collectorInfo.getEncrypt(),
							collectorInfo.getKey(),
							ctx)) {	// 操作成功
						// 记录操作结果
						if(controller.getTargetType() == 0 &&	// 断路器操作
								!controller.getRunCode().equals("2")) {
							controller.setRunCode("2");
							// 及早更新数据库，让APP知道执行成功，而不要等到多次执行全部完毕再更新
							try {
								VirtualFsuUtil.virtualFsuService.addOrUpdateControllerResult(controller);
							} catch(Exception e) {
								e.printStackTrace();
								breakerCommand.setErrCode(30);
								break;
							}
						}
					} else {	// 操作失败
						System.out.printf("[%s] CollectorCommandExecutor - switchOnOff(%012X-%012X) failed.\n",
								LocalDateTime.now(),
								codeValueCollector,
								codeValueBreaker);
						breakerCommand.setErrCode(31);
						break;
					}
					// 2、等待
					if(stopFlag)
						break;
					try {
						Thread.sleep(VirtualFsuController.BREAKER_CONTROL_WAITMS);
					} catch (InterruptedException ie) {};
					// 3、查询断路器状态
					if(stopFlag)
						break;
					if(VirtualFsuController.switchFault(
							codeValueBreaker,
							collectorInfo.getEncrypt(),
							collectorInfo.getKey(),
							ctx)) {	// 查询成功
					} else {	// 查询失败
						System.out.printf("[%s] CollectorCommandExecutor - switchFault(%012X-%012X) failed.\n",
								LocalDateTime.now(),
								codeValueCollector,
								codeValueBreaker);
						breakerCommand.setErrCode(32);
						break;
					}
					// 4、等待
					if(stopFlag)
						break;
					try {
						Thread.sleep(VirtualFsuController.BREAKER_FAULT_WAITMS);
					} catch (InterruptedException ie) {};
					// 5、判断断路器状态是否已经变更
					if(stopFlag)
						break;
					try {
						s = VirtualFsuUtil.virtualFsuService.findSwitchBySwitchID(Integer.valueOf(breakerCommand.getBreakerId()));	// 查询断路器最新记录
					} catch(Exception e) {
						s = null;
					}
					if(s == null) {
						System.out.printf("[%s] CollectorCommandExecutor - Failed to get Breaker(ID=%s, CODE=%012X).\n",
								LocalDateTime.now(),
								breakerCommand.getBreakerId(),
								codeValueBreaker);
						breakerCommand.setErrCode(33);
						break;
					}
					if(s.getState() == breakerCommand.getCommandData()) {	// 已变更，控制成功结束。
						System.out.printf("[%s] CollectorCommandExecutor - Round(%d) (%s's %s)'s state changed to (%d).\n",
								LocalDateTime.now(),
								i,
								s.getCollector().getCode(),
								s.getCode(),
								breakerCommand.getCommandData());
						breakerCommand.setErrCode(0);
						break;
					} else {	// 未变更，继续重复操作。
						if(i == VirtualFsuController.BREAKER_CONTROL_TIMES - 1) {	// 循环结束
							breakerCommand.setErrCode(34);
						}
					}
				} else {	// 集中器不在线，继续重复操作。
					breakerCommand.setErrCode(38);
				}
			}
		} catch(Exception e) {
		} finally {
			// 释放集中器控制锁。
			try {
				if(collectorInfo != null)
					collectorInfo.controllUnlock();
			} catch(Exception e) {
			}
		}

		if(stopFlag)
			breakerCommand.setErrCode(-1000);

		return breakerCommand;
	}

	/**
	 * 断路器命令执行完毕后的处理。
	 * @param breakerCommand 断路器命令对象。
	 * @return 断路器命令对象。
	 */
	private BreakerCommand doBreakerCommandResult(BreakerCommand breakerCommand) {
		LocalDateTime now = LocalDateTime.now();
		breakerCommand.setOverTime(now);
		Controller controller = null;
		if(!stopFlag) {
			for(int i = 0; !stopFlag && i < GET_TIMES; i++) {
				// 控制记录
				try {
					controller = VirtualFsuUtil.virtualFsuService.findController(breakerCommand.getControllerId());
				} catch (Exception e) {
					e.printStackTrace();
					controller = null;
					break;
				}
				if(controller == null) {
					try{
						Thread.sleep(1000);
					} catch(Exception e) {
						break;
					}
				}
				else {
					if(i > 0) {
						System.out.printf("[%s] doBreakerCommandResult - got Controller(%s) at Round(%d).\n",
								now,
								breakerCommand.getControllerId(),
								i);
					}
					break;
				}
			}
			if(controller == null) {
				System.out.printf("[%s] doBreakerCommandResult - Failed to get Controller(%s).\n",
						now,
						breakerCommand.getControllerId());
				breakerCommand.setErrCode(100);
			}
			else {
				if(controller.getTargetType() == 0) {	// 断路器操作
					if(breakerCommand.getErrCode() != 0) {	// 出错了
						controller.setRunCode("1");
						try {
							VirtualFsuUtil.virtualFsuService.addOrUpdateControllerResult(controller);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				long codeValueCollector = breakerCommand.getCodeValueCollector();
				// 获取对应的集中器信息
				CollectorInfo collectorInfo = VirtualFsuCollectorInfo.getCollectorInfo(codeValueCollector);
				if(collectorInfo == null) {
					breakerCommand.setErrCode(110);
					System.out.printf("[%s] CollectorCommandExecutor - Failed to get CollectorInfo of Collector(%012X).\n",
							now,
							codeValueCollector);
				}
				else {
					long bid = breakerCommand.getCollectorInfoId();
					if(bid >= 0) {	// 缺省<0，表示没有执行。
						if(collectorInfo.getId() == bid) {	// 集中器信息对象没有改变
						}
						else {
							System.out.printf("[%s] CollectorCommandExecutor - collectorInfo(%d) <> breakerCommand.collectorInfo(%d).\n",
									now,
									collectorInfo.getId(),
									bid);
							breakerCommand.setErrCode(120);
						}
					}
				}
			}
			if(breakerCommand.getErrCode() != 0)
				System.out.printf("BreakerCommand failed. %s\n", breakerCommand.toString());
		}

		if(stopFlag)
			breakerCommand.setErrCode(-1000);

		// 保存结果错误码
		if(!stopFlag && controller != null && breakerCommand.getErrCode() != 0) {
			try {
				controller.setRunResult(breakerCommand.getErrCode());
				VirtualFsuUtil.virtualFsuService.addOrUpdateControllerResult(controller);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}

		return breakerCommand;
	}

	/**
	 * 提交断路器命令去执行。
	 * @param breakerCommand 要执行的断路器命令对象。
	 * @return 执行并处理完毕的断路器命令对象。
	 */
	private BreakerCommand submitBreakerCommand(BreakerCommand breakerCommand) {
		if(!stopFlag) {
			breakerCommand.setErrCode(0);
			breakerCommand.setSubmitTime(LocalDateTime.now());
			CompletableFuture
			.supplyAsync(() -> doBreakerCommand(breakerCommand), executorService)	// 异步执行
			.thenAcceptAsync(this::doBreakerCommandResult, executorService);	// 执行完毕后异步处理
		}

		if(stopFlag)
			breakerCommand.setErrCode(-1000);

		return breakerCommand;
	}

	@Override
	public void run() {
		System.out.printf("[%s] CollectorCommandExecutor service start...\n", LocalDateTime.now());

		BreakerCommand breakerCommand;
		while(!stopFlag && !Thread.currentThread().isInterrupted()) {
			breakerCommand = null;
			try {
				breakerCommand = waitingQueue.take();
			} catch(Exception e) {
				breakerCommand = null;
			}
			if(!stopFlag && breakerCommand != null) {
				submitBreakerCommand(breakerCommand);
			}
		}

		System.out.printf("[%s] CollectorCommandExecutor service stop.\n", LocalDateTime.now());
	}
}
