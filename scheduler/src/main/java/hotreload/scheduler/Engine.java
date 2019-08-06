package hotreload.scheduler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import hotreload.api.App;
import hotreload.api.Context;

// app执行引擎，负责管理调度所有的app
class Engine extends Thread {

    private Context context;
    private static Engine instance = new Engine();

    // 保存所有的app
    private Map<String, App> apps = new ConcurrentHashMap<>();

    private Engine() {
    }

    void init(Context context) {
        this.context = context;
        System.out.println("Engine init");
    }

    static Engine getInstance() {
        return instance;
    }

    void addApp(App app) {
        apps.put(app.getName(), app);
        app.create(context);
        app.start();
    }

    App getApp(String name) {
        return apps.get(name);
    }

    void removeApp(String name) {
        App app = apps.remove(name);
        app.stop();
        app.destroy();
    }

    int getAppSize() {
        return apps.size();
    }

    private void schedule() {
        System.out.println("engine begin schedule");
        for (App app : apps.values()) {
            app.doWork();
        }
        System.out.println("engine finish schedule");
    }

    @Override
    public void run() {
        while (!interrupted()) {
            try {
                sleep(10 * 1000);
                schedule();
            } catch (Exception e) {
                System.out.println("engine schedule error:" + e);
                return;
            }
        }
    }

}