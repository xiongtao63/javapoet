package com.xiangxue.arouter_api;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 跳转时 Intent ，用于参数的传递
 */
public class BundleManager {

    // 携带的值，保存到这里  Intent 传输
    private Bundle bundle = new Bundle();

    public Bundle getBundle() {
        return this.bundle;
    }

    // 对外界提供，可以携带参数的方法
    public BundleManager withString(@NonNull String key, @Nullable String value) {
        bundle.putString(key, value);
        return this;
    }

    public BundleManager withBoolean(@NonNull String key, @Nullable boolean value) {
        bundle.putBoolean(key, value);
        return this;
    }

    public BundleManager withInt(@NonNull String key, @Nullable int value) {
        bundle.putInt(key, value);
        return this;
    }

    public BundleManager withBundle(Bundle bundle) {
        this.bundle = bundle;
        return this;
    }

    // Derry只写到了这里，同学们可以自己增加 ...
    // 架构师后续扩展

    // 直接完成跳转
    public Object navigation(Context context) {
        // 单一原则
        // 把自己所有行为 都交给了  路由管理器
        return RouterManager.getInstance().navigation(context, this);
    }
}
