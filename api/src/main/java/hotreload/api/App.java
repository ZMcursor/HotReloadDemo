package hotreload.api;

// app定义，包含生命周期方法，由框架负责管理
public interface App {

    void init(AppConfig config);

    String getName();

    AppConfig getConfig();

    void create(Context context);

    void start();

    void doWork();

    void stop();

    void destroy();
}