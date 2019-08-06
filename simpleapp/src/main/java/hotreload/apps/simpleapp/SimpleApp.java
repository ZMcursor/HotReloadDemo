package hotreload.apps.simpleapp;

import java.lang.reflect.Method;

import hotreload.api.App;
import hotreload.api.AppConfig;
import hotreload.api.Context;

public class SimpleApp implements App {

    private AppConfig config;

    @Override
    public String getName() {
        return config.getAppName();
    }

    @Override
    public void create(Context context) {
        System.out.println(getName() + " is created");
    }

    @Override
    public void start() {
        System.out.println(getName() + " is starting");
    }

    @Override
    public void doWork() {
        System.out.println(getName() + " is doing work");
        try {
            // 动态加载demo类
            // Class<?> clazz = Class.forName("hotreload.apps.simpleapp.demo");
            Class<?> clazz = getClass().getClassLoader().loadClass("hotreload.apps.simpleapp.demo");

            Object obj = clazz.newInstance();
            Method m = clazz.getMethod("work");
            m.invoke(obj);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        System.out.println(getName() + " is stoped");
    }

    @Override
    public void destroy() {
        System.out.println(getName() + " is destroyed");
    }

    @Override
    public void init(AppConfig config) {
        this.config = config;
        System.out.println(getName() + " is inited");
    }

    @Override
    public AppConfig getConfig() {
        return config;
    }

}