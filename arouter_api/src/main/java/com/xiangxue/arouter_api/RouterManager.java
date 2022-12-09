package com.xiangxue.arouter_api;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.util.LruCache;

import androidx.annotation.RequiresApi;

import com.xiangxue.arouter_annotation.bean.RouterBean;

/**
 * 路由管理器，辅助完成交互通信
 * 模块化 组件化
 * 模块化Google推出AS建立的模式，已经了这个模式
 *  1.组件化：没有任何依赖，但是还能通信
 *  2.插件化：
 *
 *   * 1.查找  ARouter$$Group$$order  ----> ARouter$$Path$$order
 *  * 2.使用 ARouter$$Group$$order  ----> ARouter$$Path$$order
 *
 *  详细流程：
 *      1.拼接 找 ARouter$$Group$$personal
 *      2.进入 ARouter$$Group$$personal 调用函数返回groupMap
 *      3.执行 groupMap.get(group)  group == personal
 *      4.查找  ARouter$$Path$$personal.class
 *      5.进入  ARouter$$Path$$personal.class 执行函数
 *      6.执行 pathMap.get(path) path = "/personal/Personal_Main2Activity"
 *      7.拿到 RouterBean（Personal_MainActivity.class）
 *      8.startActivity（new Intent(this, Personal_MainActivity.class)）
 */
public class RouterManager {

    private String group; // 路由的组名 app，order，personal ...
    private String path;  // 路由的路径  例如：/order/Order_MainActivity

    /**
     * 上面定义的两个成员变量意义：
     * 1.拿到ARouter$$Group$$personal  根据组名 拿到 ARouter$$Path$$personal
     * 2.操作路径，通过路径 拿到  Personal_MainActivity.class，就可以实现跳转了
     */

    // 单例模式
    private static RouterManager instance;

    public static RouterManager getInstance() {
        if (instance == null) {
            synchronized (RouterManager.class) {
                if (instance == null) {
                    instance = new RouterManager();
                }
            }
        }
        return instance;
    }

    // 性能  LRU缓存
    private LruCache<String, ARouterGroup> groupLruCache;
    private LruCache<String, ARouterPath> pathLruCache;

    // 为了拼接，例如:ARouter$$Group$$personal
    private final static String FILE_GROUP_NAME = "ARouter$$Group$$";

    private RouterManager() {
        groupLruCache = new LruCache<>(100);
        pathLruCache = new LruCache<>(100);
    }

    /***
     * @param path 例如：/order/Order_MainActivity
     *      * @return
     */
    public BundleManager build(String path) {
        if (TextUtils.isEmpty(path) || !path.startsWith("/")) {
            throw new IllegalArgumentException("不按常理出牌 path乱搞的啊，正确写法：如 /order/Order_MainActivity");
        }

        // 同学可以自己增加
        // ...

        if (path.lastIndexOf("/") == 0) { // 只写了一个 /
            throw new IllegalArgumentException("不按常理出牌 path乱搞的啊，正确写法：如 /order/Order_MainActivity");
        }

        // 截取组名  /order/Order_MainActivity  finalGroup=order
        String finalGroup = path.substring(1, path.indexOf("/", 1)); // finalGroup = order

        if (TextUtils.isEmpty(finalGroup)) {
            throw new IllegalArgumentException("不按常理出牌 path乱搞的啊，正确写法：如 /order/Order_MainActivity");
        }

        // TODO  证明没有问题，没有抛出异常
        this.path =  path;  // 最终的效果：如 /order/Order_MainActivity
        this.group = finalGroup; // 例如：order，personal

        return new BundleManager();
    }

    // 真正完成跳转
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public Object navigation(Context context, BundleManager bundleManager) {
        // 拼接 ARouter$$Group$$order 才能找到
        // 例如：寻找 ARouter$$Group$$personal
        String groupClassName = context.getPackageName() + "." + FILE_GROUP_NAME + group;
        Log.e("derry >>>", "navigation: groupClassName=" + groupClassName);

        try {

            /**
             * TODO  Group 缓存
             */
            // 读取路由组Group类文件
            ARouterGroup loadGroup = groupLruCache.get(group);
            // 读取路由组Group类文件
            if (null == loadGroup) { // 缓存里面没有东东
                // 加载APT路由组Group类文件 例如：ARouter$$Group$$order
                Class<?> aClass = Class.forName(groupClassName);
                // 初始化类文件
                loadGroup = (ARouterGroup) aClass.newInstance();

                // 保存到缓存
                groupLruCache.put(group, loadGroup);
            }

            if (loadGroup.getGroupMap().isEmpty()) {
                throw new RuntimeException("路由表Group报废了...");
            }

            /**
             * TODO  PATH 缓存
             */
            // 读取路由Path类文件
            ARouterPath loadPath = pathLruCache.get(path);
            if (null == loadPath) { // 缓存里面没有东东 Path
                // 1.invoke loadGroup
                // 2.Map<String, Class<? extends ARouterLoadPath>>
                Class<? extends ARouterPath> clazz = loadGroup.getGroupMap().get(group);
                // 3.从map里面获取 ARouter$$Path$$order.class
                loadPath = clazz.newInstance();

                // 保存到缓存
                pathLruCache.put(path, loadPath);
            }

            if (loadPath != null) { // 健壮
                if (loadPath.getPathMap().isEmpty()) {
                    throw new RuntimeException("路由表Path报废了...");
                }

                // 我们已经进入 PATH 函数 ，开始拿 Class 进行跳转
                RouterBean routerBean = loadPath.getPathMap().get(path);
                if (routerBean != null) {
                    switch (routerBean.getTypeEnum()) {
                        case ACTIVITY:
                            Intent intent = new Intent(context, routerBean.getMyClass()); // 例如：getClazz == Order_MainActivity.class
                            intent.putExtras(bundleManager.getBundle()); // 携带参数
                            context.startActivity(intent, bundleManager.getBundle());
                            break;

                        // 同学们可以自己扩展 类型
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
