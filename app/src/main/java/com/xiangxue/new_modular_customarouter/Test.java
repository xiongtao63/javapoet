package com.xiangxue.new_modular_customarouter;

import com.xiangxue.arouter_annotation.ARouter;
import com.xiangxue.arouter_annotation.Parameter;

// @ARouter(path = "/app/Test")
public class Test {

    /*@Parameter
    String name;

    @Parameter
    int age;*/

    /**
     * 路由表，模仿ARouter框架：
     *
     * Group： 带头大哥 （标准）
     *
     * ​	app，order，person
     *
     *
     *
     * Path：一群小弟 （标准）
     *
     * ​	[
     *
     * ​		path= "/app/MainActivity"  : "MainActivity.class"   == RouterBean(class)
     *
     * ​	    path= "/app/MainActivity2"  : "MainActivity2.class"
     *
     * ​		path= "/app/MainActivity3"  : "MainActivity3.class"
     *
     * ​        ....
     *
     * ​	]
     */

    // Group： key=="app"     value="ARouter$$Path$$app"       ---------   /app/MainActivity

    // Path:   key=="/app/MainActivity"   value="MainActivity.class的封装类 RouterBean"

}
