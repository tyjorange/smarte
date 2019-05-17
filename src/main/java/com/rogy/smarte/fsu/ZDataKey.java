package com.rogy.smarte.fsu;

import java.util.Objects;

public class ZDataKey {
	private String switchID;
	private Short signalsTypeID;
	private int _hash;

	public ZDataKey(String switchID, Short signalsTypeID) {
		this.switchID = switchID;
		this.signalsTypeID = signalsTypeID;
		_hash = Objects.hash(switchID, signalsTypeID);	// 因为数据内容不会改变，所以只需计算一次hash值。
	}

	@Override
	public int hashCode() {
		return _hash;
	}

	@Override
	public boolean equals(Object o) {
		if(this == o)
			return true;
		if(o == null)
			return false;
		if(o instanceof ZDataKey) {
			return switchID.equals(((ZDataKey) o).switchID) &&
					signalsTypeID.equals(((ZDataKey) o).signalsTypeID);
		} else
			return false;
	}

	public String getSwitchID() {
		return switchID;
	}

	public Short getSignalsTypeID() {
		return signalsTypeID;
	}
}
