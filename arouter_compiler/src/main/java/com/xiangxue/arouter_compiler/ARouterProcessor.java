package com.xiangxue.arouter_compiler;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;
import com.xiangxue.arouter_annotation.ARouter;
import com.xiangxue.arouter_annotation.bean.RouterBean;
import com.xiangxue.arouter_compiler.utils.ProcessorConfig;
import com.xiangxue.arouter_compiler.utils.ProcessorUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

/**
 * 同学们注意：编码此类，记住就是一个字（细心，细心，细心），出了问题debug真的不好调试
 */

// AutoService则是固定的写法，加个注解即可
// 通过auto-service中的@AutoService可以自动生成AutoService注解处理器，用来注册
// 用来生成 META-INF/services/javax.annotation.processing.Processor 文件
@AutoService(Processor.class)

// 允许/支持的注解类型，让注解处理器处理
@SupportedAnnotationTypes({ProcessorConfig.AROUTER_PACKAGE})

// 指定JDK编译版本
@SupportedSourceVersion(SourceVersion.RELEASE_7)

// 注解处理器接收的参数
@SupportedOptions({ProcessorConfig.OPTIONS, ProcessorConfig.APT_PACKAGE})

public class ARouterProcessor extends AbstractProcessor {

    // 操作Element的工具类（类，函数，属性，其实都是Element）
    private Elements elementTool;

    // type(类信息)的工具类，包含用于操作TypeMirror的工具方法
    private Types typeTool;

    // Message用来打印 日志相关信息
    private Messager messager;

    // 文件生成器， 类 资源 等，就是最终要生成的文件 是需要Filer来完成的
    private Filer filer;

    private String options;  // （模块传递过来的）模块名  app，personal
    private String aptPackage; // （模块传递过来的） 包名

    // 仓库一  PATH
    private Map<String, List<RouterBean>> mAllPathMap = new HashMap<>();

    // 仓库二 GROUP
    private Map<String, String> mAllGroupMap = new HashMap<>();

    // 做初始化工作，就相当于 Activity中的 onCreate函数一样的作用
    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);

        elementTool = processingEnvironment.getElementUtils();
        messager = processingEnvironment.getMessager();
        filer = processingEnvironment.getFiler();
        typeTool = processingEnvironment.getTypeUtils();

        // 只有接受到 App壳 传递过来的书籍，才能证明我们的 APT环境搭建完成
        options = processingEnvironment.getOptions().get(ProcessorConfig.OPTIONS);
        aptPackage = processingEnvironment.getOptions().get(ProcessorConfig.APT_PACKAGE);
        messager.printMessage(Diagnostic.Kind.NOTE, ">>>>>>>>>>>>>>>>>>>>>> options:" + options);
        messager.printMessage(Diagnostic.Kind.NOTE, ">>>>>>>>>>>>>>>>>>>>>> aptPackage:" + aptPackage);
        if (options != null && aptPackage != null) {
            messager.printMessage(Diagnostic.Kind.NOTE, "APT 环境搭建完成....");
        } else {
            messager.printMessage(Diagnostic.Kind.NOTE, "APT 环境有问题，请检查 options 与 aptPackage 为null...");
        }
    }

    /**
     * 相当于main函数，开始处理注解
     * 注解处理器的核心方法，处理具体的注解，生成Java文件
     *
     * @param set              使用了支持处理注解的节点集合
     * @param roundEnvironment 当前或是之前的运行环境,可以通过该对象查找的注解。
     * @return true 表示后续处理器不会再处理（已经处理完成）
     */
    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        if (set.isEmpty()) {
            messager.printMessage(Diagnostic.Kind.NOTE, "并没有发现 被@ARouter注解的地方呀");
            return false;
        }

        // Activity type
        TypeElement activityType = elementTool.getTypeElement(ProcessorConfig.ACTIVITY_PACKAGE);
        TypeMirror activityMirror = activityType.asType();

        // 获取所有被 @ARouter 注解的 元素集合
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(ARouter.class);
        // 遍历所有的类节点
        for (Element element : elements) {  // 有三处地方使用到注解  for 3
            // 获取类节点，获取包节点 （com.xiangxue.xxxxxx）
            // String packageName = elementTool.getPackageOf(element).getQualifiedName().toString();

            // 获取简单类名，例如：MainActivity
            String className = element.getSimpleName().toString();
            messager.printMessage(Diagnostic.Kind.NOTE, "被@ARetuer注解的类有：" + className); // 打印出 就证明APT没有问题

            // 毒已经解除了
            /*// 方法
            MethodSpec mainMethod = MethodSpec.methodBuilder("main")
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .returns(void.class)
                    .addParameter(System[].class, "args")
                    // 增加main方法里面的内容
                    .addStatement("$T.out.println($S)", System.class, "AAAAAAAAAAA!")
                    .build();
            // 类
            TypeSpec testClass = TypeSpec.classBuilder("Test" + options) // app,order,personal   Testapp  Testorder Testpersonal
                    .addMethod(mainMethod)
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .build();

            // 包
            JavaFile packagef = JavaFile.builder("com.xiangxue.test22", testClass).build();

            try {
                packagef.writeTo(filer);
            } catch (IOException e) {
                e.printStackTrace();
                messager.printMessage(Diagnostic.Kind.NOTE, "生成Test文件时失败，异常:" + e.getMessage());
            }*/

            // 拿到注解
            ARouter aRouter = element.getAnnotation(ARouter.class);

            // TODO PATH
            RouterBean routerBean = new RouterBean.Builder()
                    .addGroup(aRouter.group())
                    .addPath(aRouter.path())
                    .addElement(element)
                    .build();

            // 必须是Activity
            TypeMirror elementMirror = element.asType(); // 当前 == Activity
            if (typeTool.isSubtype(elementMirror, activityMirror)) {
                routerBean.setTypeEnum(RouterBean.TypeEnum.ACTIVITY);
            } else {
                // 不匹配抛出异常，这里谨慎使用！考虑维护问题
                throw new RuntimeException("@ARouter注解目前仅限用于Activity类之上");
            }

            // 校验 path  group  用户传递过来的
            if (checkRouterPath(routerBean)) {
                messager.printMessage(Diagnostic.Kind.NOTE, "RouterBean Check Success:" + routerBean.toString());

                // PATH 仓库一
                List<RouterBean> routerBeans = mAllPathMap.get(routerBean.getGroup());

                // 如果从Map中找不到key为：bean.getGroup()的数据，就新建List集合再添加进Map
                if (ProcessorUtils.isEmpty(routerBeans)) {
                    routerBeans = new ArrayList<>();
                    routerBeans.add(routerBean);
                    mAllPathMap.put(routerBean.getGroup(), routerBeans);
                } else { // 从Map中找到key中返回的Value值routerBeans，就直接添加进去
                    routerBeans.add(routerBean);
                }
            } else {
                messager.printMessage(Diagnostic.Kind.ERROR, "@ARouter注解未按规范配置，如：/app/MainActivity");
            }

        } // for end

        // 定义（拿到标准 TYPE） PATH  GROUP
        TypeElement pathType = elementTool.getTypeElement(ProcessorConfig.AROUTER_API_PATH);
        TypeElement groupType = elementTool.getTypeElement(ProcessorConfig.AROUTER_API_GROUP);

        // TODO 第一大步：PAHT  一群小弟
        try {
            createPathFile(pathType);
        } catch (IOException e) {
            e.printStackTrace();
            messager.printMessage(Diagnostic.Kind.NOTE, "在生成PATH模板时，异常了 e:" + e.getMessage());
        }

        // TODO 第二大步：GROUP  group的仓库 + 1（path）   组头（带头大哥）
        try {
            createGroupFile(groupType, pathType);
        } catch (IOException e) {
            e.printStackTrace();
            messager.printMessage(Diagnostic.Kind.NOTE, "在生成GROUP模板时，异常了 e:" + e.getMessage());
        }


        return true; // 坑：必须写返回值，表示处理@ARouter注解完成
    }

    /**
     * 校验@ARouter注解的值，如果group未填写就从必填项path中截取数据
     *
     * @param bean 路由详细信息，最终实体封装类
     */
    private final boolean checkRouterPath(RouterBean bean) {
        String group = bean.getGroup(); //  同学们，一定要记住： "app"   "order"   "personal"
        String path = bean.getPath();   //  同学们，一定要记住： "/app/MainActivity"   "/order/Order_MainActivity"   "/personal/Personal_MainActivity"

        // @ARouter注解中的path值，必须要以 / 开头（模仿阿里Arouter规范）
        if (ProcessorUtils.isEmpty(path) || !path.startsWith("/")) {
            // ERROR 故意去奔溃的
            messager.printMessage(Diagnostic.Kind.ERROR, "@ARouter注解中的path值，必须要以 / 开头");
            return false;
        }

        // 比如开发者代码为：path = "/MainActivity"，最后一个 / 符号必然在字符串第1位
        if (path.lastIndexOf("/") == 0) {
            // 架构师定义规范，让开发者遵循
            messager.printMessage(Diagnostic.Kind.ERROR, "@ARouter注解未按规范配置，如：/app/MainActivity");
            return false;
        }

        // 从第一个 / 到第二个 / 中间截取，如：/app/MainActivity 截取出 app 作为group
        String finalGroup = path.substring(1, path.indexOf("/", 1));
        // finalGroup == app, personal, order

        // @ARouter注解中的group有赋值情况   用户传递进来时 order，  我截取出来的也必须是 order
        if (! ProcessorUtils.isEmpty(group) && ! group.equals(options)) {
            // 架构师定义规范，让开发者遵循
            messager.printMessage(Diagnostic.Kind.ERROR, "@ARouter注解中的group值必须和子模块名一致！");
            return false;
        } else {
            bean.setGroup(finalGroup); // 赋值  order 添加进去了
        }

        return true;
    }

    /**
     * TODO　PATH　生成
     * @param pathType
     * @throws IOException
     */
    private final void createPathFile(TypeElement pathType) throws IOException {
        // 判断 map仓库中，是否有需要生成的文件
        if (ProcessorUtils.isEmpty(mAllPathMap)) {
            return;
        }

        // Map<String, RouterBean>  返回值
        TypeName methodReturn = ParameterizedTypeName.get(
                ClassName.get(Map.class), // Map
                ClassName.get(String.class), // Map<String,
                ClassName.get(RouterBean.class) // Map<String, RouterBean>
        );

        for (Map.Entry<String, List<RouterBean>> entry : mAllPathMap.entrySet()) {
            // 1.方法
            MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(ProcessorConfig.PATH_METHOD_NAME)
                    .addAnnotation(Override.class) // 给方法上添加注解
                    .addModifiers(Modifier.PUBLIC) // public修饰符
                    .returns(methodReturn)
                    ;
            //  Map<String, RouterBean> pathMap = new HashMap<>();
            methodBuilder.addStatement("$T<$T, $T> $N = new $T<>()",
                    ClassName.get(Map.class), // Map
                    ClassName.get(String.class), // String
                    ClassName.get(RouterBean.class), // RouterBean
                    ProcessorConfig.PATH_VAR1,
                    ClassName.get(HashMap.class)
                    );

            List<RouterBean> pathList = entry.getValue();

            for (RouterBean routerBean : pathList) {
                /**
                 * pathMap.put("/personal/Personal_MainActivity",
                 *                 RouterBean.create(RouterBean.TypeEnum.ACTIVITY,
                 *                                   Order_MainActivity.class,
                 *                                  "/personal/Personal_MainActivity",
                 *                                  "personal")
                 *             );
                 */
                // 给方法添加代码
                methodBuilder.addStatement(
                        "$N.put($S, $T.create($T.$L, $T.class, $S, $S))",
                        ProcessorConfig.PATH_VAR1, // pathMap.put
                        routerBean.getPath(), // "/app/MainActivity"
                        ClassName.get(RouterBean.class), // RouterBean
                        ClassName.get(RouterBean.TypeEnum.class), // RouterBean.Type
                        routerBean.getTypeEnum(), // 枚举类型：ACTIVITY
                        ClassName.get((TypeElement) routerBean.getElement()), // MainActivity.class
                        routerBean.getPath(), // 路径名
                        routerBean.getGroup() // 组名
                );
            } // for end

            //  return pathMap;
            methodBuilder.addStatement("return $N", ProcessorConfig.PATH_VAR1);

            // 最终生成的类文件名  ARouter$$Path$$  + personal
            String finalClassName = ProcessorConfig.PATH_FILE_NAME + entry.getKey();

            // 生成 和 类 等等，结合一体
            JavaFile.builder(aptPackage, // 包名
                    TypeSpec.classBuilder(finalClassName) // 类名
                            .addSuperinterface(ClassName.get(pathType)) // 实现ARouterLoadPath接口
                            .addModifiers(Modifier.PUBLIC) // public修饰符
                            .addMethod(methodBuilder.build()) // 方法的构建（方法参数 + 方法体）
                            .build()) // 类构建完成
                    .build() // JavaFile构建完成
                    .writeTo(filer); // 文件生成器开始生成类文件

            // 告诉Group
            mAllGroupMap.put(entry.getKey(), finalClassName);

            // PATH 全部结束
        }
    }

    /**
     * TODO GROUP 生成
     * @param groupType
     * @param pathType
     * @throws IOException
     */
    private void createGroupFile(TypeElement groupType, TypeElement pathType) throws IOException {

        // 判断是否有需要生成的类文件
        if (ProcessorUtils.isEmpty(mAllGroupMap) || ProcessorUtils.isEmpty(mAllPathMap)) return;

        // Map<String, Class<? extends ARouterPath>>  返回参数
        TypeName methodReturns = ParameterizedTypeName.get(
                ClassName.get(Map.class), // Map
                ClassName.get(String.class), // Map<String,
                // 第二个参数：Class<? extends ARouterLoadPath>
                // 某某Class是否属于ARouterLoadPath接口的实现类
                // <>
                ParameterizedTypeName.get(ClassName.get(Class.class),
                        WildcardTypeName.subtypeOf(ClassName.get(pathType))));

        // 方法架子
        MethodSpec.Builder methodBuidler = MethodSpec.methodBuilder(ProcessorConfig.GROUP_METHOD_NAME) // 方法名
                .addAnnotation(Override.class) // 重写注解
                .addModifiers(Modifier.PUBLIC) // public修饰符
                .returns(methodReturns); // 方法返回值

        // Map<String, Class<? extends ARouterPath>> groupMap = new HashMap<>();
        methodBuidler.addStatement("$T<$T, $T> $N = new $T<>()",
                ClassName.get(Map.class),
                ClassName.get(String.class),
                ParameterizedTypeName.get(ClassName.get(Class.class),
                        WildcardTypeName.subtypeOf(ClassName.get(pathType))),
                ProcessorConfig.GROUP_VAR1,
                HashMap.class);

        // 方法内容配置
        for (Map.Entry<String, String> entry : mAllGroupMap.entrySet()) {
            // groupMap.put("personal", ARouter$$Path$$personal.class);
            methodBuidler.addStatement("$N.put($S, $T.class)",
                    ProcessorConfig.GROUP_VAR1, // groupMap.put
                    entry.getKey(),
                    // 类文件在指定包名下
                    ClassName.get(aptPackage, entry.getValue()));
        }

        // 遍历之后：return groupMap;
        methodBuidler.addStatement("return $N", ProcessorConfig.GROUP_VAR1);

        // 最终生成的类文件名   ARouter$$Group$$ + personal
        String finalClassName = ProcessorConfig.GROUP_FILE_NAME + options;
        messager.printMessage(Diagnostic.Kind.NOTE, "APT生成路由组Group类文件：" +
                aptPackage + "." + finalClassName);

        // 生成类文件：ARouter$$Group$$app
        JavaFile.builder(aptPackage, // 包名
                TypeSpec.classBuilder(finalClassName) // 类名
                        .addSuperinterface(ClassName.get(groupType)) // 实现ARouterLoadGroup接口
                        .addModifiers(Modifier.PUBLIC) // public修饰符
                        .addMethod(methodBuidler.build()) // 方法的构建（方法参数 + 方法体）
                        .build()) // 类构建完成
                .build() // JavaFile构建完成
                .writeTo(filer); // 文件生成器开始生成类文件
    }
}
