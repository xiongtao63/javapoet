package com.xiangxue.new_modular_customarouter;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.xiangxue.arouter_annotation.ARouter;
import com.xiangxue.arouter_annotation.bean.RouterBean;
import com.xiangxue.arouter_api.ARouterPath;
import com.xiangxue.common.utils.Cons;
import com.xiangxue.new_modular_customarouter.apt_create_test.ARouter$$Group$$personal;
import com.xiangxue.new_modular_customarouter.apt_create_test.ARouter$$Path$$personal;
import com.xiangxue.new_modular_customarouter.order.BuildConfig;
import com.xiangxue.new_modular_customarouter.order.Order_MainActivity;
import com.xiangxue.new_modular_customarouter.personal.Personal_MainActivity;

import java.util.Map;

@ARouter(path = "/app/MainActivity")
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (BuildConfig.isRelease) {
            Log.e(Cons.TAG, "当前为：集成化模式，除app可运行，其他子模块都是Android Library");
        } else {
            Log.e(Cons.TAG, "当前为：组件化模式，app/order/personal子模块都可独立运行");
        }
    }

    public void jumpOrder(View view) {
        Intent intent = new Intent(this, Order_MainActivity.class);
        intent.putExtra("name", "derry");
        startActivity(intent);
    }

    public void jumpPersonal(View view) {
        // 以前是这样跳转
        /*Intent intent = new Intent(this, Personal_MainActivity.class);
        intent.putExtra("name", "derry");
        startActivity(intent);*/
        
        // 现在是这样跳转
        ARouter$$Group$$personal group$$personal = new ARouter$$Group$$personal();
        Map<String, Class<? extends ARouterPath>> groupMap = group$$personal.getGroupMap();
        Class<? extends ARouterPath> myClass = groupMap.get("personal");

        try {
            ARouter$$Path$$personal path = (ARouter$$Path$$personal) myClass.newInstance();
            Map<String, RouterBean> pathMap = path.getPathMap();
            RouterBean bean = pathMap.get("/personal/Personal_MainActivity");

            if (bean != null) {
                Intent intent = new Intent(this, bean.getMyClass());
                startActivity(intent);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
