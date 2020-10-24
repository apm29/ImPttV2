package com.imptt.v2.core.ptt;

import android.graphics.Bitmap;
import android.graphics.Color;

public class AppConstants {
	public final static String DEFAULT_PERSONAL_HOST = AppConstants.ENT_VERSION ? "" : "totalkd.allptt.com";
	public final static boolean TEST_SMS = false;//去掉短信验证
	public final static boolean DEBUG = false;
	//编译出来是个人版还是企业版。企业版支持设置服务器地址、中性界面，以及屏蔽创建、注册，等等
	//编译企业版时，除修改此值，还需修改：
	// string文件里的app_name，drawable里的app_icon
    // 以及pttlib里的FOR_SDK定义
	// 考虑地图等sdk的key，简单起见，个人版企业版包名相同。企业版本号+1000不升级
	public final static boolean ENT_VERSION = false;
	public final static boolean FIX_VOICE_BPS = true;	//为true时，隐藏码率设置

	public final static int SEND_LOC_INTERVAL = 10;
	public final static int LOCATION_MAX_INTERVAL_SECONDS = 300;	//位置不变时，减少上报次数，但是仍然需要一个最大时间
	public final static double LOCATION_DIFF_DISTANCE = 5.0;	//位置不变时，减少上报次数。如果位置偏离超过此数值，认为位置变化了

	public final static int EXP_CHANNEL_ID = 1024;	//体验频道
	public final static int MOTOR_CHANNEL_ID = 1302;	//体验频道
	public final static int HAM_CHANNEL_ID = 6702;	//体验频道
	public final static int CAR_CHANNEL_ID = 6704;	//体验频道
	public final static int MMDVM_CHANNEL_ID = 37916;	//体验频道
	public final static int CHAT_CHANNEL_ID = 88888;	//体验频道
	public final static Bitmap.Config BITMAP_CONFIG = Bitmap.Config.RGB_565;

	//注册完成后，返回注册结果
	public static final String EXTRA_REG_USER = "extra_reg_user";
	public static final String EXTRA_REG_PWD = "extra_reg_pwd";
	public static final String EXTRA_START_VERIFYPHONE_FOR = "extra_start_verifyphone_for";

	//用户名和密码的正则表达式
	public static final int NAME_MAX_LENGTH = 512;	//频道和用户名称的最大长度
	public static final String EX_CHANNELNAME = "[ \\-=\\w\\#\\[\\]\\{\\}\\(\\)\\@\\|]+";	//从服务器copy过来，与服务器保持一致
	public static final String EX_PASSWORD = "\\w{6,32}+";				//6-32位，不含空格
	public static final String EX_CHANNEL_PASSWORD = "^\\d{4}$";				//4-16位，不含空格
	public static final String EX_NICK = "[-=\\w\\[\\]\\{\\}\\(\\)\\@\\|\\. ]+";		//允许点和空格
	public static final String EX_VERIFY_CODE = "^\\d{4}$";		//4位数字

	public final static int CURRENT_NICK_COLOR = Color.rgb(29, 103, 203);	//本人名字高亮显示颜色
	public final static int OTHER_NICK_COLOR = Color.rgb(0x33, 0x33, 0x33);	//别人名字
	public final static int OTHER_NICK_COLOR_DARK = Color.rgb(0xff, 0xff, 0xff);	//别人名字

	//以下来自原来的Globals.java
	public static final String LOG_TAG = "Totalk";

	public enum NETWORK_STATE {DISCONNECT, WIFI, MOBILE}

	public static final String[] permReason =
			{
					"没有权限", "频道名称格式错误", "用户名格式错误", "频道人数已满", "频道已过期" ,
					"单个用户创建频道数已达上限", "频道不存在", "频道口令错误"
			};
}
