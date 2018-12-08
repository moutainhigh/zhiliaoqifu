package com.ebeijia.zl.config;

public interface ShopConfig {
    //用于Redis命名空间或注册发现时标记自己
    String ID = "zl-api-shop";

    //单位秒，表示避免重复提交的时间间隔
    int SUBMIT_INTERVAL = 3;

    //单位秒，表示手机验证码的发送间隔
    int PHONE_VAILD_INTERVAL = 30;
}