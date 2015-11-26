package com.nifty.cloud.mb.ncmbgcmplugin;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;


/**
 * 通知関連を操作する
 *
 */
//@NCMBClassName("push")
public class NCMBPush {

	private static final String TAG = "com.nifty.cloud.mb.ncmbgcmplugin.NCMBPush";
	private static final String MATCH_URL_REGEX =
			"^(https?)(:\\/\\/[-_.!~*\\'()a-zA-Z0-9;\\/?:\\@&=+\\$,%#]+)$";

	/**
	 * コンストラクタ
	 *
	 */
	public NCMBPush() {
	}

	/**
	 * If it contains the URL in the payload data, it will display the webview
	 *
	 * @param context context
	 * @param intent  URL
	 */
	public static void richPushHandler(Context context, Intent intent) {
		if (intent == null) {
			return;
		}
		// URLチェック
		String url = intent.getStringExtra("com.nifty.RichUrl");
		if (url == null) {
			return;
		}
		// URLのバリデーションチェック
		if (!url.matches(MATCH_URL_REGEX)) {
			return;
		}

		// ダイアログ表示
		final NCMBRichPush dialog = new NCMBRichPush(context, url);
		dialog.show();
	}

	/**
	 * If it contains the dialog in the payload data, it will display the dialog
	 * @param context context
	 * @param bundle pushData
	 * @param dialogPushConfiguration push settings
	 */
	public static void dialogPushHandler(Context context,Bundle bundle, NCMBDialogPushConfiguration dialogPushConfiguration)
	{
		if(!bundle.containsKey("com.nifty.Dialog")){
			//dialogが有効になっていない場合
			return;
		}

		if(dialogPushConfiguration.getDisplayType() == NCMBDialogPushConfiguration.DIALOG_DISPLAY_NONE){
			//ダイアログ設定クラスの表示形式が"表示しない"(DIALOG_DISPLAY_NONE)場合
			return;
		}

		ApplicationInfo appInfo;
		String activityName ="";
		try {
			appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
			activityName = appInfo.packageName + appInfo.metaData.getString(".UnityPlayerNativeActivity");
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}

		//NCMBDialogActivityクラスを呼び出す
		Intent intetnt = new Intent(Intent.ACTION_MAIN);
		intetnt.setClass(context.getApplicationContext(), NCMBDialogActivity.class);
		intetnt.putExtra("com.nifty.OriginalData", bundle);
		intetnt.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intetnt.putExtra(NCMBDialogActivity.INTENT_EXTRA_THEME, android.R.style.Theme_Wallpaper_NoTitleBar);
		intetnt.putExtra(NCMBDialogActivity.INTENT_EXTRA_LAUNCH_CLASS, activityName);
		intetnt.putExtra(NCMBDialogActivity.INTENT_EXTRA_SUBJECT, bundle.getString("title"));
		intetnt.putExtra(NCMBDialogActivity.INTENT_EXTRA_MESSAGE, bundle.getString("message"));
		intetnt.putExtra(NCMBDialogActivity.INTENT_EXTRA_DISPLAYTYPE, dialogPushConfiguration.getDisplayType());
		context.getApplicationContext().startActivity(intetnt);
	}

}