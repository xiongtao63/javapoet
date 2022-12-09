package com.xiangxue.arouter_api;

import android.app.Activity;
import android.util.LruCache;

/**
 * 参数的 加载管理器
 * TODO 同学们：这是用于接收参数的
 *
 * 1.查找  Order_MainActivity$$Parameter
 * 2.使用 Order_MainActivity$$Parameter
 */
public class ParameterManager {

    private static ParameterManager instance;

    public static ParameterManager getInstance() {
        if (instance == null) {
            synchronized (ParameterManager.class) {
                if (instance == null) {
                    instance = new ParameterManager();
                }
            }
        }
        return instance;
    }

    // 懒加载  用到了 就加载，    阿里的路由 全局加载（不管你用没有用到，反正全部加载）
    // LRU缓存 key=类名      value=参数加载接口
    private LruCache<String, ParameterGet> cache;

    private ParameterManager() {
        cache = new LruCache<>(100);
    }

    // 我们已经生成完毕了，是为查找
    static final String FILE_SUFFIX_NAME = "$$Parameter"; // 为了这个效果：Order_MainActivity + $$Parameter

    // 使用者 只需要调用这一个方法，就可以进行参数的接收
    public void loadParameter(Activity activity) {
        String className = activity.getClass().getName(); // className == Personal_MainActivity

        ParameterGet parameterLoad = cache.get(className); // key className
        if (null == parameterLoad) { // 缓存里面没东东
            // 拼接 如：Order_MainActivity + $$Parameter
            // 类加载Order_MainActivity + $$Parameter
            try {
                Class<?> aClass = Class.forName(className + FILE_SUFFIX_NAME);
                // 用接口parameterLoad = 接口的实现类Personal_MainActivity
                parameterLoad = (ParameterGet) aClass.newInstance();
                cache.put(className, parameterLoad); // 保存到缓存
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        parameterLoad.getParameter(activity); // 最终的执行
    }
}
