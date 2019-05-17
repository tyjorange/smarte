package com.rogy.smarte.util;


import com.rogy.smarte.sdk.CCPRestSDK;

import java.util.HashMap;
import java.util.Set;

/**
 *
 */
public class SendMessage {

	@SuppressWarnings("unchecked")
	public static boolean sendMessage(String phones, String code, String time) {
		HashMap<String, Object> result = null;
		CCPRestSDK restAPI = new CCPRestSDK();
		restAPI.init("app.cloopen.com", "8883");// 初始化服务器地址和端口，格式如下，服务器地址不需要写https://
//		restAPI.init("sandboxapp.cloopen.com", "8883");// 初始化服务器地址和端口，格式如下，服务器地址不需要写https://
		restAPI.setAccount("8a216da85577a5cc0155802d38160b22", "dc1abb3a588a4a4094d85d29ef60ec27");// 初始化主帐号和主帐号TOKEN
		restAPI.setAppId("8aaf07086715299301672a9af0200f67");// 初始化应用ID
		result = restAPI.sendTemplateSMS(phones, "389115" , new String[]{code, time + "分钟"});
		System.out.println("SDKTestSendTemplateSMS result=" + result);
		if("000000".equals(result.get("statusCode"))){
			//正常返回输出data包体信息（map）
			HashMap<String, Object> data = (HashMap<String, Object>) result.get("data");
			Set<String> keySet = data.keySet();
			for(String key:keySet){
				Object object = data.get(key);
				System.out.println(key +" = "+object);
			}
			return true;
		}else{
			//异常返回输出错误码和错误信息
			System.out.println("错误码=" + result.get("statusCode") +" 错误信息= "+result.get("statusMsg"));
			return false;
		}
	
	}
}
