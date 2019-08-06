package hotreload.scheduler;

import java.io.File;

import hotreload.api.Context;

public class Main {

    private Context context;

    Main() {
        context = new ContextImpl();
    }

    void init() {
        System.out.println("init");
        Engine.getInstance().init(context);
        HotAppLoad.getInstance().init("apps", context);
    }

    // 监视框架运行状态
    private void look_up() {
        for (;;) {
            try {
                Thread.sleep(60000);
                System.out.println("checked");
            } catch (Exception e) {
                return;
            }
        }
    }

    void start() {
        Engine.getInstance().start();
        HotAppLoad.getInstance().start();
        look_up();
    }

    public static void main(String[] args) {
        File directory = new File("");
        System.out.println(directory.getAbsolutePath());
        Main m = new Main();
        m.init();
        m.start();
    }
}