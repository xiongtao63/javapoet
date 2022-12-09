package com.xiangxue.new_modular_customarouter.order;

import androidx.appcompat.app.AppCompatActivity;
import androidx.versionedparcelable.ParcelField;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.xiangxue.arouter_annotation.ARouter;
import com.xiangxue.arouter_annotation.Parameter;
import com.xiangxue.arouter_annotation.bean.RouterBean;
import com.xiangxue.arouter_api.ARouterPath;
import com.xiangxue.arouter_api.ParameterManager;
import com.xiangxue.arouter_api.RouterManager;
import com.xiangxue.common.utils.Cons;

import java.util.Map;

@ARouter(path = "/order/Order_MainActivity")
public class Order_MainActivity extends AppCompatActivity {

    @Parameter
    String name;

    /*@Parameter
    int age;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_activity_main);

        // 【懒加载】用到才加载，不用到就不加载
        ParameterManager.getInstance().loadParameter(this);

        Log.e(Cons.TAG, "order/Order_MainActivity name:" + name);
    }

    public void jumpApp(View view) {
        Toast.makeText(this, "路由还没有写好呢，别猴急...", Toast.LENGTH_SHORT).show();
    }

    public void jumpPersonal(View view) {
        // Toast.makeText(this, "路由还没有写好呢，别猴急...", Toast.LENGTH_SHORT).show();

        RouterManager.getInstance()
                .build("/personal/Personal_MainActivity")
                .withString("name222", "Derry 66546436546")
                .withString("sex222", "男")
               .navigation(this);

        // 用户 作业 伪代码
        /*RouterManager.getInstance()
                .build("/personal/Personal_MainActivity")
                .withStringResutl("name222", "Derry 66546436546")
                .withStringResutl("sex222", "男")
                .navigation(this, 88);*/

    }
}
