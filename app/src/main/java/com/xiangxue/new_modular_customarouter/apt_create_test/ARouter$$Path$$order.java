//package com.xiangxue.new_modular_customarouter.apt_create_test;
//
//import com.xiangxue.arouter_annotation.bean.RouterBean;
//import com.xiangxue.arouter_api.ARouterPath;
//import com.xiangxue.new_modular_customarouter.order.Order_MainActivity;
//
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * 代表这个路径/order/Order_MainActivity下的所有 --- RouterBean.myClass
// */
//public class ARouter$$Path$$order implements ARouterPath {
//
//    /**
//     * @return map[key:路径 如: /order/Order_MainActivity, value:RouterBean.myClass]
//     */
//    @Override
//    public Map<String, RouterBean> getPathMap() {
//        Map<String, RouterBean> pathMap = new HashMap<>();
//        pathMap.put("/order/Order_MainActivity",
//                RouterBean.create(RouterBean.TypeEnum.ACTIVITY,
//                                  Order_MainActivity.class,
//                            "/order/Order_MainActivity",
//                                  "order"
//                                  ));
//        return pathMap;
//    }
//}
