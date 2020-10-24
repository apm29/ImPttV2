package com.imptt.v2.core.ptt;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import java.util.Observable;

/**
 * Singleton settings class for universal access to the app's preferences.
 * You can listen to LibSettings events by registering your class as an observer and responding according to the observer key passed.
 *
 * @author morlunk
 *
 */
public class AppSettings extends Observable {
	// If you can't find a specifically observable key here, listen for "all".
	//关键字
	public final static String KEY_PERSONAL_HOST = "key_personal_host";
	public final static String KEY_COUNTRY = "key_country";
	public final static String KEY_PERSONAL_USERID = "key_personal_userid";
	public final static String KEY_PERSONAL_PASSWORD = "key_personal_password";
	public final static String KEY_AUTO_LOGIN = "key_auto_login";
	public final static String KEY_DARK = "key_dark";
	public final static String KEY_SELF_CENTER = "key_self_center";
	public final static String KEY_AUTO_LAUNCH = "key_auto_launch";	//开机自启动
	public final static String KEY_FOCE_SCREEN= "key_foce_screen";


	public final static String KEY_FIRST_USE = "key_first_use";	//是否首次使用
	public final static String KEY_LAST_CHECK_UPDATE = "key_last_check_update";

	private final SharedPreferences preferences;

	private static AppSettings settings;

	public static AppSettings getInstance(Context context) {
		if(settings == null)
			settings = new AppSettings(context);
		return settings;
	}

	private AppSettings(final Context ctx) {
		preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
	}

	public String getKeyCountry() {
		return preferences.getString(KEY_COUNTRY, "+86");
	}
	public void setKeyCountry(String s) {
		preferences.edit().putString(KEY_COUNTRY, s).commit();
	}

	public String getUserid() {
		return preferences.getString(KEY_PERSONAL_USERID, null);
	}
	public void setUserid(String s) {
		preferences.edit().putString(KEY_PERSONAL_USERID, s).commit();
	}

	public String getPassword() {
		return preferences.getString(KEY_PERSONAL_PASSWORD, null);
	}
	public void setPassword(String s) {
		preferences.edit().putString(KEY_PERSONAL_PASSWORD, s).commit();
	}

	public boolean getAutoLogin() {
        return preferences.getBoolean(KEY_AUTO_LOGIN, true);
	}
	public void setAutoLogin(boolean b) {
		preferences.edit().putBoolean(KEY_AUTO_LOGIN, b).commit();
	}

	public float getSelfCenter() {
		return preferences.getFloat(KEY_SELF_CENTER, 0);
	}
	public void setSelfCenter(float f) {
		preferences.edit().putFloat(KEY_SELF_CENTER, f).commit();
	}

	public boolean getDark() {
		return preferences.getBoolean(KEY_DARK, false);
	}
	public void setDark(boolean b) {
		preferences.edit().putBoolean(KEY_DARK, b).commit();
	}

	public boolean getAutoLaunch() {
        boolean isTotalkPhone = (Build.MODEL!=null && Build.MODEL.equals("N9I"));
		boolean isTotalkHardware = (Build.MODEL!=null && Build.MODEL.startsWith("TOTALK_"));

		return preferences.getBoolean(KEY_AUTO_LAUNCH, isTotalkPhone||isTotalkHardware ? true : false);
	}
	public void setAutoLaunch(boolean b) {
		preferences.edit().putBoolean(KEY_AUTO_LAUNCH, b).commit();
	}

	public String getHost() {
		return preferences.getString(KEY_PERSONAL_HOST, AppConstants.DEFAULT_PERSONAL_HOST);
	}
	public void setHost(String s) {
		preferences.edit().putString(KEY_PERSONAL_HOST, s).commit();
	}

	public boolean getFirstUse() {
		return preferences.getBoolean(KEY_FIRST_USE, true);
	}
	public void setFirstUse(boolean b) {
		preferences.edit().putBoolean(KEY_FIRST_USE, b).commit();
	}
	public long getLastCheckUpdate() {
		return preferences.getLong(KEY_LAST_CHECK_UPDATE, 0);
	}
	public void setLastCheckUpdate(long l) {
		preferences.edit().putLong(KEY_LAST_CHECK_UPDATE, l).commit();
	}

	public boolean getIsFoceScreenOrientation() {
		return preferences.getBoolean(KEY_FOCE_SCREEN, false);
	}

	public void setIsFoceScreenOrientation(boolean f) {
		preferences.edit().putBoolean(KEY_FOCE_SCREEN, f).commit();
	}

}
