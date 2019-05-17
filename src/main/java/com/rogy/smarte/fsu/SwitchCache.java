package com.rogy.smarte.fsu;

import com.rogy.smarte.entity.db1.Signalstype;
import com.rogy.smarte.entity.db1.Switch;

/**
 * 断路器相关信息的Cache。
 */
public class SwitchCache {
	private volatile int switchID;
	private volatile Integer collectorID;
	private volatile ZDataValue[] zDataValues;

	private SwitchCache(int switchID, final Signalstype[] signalstypes) {
		this.switchID = switchID;

		initZDataValues(signalstypes);
	}

	public static SwitchCache newSwitchCacheFromSwitch(Switch swt) {
		SwitchCache sc = new SwitchCache(swt.getSwitchID(), VirtualFsuUtil.SIGNALTYPES);
		sc.setCollectorID(swt.getCollector().getCollectorID());
		return sc;
	}

	public int getSwitchID() {
		return switchID;
	}

	public Integer getCollectorID() {
		return collectorID;
	}

	public void setCollectorID(Integer collectorID) {
		this.collectorID = collectorID;
	}

	public ZDataValue[] getzDataValues() {
		return zDataValues;
	}
	public ZDataValue getzDataValue(int i) {
		return zDataValues[i];
	}

	private void initZDataValues(Signalstype[] signalstypes) {
		zDataValues = new ZDataValue[signalstypes.length];
		for(int i = 0; i < signalstypes.length; i++) {
			if(signalstypes[i] != null) {
				zDataValues[i] = new ZDataValue();
			}
		}
	}

}
