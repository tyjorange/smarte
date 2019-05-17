package com.rogy.smarte.controller.app;


import net.sf.json.JSONArray;
import net.sf.json.JSONNull;
import net.sf.json.JSONObject;

public abstract class BaseWS {

	private static final long serialVersionUID = 1L;
	private static final String FIELD_CODE = "code";
	private static final String FIELD_MESSAGE = "message";
	private static final String FIELD_DATA_SIZE = "datasize";
	private static final String FIELD_DATA = "data";

	public static enum WSCode {
		SUCCESS(1, "成功"),
		UPLOAD_FAIL(2, "上传文件失败"),
		WRONG_PARAMETER_FORMAT(3, "参数格式错误"),
		DATABASE_EXCEPTION(4, "数据库操作失败"),
		WRONG_PARAMETER_VALUE(5, "参数值错误"),
		EXIST(6, "已存在"),
		PHONE_UNREGISTERED(7, "手机号码未注册"),
		PHONE_REGISTERED(8, "手机号码已经注册"),
		CODE_DISABLED(9, "验证码已经失效"),
		WRONG_PWD(10, "密码错误"),
		PHONE_MISMATCHING_CODE(11, "手机号码与验证码不匹配"),
		EMPTY_RESULT(12, "无结果"),
		USERNAME_REIGSTERED(13, "用户名已注册"),
		UNEXIST(20, "不存在"),
		AUTH_FAILED(100, "权限错误"),
		FAILED(0, "操作失败");

		private int code;				// 返回码
		private String desc;			// 描述
		WSCode(int code, String desc) {
			this.code = code;
			this.desc = desc;
		}
		public int getCode() {
			return code;
		}
		public String getDesc() {
			return desc;
		}
	}

	/**
	 * 得到指定类型的Json字符串
	 * @param wsCode 结果码
	 * @return 对应的Json字符串
	 */
	public String getResultJsonString(WSCode wsCode) {
		JSONObject result = new JSONObject();
		result.put(FIELD_CODE, wsCode.getCode());
		result.put(FIELD_MESSAGE, wsCode.getDesc());
		return result.toString();
	}

	/**
	 * 得到指定类型和结果集的Json字符串
	 * @param wsCode 结果码
	 * @param ja 结果集
	 * @return 对应的Json字符串
	 */
	public String getResultJsonString(WSCode wsCode, JSONArray ja) {
		JSONObject result = new JSONObject();
		result.put(FIELD_CODE, wsCode.getCode());
		result.put(FIELD_MESSAGE, wsCode.getDesc());
		if(ja == null) {
			result.put(FIELD_DATA, JSONNull.getInstance());
		}
		else {
			result.put(FIELD_DATA_SIZE, ja.size());
			result.put(FIELD_DATA, ja);
		}
		return result.toString();
	}

	/**
	 * 得到指定类型和结果的Json字符串
	 * @param wsCode 结果码
	 * @param jo 结果
	 * @return 对应的Json字符串
	 */
	public String getResultJsonString(WSCode wsCode, JSONObject jo) {
		JSONObject result = new JSONObject();
		result.put(FIELD_CODE, wsCode.getCode());
		result.put(FIELD_MESSAGE, wsCode.getDesc());
		if(jo == null) {
			result.put(FIELD_DATA, JSONNull.getInstance());
		}
		else {
			result.put(FIELD_DATA, jo);
		}
		return result.toString();
	}

}
