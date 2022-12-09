//package com.xiangxue.new_modular_customarouter.apt_create_test;
//
//import com.xiangxue.arouter_api.ARouterGroup;
//import com.xiangxue.arouter_api.ARouterPath;
//
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * 代表根据 组名“order” 对应---ARouterPath（ARouter$$Path$$order--(包含了很多的myClass)）
// */
//public class ARouter$$Group$$order implements ARouterGroup {
//
//    /**
//     * @return key：组名 如：“order”  ---- ARouter$$Path$$order--(包含了很多的myClass)
//     */
//    @Override
//    public Map<String, Class<? extends ARouterPath>> getGroupMap() {
//        Map<String, Class<? extends ARouterPath>> groupMap = new HashMap<>();
//        groupMap.put("order", ARouter$$Path$$order.class);
//        return groupMap;
//    }
//}
