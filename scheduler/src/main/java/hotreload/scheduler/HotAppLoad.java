package hotreload.scheduler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

import hotreload.api.App;
import hotreload.api.AppConfig;
import hotreload.api.Context;

class HotAppLoad extends Thread {

    private static HotAppLoad instance = new HotAppLoad();

    private List<File> appDirs = new CopyOnWriteArrayList<>();
    private volatile Map<String, String> loadedApp = new HashMap<>();
    private Map<String, String> newLoadedApp = new HashMap<>();

    private Context context;

    private int scanInterval = 60;
    private String appTempDir = "hotload\\scheduler\\apps\\";

    private HotAppLoad() {
    }

    public void init(String dir, Context context) {
        addDir(dir);
        this.context = context;
        System.out.println("HotAppLoad init");
    }

    public static HotAppLoad getInstance() {
        return instance;
    }

    public void addDir(String dir) {
        File file = new File(dir);
        if (file.exists() && file.isDirectory() && file.canRead()) {
            appDirs.add(file);
        }
    }

    public void removeDir(String dir) {
        appDirs.remove(new File(dir));
    }

    // 反射加载app
    private void loadApp(File jar, String md5, AppConfig appConfig) {
        try {
            File jarTemp = new File(appTempDir, appConfig.getAppName());
            if (Utils.copyFile(jar, jarTemp)) {
                URLClassLoader classLoader = new URLClassLoader(new URL[] { jarTemp.toURI().toURL() });
                Class<App> clazz = (Class<App>) classLoader.loadClass(appConfig.getStartClass());
                App newApp = clazz.newInstance();
                newApp.init(appConfig);
                newLoadedApp.put(newApp.getName(), md5);
                // classLoader.close();
                Engine.getInstance().addApp(newApp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 检查app是否已加载，或者更新
    private void checkApp(File jar, File config) {
        String md5 = Utils.getFileMD5(jar);
        if (md5 == null)
            return;
        try {
            AppConfig appConfig = new AppConfig(config);
            String _md5 = loadedApp.get(appConfig.getAppName());
            if (_md5 == null) {
                System.out.println("find a new app:" + appConfig.getAppName());
                loadApp(jar, md5, appConfig);
            } else if (!md5.equals(_md5)) {
                System.out.println("modified app:" + appConfig.getAppName());
                Engine.getInstance().removeApp(appConfig.getAppName());
                loadApp(jar, md5, appConfig);
            } else {
                if (!Engine.getInstance().getApp(appConfig.getAppName()).getConfig().equals(appConfig)) {
                    // TODO reload config
                    System.out.println("modified config:" + appConfig.getAppName());
                }
                newLoadedApp.put(appConfig.getAppName(), md5);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 扫描app目录，查找必要的文件
    private void checkDir(File dir) {
        File jar = null;
        File config = null;
        for (File file : dir.listFiles()) {
            if (file.isFile()) {
                if (file.getName().endsWith(".jar"))
                    jar = file;
                else if (file.getName().equals("config.properties"))
                    config = file;
                if (jar != null && config != null) {
                    checkApp(jar, config);
                    return;
                }
            }
        }
    }

    // 从所有目录扫描app
    private void scanApps() {
        System.out.println("begin scan apps size:" + loadedApp.size());
        for (File appDir : appDirs) {
            System.out.println("begin scan dir:" + appDir.getAbsolutePath());
            for (File dir : appDir.listFiles()) {
                if (dir.isDirectory())
                    checkDir(dir);
            }
        }
        for (String name : loadedApp.keySet()) {
            if (!newLoadedApp.containsKey(name)) {
                Engine.getInstance().removeApp(name);
                File jarTemp = new File(appTempDir, name);
                jarTemp.delete();
            }
        }
        Map<String, String> oldApp = loadedApp;
        loadedApp = newLoadedApp;
        oldApp.clear();
        newLoadedApp = oldApp;
        System.out.println("finish scan apps size:" + loadedApp.size());
    }

    @Override
    public void run() {
        while (!interrupted()) {
            scanApps();
            try {
                sleep(scanInterval * 1000);
            } catch (Exception e) {
                return;
            }
        }
    }

}