package com.rogy.smarte.util;

import cn.jpush.api.JPushClient;
import cn.jpush.api.common.resp.APIConnectionException;
import cn.jpush.api.common.resp.APIRequestException;
import cn.jpush.api.push.model.Options;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.AndroidNotification;
import cn.jpush.api.push.model.notification.IosNotification;
import cn.jpush.api.push.model.notification.Notification;

public class PushMessage {

	private static final String APPKEY = "54ab52bf58bffe139aa0a64d";
	private static final String MASTER = "db4c88c8aa865eb577c4148b";
	private static JPushClient jpushClient = new JPushClient(MASTER, APPKEY);

	/**
	 * 所有平台，所有设备，内容为alert
	 *
	 * @param alert
	 * @return
	 */
	public static boolean buildPush(String alert) {
		PushPayload payload = PushPayload.alertAll(alert);
		try {
			jpushClient.sendPush(payload);
			return true;
		} catch (APIConnectionException e) {
			e.printStackTrace();
			return false;
		} catch (APIRequestException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 所有平台，标签为tag，内容为alert
	 * 
	 * @param tag
	 * @param alert
	 * @return
	 */
	public static boolean buildPushByTag(String tag, String alert, String type) {
		if (tag == null || tag.trim().isEmpty() || alert == null
				|| alert.trim().isEmpty())
			return false;
		PushPayload payload = PushPayload
				.newBuilder()
				.setPlatform(Platform.all()).setOptions(Options.newBuilder().setApnsProduction(true).build())
				.setAudience(Audience.tag(tag))
				.setNotification(
						Notification
								.newBuilder()
								.setAlert(alert)
								.addPlatformNotification(
										AndroidNotification.newBuilder()
												.addExtra("type", type).build())
								.addPlatformNotification(
										IosNotification.newBuilder()
												.addExtra("type", type)
												.autoBadge()
												// .incrBadge(1)
												.setSound("alarm_burglar.caf")
												.build()).build()).build();
		try {
			jpushClient.sendPush(payload);
			return true;
		} catch (APIConnectionException e) {
			e.printStackTrace();
			return false;
		} catch (APIRequestException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 安卓平台，别名为alias，内容为alert
	 * 
	 * @param alias
	 * @param alert
	 * @return
	 */
	/*
	 * public static boolean buildPushByAliasWithAndroid(String alias, String
	 * content) { if (alias == null || alias.trim().isEmpty() || content == null
	 * || content.trim().isEmpty()) return false; PushPayload payload =
	 * PushPayload.newBuilder()
	 * .setPlatform(Platform.android()).setAudience(Audience.alias(alias))
	 * 
	 * .setNotification( Notification .newBuilder() .setAlert(alert)
	 * .addPlatformNotification( AndroidNotification.newBuilder() .build())
	 * .addPlatformNotification( IosNotification.newBuilder() .incrBadge(1)
	 * .setSound("default").build()) .build())
	 * 
	 * .setMessage(Message.content(content)).build(); try {
	 * jpushClient.sendPush(payload); return true; } catch
	 * (APIConnectionException e) { e.printStackTrace(); return false; } catch
	 * (APIRequestException e) { e.printStackTrace(); return false; } }
	 */

	public static boolean buildPushByAlias(String alias, String alert,
                                           String type) {
		if (alias == null || alias.trim().isEmpty() || alert == null
				|| alert.trim().isEmpty())
			return false;
		PushPayload payload = PushPayload
				.newBuilder()
				.setPlatform(Platform.all()).setOptions(Options.newBuilder().setApnsProduction(true).build())
				.setAudience(Audience.alias(alias))
				.setNotification(
						Notification
								.newBuilder()
								.setAlert(alert)
								.addPlatformNotification(
										AndroidNotification.newBuilder()
												.addExtra("type", type).build())
								.addPlatformNotification(
										IosNotification.newBuilder()
												.addExtra("type", type)
												.autoBadge()
//												.incrBadge(1)
												.setSound("alarm_burglar.caf").build())
								.build()).build();
		try {
			jpushClient.sendPush(payload);
			return true;
		} catch (APIConnectionException e) {
			e.printStackTrace();
			return false;
		} catch (APIRequestException e) {
			e.printStackTrace();
			return false;
		}
	}
}
