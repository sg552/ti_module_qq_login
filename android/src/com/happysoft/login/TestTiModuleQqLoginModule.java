/**
 * This file was auto-generated by the Titanium Module SDK helper for Android
 * Appcelerator Titanium Mobile
 * Copyright (c) 2009-2010 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Apache Public License
 * Please see the LICENSE included with this distribution for details.
 *
 */
package com.happysoft.login;

import org.appcelerator.kroll.KrollModule;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.kroll.*;

import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.util.*;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.kroll.common.TiConfig;


import java.util.HashMap;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
//import cn.sharesdk.demo.tpl.R;
import cn.sharesdk.framework.FakeActivity;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.*;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;

@Kroll.module(name="TestTiModuleQqLogin", id="com.happysoft.login")
public class TestTiModuleQqLoginModule extends KrollModule
  implements PlatformActionListener, Callback
{

  // Standard Debugging variables
  private static final String LCAT = "TestTiModuleQqLoginModule";
  private static final boolean DBG = TiConfig.LOGD;

	private static final int MSG_AUTH_CANCEL = 2;
	private static final int MSG_AUTH_ERROR= 3;
	private static final int MSG_AUTH_COMPLETE = 4;

	private static Handler handler;
  private static TestTiModuleQqLoginModule myModule;

  // You can define constants with @Kroll.constant, for example:
  // @Kroll.constant public static final String EXTERNAL_NAME = value;
  private KrollFunction onSuccess;
  private KrollFunction onError;
  private KrollFunction onCancel;

  public TestTiModuleQqLoginModule()
  {
    super();
    myModule = this;
  }

  @Kroll.onAppCreate
  public static void onAppCreate(TiApplication app)
  {
    Log.d(LCAT, "=== inside onAppCreate");
    // put module init code that needs to run when the application is created
    ShareSDK.initSDK(app);

		// 初始化ui
		handler = new Handler(myModule);
  }


  @Kroll.method
  public void showWeixinLoginPage(String platform_type,
      KrollFunction onError,
      KrollFunction onSuccess,
      KrollFunction onCancel){
    this.onError = onError;
    this.onSuccess = onSuccess;
    this.onCancel = onCancel;
    String type_name = "";
    if(platform_type.equals("weixin")){
      type_name = Wechat.NAME;
    }else if(platform_type.equals("qq")){
      type_name = QZone.NAME;
    }else if(platform_type.equals("weibo")){
      type_name = SinaWeibo.NAME;
    }else{
      System.out.println("=== in else... ");
      type_name = Wechat.NAME;
    }
    System.out.println("==== platform type name: " + type_name);
    Platform wechat = ShareSDK.getPlatform(type_name);
    Log.d(LCAT, "=== platform" + wechat);
    authorize(wechat);
  }

  /*
  @Kroll.method
  public void showWeixinLoginPage(String platform_type){
    System.out.println("=== not convert, platform_type: /" + platform_type +"/");
    System.out.println("=== after convert, platform_type: /" + TiConvert.toString(platform_type) +"/");
    String type_name = "";
    if(platform_type.equals("weixin")){
      type_name = Wechat.NAME;
    }else if(platform_type.equals("qq")){
      type_name = QZone.NAME;
    }else if(platform_type.equals("weibo")){
      type_name = SinaWeibo.NAME;
    }else{
      System.out.println("=== in else... ");
      type_name = Wechat.NAME;
    }
    System.out.println("==== platform type name: " + type_name);
    Platform wechat = ShareSDK.getPlatform(type_name);
    Log.d(LCAT, "=== platform" + wechat);
    authorize(wechat);
  }
  */

	//执行授权,获取用户信息
	//文档：http://wiki.mob.com/Android_%E8%8E%B7%E5%8F%96%E7%94%A8%E6%88%B7%E8%B5%84%E6%96%99
	private void authorize(Platform plat) {
		plat.setPlatformActionListener(this);
		//关闭SSO授权
		plat.SSOSetting(true);

		plat.showUser(null);

    Log.d(LCAT, "== authorize done");
    Log.d(LCAT, "== now get platformDb");
    PlatformDb platformDb = plat.getDb();
    Log.d(LCAT, platformDb.exportData());
    Log.d(LCAT, platformDb.getToken());

    System.out.println("== platformDb.isValid()");
    System.out.println(platformDb.isValid());
    Log.d(LCAT, platformDb.getUserId());
    Log.d(LCAT, platformDb.getExpiresIn()+"");
    Log.d(LCAT, platformDb.getPlatformNname());
    Log.d(LCAT, platformDb.getPlatformVersion()+"");
    Log.d(LCAT, platformDb.getUserIcon());
    System.out.println("============");
    Log.d(LCAT, "== donw: get platformDb");
	}


	public void onComplete(Platform platform, int action, HashMap<String, Object> res) {

    Log.d(LCAT, "=== in complete: " );
    Log.d(LCAT, platform+"");
    Log.d(LCAT, action+"");
    Log.d(LCAT, res+"");
    this.onSuccess.call(getKrollObject(), new Object[]{});

		if (action == Platform.ACTION_USER_INFOR) {
			Message msg = new Message();
			msg.what = MSG_AUTH_COMPLETE;
			msg.obj = new Object[] {platform.getName(), res};
      //System.out.println("=== complete: " + msg);
      Log.d(LCAT, "=== complete: " + msg);
			handler.sendMessage(msg);
		}
	}

	public void onError(Platform platform, int action, Throwable t) {
    this.onError.call(getKrollObject(), new Object[]{});
		if (action == Platform.ACTION_USER_INFOR) {
			handler.sendEmptyMessage(MSG_AUTH_ERROR);
      //System.out.println("=== error: " + MSG_AUTH_ERROR);
      Log.d(LCAT, "== error");
      Log.d(LCAT, ""+t);
		}
		t.printStackTrace();
	}

	public void onCancel(Platform platform, int action) {
    Log.d(LCAT, "=== in onCancel: " );
		if (action == Platform.ACTION_USER_INFOR) {
			handler.sendEmptyMessage(MSG_AUTH_CANCEL);
      Log.d(LCAT, "== canceled");
      //System.out.println("=== cancel: " + MSG_AUTH_CANCEL);
		}
	}
  private String name;
  // Methods
  @Kroll.method
  public String example()
  {
    Log.d(LCAT, "example called");
    //return "hello world";
    return name;
  }

  // Properties
  @Kroll.getProperty
  //public String getExampleProp()
  public String getName()
  {
    Log.d(LCAT, "get name property");
    return name;
  }


  @Kroll.setProperty
  public void setName(String value) {
    Log.d(LCAT, "set example property: " + value);
    name = value;
  }

}

