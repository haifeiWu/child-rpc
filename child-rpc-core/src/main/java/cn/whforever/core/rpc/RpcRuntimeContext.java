package cn.whforever.core.rpc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 全局的运行时上下文
 *
 * @author wuhaifei
 * @date 2018/08/30
 */
public class RpcRuntimeContext {
    /**
     * slf4j Logger for this class
     */
    private final static Logger LOGGER = LoggerFactory.getLogger(RpcRuntimeContext.class);

    /**
     * 上下文信息，例如instancekey，本机ip等信息
     */
    private final static ConcurrentHashMap CONTEXT = new ConcurrentHashMap();

    /**
     * 当前进程Id
     */
    public static final String PID = ManagementFactory
            .getRuntimeMXBean()
            .getName().split("@")[0];

    /**
     * 当前应用启动时间（用这个类加载时间为准）
     */
    public static final long START_TIME = now();

    /**
     * 发布的服务配置
     */
//    private final static ConcurrentHashSet<ProviderBootstrap> EXPORTED_PROVIDER_CONFIGS = new ConcurrentHashSet<ProviderBootstrap>();
//
//    /**
//     * 发布的订阅配置
//     */
//    private final static ConcurrentHashSet<ConsumerBootstrap> REFERRED_CONSUMER_CONFIGS = new ConcurrentHashSet<ConsumerBootstrap>();
//
//    /**
//     * 关闭资源的钩子
//     */
//    private final static List<Destroyable.DestroyHook> DESTROY_HOOKS             = new CopyOnWriteArrayList<Destroyable.DestroyHook>();


    static {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Welcome! Loading SOFA RPC Framework : {}, PID is:{}", "version 0.1", PID);
        }
//        put(RpcConstants.CONFIG_KEY_RPC_VERSION, Version.RPC_VERSION);
//        // 初始化一些上下文
//        initContext();
//        // 初始化其它模块
//        ModuleFactory.installModules();
//        // 增加jvm关闭事件
//        if (RpcConfigs.getOrDefaultValue(RpcOptions.JVM_SHUTDOWN_HOOK, true)) {
//            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    if (LOGGER.isWarnEnabled()) {
//                        LOGGER.warn("SOFA RPC Framework catch JVM shutdown event, Run shutdown hook now.");
//                    }
//                    destroy(false);
//                }
//            }, "SOFA-RPC-ShutdownHook"));
//        }
    }

    /**
     * 初始化一些上下文
     */
    private static void initContext() {
//        putIfAbsent(KEY_APPID, RpcConfigs.getOrDefaultValue(APP_ID, null));
//        putIfAbsent(KEY_APPNAME, RpcConfigs.getOrDefaultValue(APP_NAME, null));
//        putIfAbsent(KEY_APPINSID, RpcConfigs.getOrDefaultValue(INSTANCE_ID, null));
//        putIfAbsent(KEY_APPAPTH, System.getProperty("user.dir"));
    }

    /**
     * 获取当前时间，此处可以做优化
     *
     * @return 当前时间
     */
    public static long now() {
        return System.currentTimeMillis();
    }

    /**
     * 得到上下文信息
     *
     * @param key the key
     * @return the object
     * @see ConcurrentHashMap#get(Object)
     */
    public static Object get(String key) {
        return CONTEXT.get(key);
    }

    /**
     * 设置上下文信息（不存在才设置成功）
     *
     * @param key   the key
     * @param value the value
     * @return the object
     * @see ConcurrentHashMap#putIfAbsent(Object, Object)
     */
    public static Object putIfAbsent(String key, Object value) {
        return value == null ? CONTEXT.remove(key) : CONTEXT.putIfAbsent(key, value);
    }

    /**
     * 设置上下文信息
     *
     * @param key   the key
     * @param value the value
     * @return the object
     * @see ConcurrentHashMap#put(Object, Object)
     */
    public static Object put(String key, Object value) {
        return value == null ? CONTEXT.remove(key) : CONTEXT.put(key, value);
    }

    /**
     * 得到全部上下文信息
     *
     * @return the CONTEXT
     */
    public static ConcurrentHashMap getContext() {
        return new ConcurrentHashMap(CONTEXT);
    }
}
