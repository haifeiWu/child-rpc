package cn.whforever.core.registry;

import cn.whforever.core.config.ClientConfig;
import cn.whforever.core.config.RegistryConfig;
import cn.whforever.core.config.ServerConfig;
import cn.whforever.core.exception.ChildRpcRuntimeException;
import cn.whforever.core.register.Registry;
import cn.whforever.core.util.CommonUtils;
import cn.whforever.core.util.StringUtils;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static cn.whforever.core.util.RegistryUtils.buildConsumerPath;
import static cn.whforever.core.util.RegistryUtils.buildProviderPath;
import static cn.whforever.core.util.StringUtils.CONTEXT_SEP;

/**
 * <pre>
 *  在zookeeper上存放的数据结构为：
 *  -$rootPath (根路径)
 *         └--child-rpc
 *             |--cn.whforever.rpc.example.HelloService （服务）
 *             |       |-providers （服务提供者列表）
 *             |       |     |--192.168.1.100:22000
 *             |       |     |--192.168.1.110:23000
 *             |       |     └--192.168.1.120:2400
 *             |       |-consumers （服务调用者列表）
 *             |       |     |--192.168.3.100
 *             |       |     |--192.168.3.110
 *             |       |     └--192.168.3.120
 *             |--cn.whforever.rpc.example.EchoService （下一个服务）
 *             | ......
 *  </pre>
 *
 * @author wuhf
 * @Date 2018/9/12 16:27
 **/
public class ZookeeperRegistry extends Registry {
    /**
     * slf4j Logger for this class
     */
    private final static Logger LOGGER = LoggerFactory.getLogger(ZookeeperRegistry.class);

    /**
     * 注册中心配置
     *
     * @param registryConfig 注册中心配置
     */
    public ZookeeperRegistry(RegistryConfig registryConfig) {
        super(registryConfig);
    }

    /**
     * 配置项：是否本地优先
     */
    public final static String PARAM_PREFER_LOCAL_FILE = "preferLocalFile";

    /**
     * 配置项：是否使用临时节点。<br>
     * 如果使用临时节点：那么断开连接的时候，将zookeeper将自动消失。好处是如果服务端异常关闭，也不会有垃圾数据。<br>
     * 坏处是如果和zookeeper的网络闪断也通知客户端，客户端以为是服务端下线<br>
     * 如果使用永久节点：好处：网络闪断时不会影响服务端，而是由客户端进行自己判断长连接<br>
     * 坏处：服务端如果是异常关闭（无反注册），那么数据里就由垃圾节点，得由另外的哨兵程序进行判断
     */
    public final static String PARAM_CREATE_EPHEMERAL = "createEphemeral";

    /**
     * 服务被下线
     */
    private final static byte[] PROVIDER_OFFLINE = new byte[]{0};

    /**
     * 正常在线服务
     */
    private final static byte[] PROVIDER_ONLINE = new byte[]{1};

    /**
     * Zookeeper zkClient
     */
    private CuratorFramework zkClient;

    /**
     * Root path of registry data
     */
    private String rootPath;

    /**
     * Prefer get data from local file to remote zk cluster.
     *
     * @see ZookeeperRegistry#PARAM_PREFER_LOCAL_FILE
     */
    private boolean preferLocalFile = false;

    /**
     * Create EPHEMERAL node when true, otherwise PERSISTENT
     *
     * @see ZookeeperRegistry#PARAM_CREATE_EPHEMERAL
     * @see CreateMode#PERSISTENT
     * @see CreateMode#EPHEMERAL
     */
    private boolean ephemeralNode = true;

    /**
     * 保存服务发布者的url
     */
    private Map<ServerConfig, String> providerUrls = new ConcurrentHashMap<>();

    /**
     * 保存服务消费者的url
     */
    private Map<ClientConfig, String> consumerUrls = new ConcurrentHashMap<>();

    /**
     * 初始化zookeeper
     */
    @Override
    public synchronized void init() {
        if (zkClient != null) {
            return;
        }

        // xxx:2181,yyy:2181/path1/paht2
        String addressInput = registryConfig.getAddress();
        if (StringUtils.isEmpty(addressInput)) {
            throw new ChildRpcRuntimeException("Address of zookeeper registry is empty.");
        }

        int idx = addressInput.indexOf(CONTEXT_SEP);
        String address; // IP地址
        if (idx > 0) {
            address = addressInput.substring(0, idx);
            rootPath = addressInput.substring(idx);
            if (!rootPath.endsWith(CONTEXT_SEP)) {
                // 保证以"/"结尾
                rootPath += CONTEXT_SEP;
            }
        } else {
            address = addressInput;
            rootPath = CONTEXT_SEP;
        }
        preferLocalFile = !CommonUtils.isFalse(registryConfig.getParameter(PARAM_PREFER_LOCAL_FILE));
        ephemeralNode = !CommonUtils.isFalse(registryConfig.getParameter(PARAM_CREATE_EPHEMERAL));

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(
                    "Init ZookeeperRegistry with address {}, root path is {}. preferLocalFile:{}, ephemeralNode:{}",
                    address, rootPath, preferLocalFile, ephemeralNode);
        }

        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        zkClient = CuratorFrameworkFactory.builder()
                .connectString(address)
                .sessionTimeoutMs(registryConfig.getConnectTimeout() * 3)
                .connectionTimeoutMs(registryConfig.getConnectTimeout())
                .sessionTimeoutMs(1000)
                .connectionTimeoutMs(1000)
                .canBeReadOnly(false)
                .retryPolicy(retryPolicy)
                .defaultData(null)
                .build();
    }

    @Override
    public synchronized boolean start() {
        if (zkClient == null) {
            LOGGER.warn("Start zookeeper registry must be do init first!");
            return false;
        }
        if (zkClient.getState() == CuratorFrameworkState.STARTED) {
            return true;
        }
        try {
            zkClient.start();
        } catch (Exception e) {
            throw new ChildRpcRuntimeException("Failed to start zookeeper zkClient", e);
        }
        return zkClient.getState() == CuratorFrameworkState.STARTED;
    }

    @Override
    public void destroy() {
        if (zkClient != null && zkClient.getState() == CuratorFrameworkState.STARTED) {
            zkClient.close();
        }
        providerUrls.clear();
        consumerUrls.clear();
    }

    @Override
    public void destroy(DestroyHook hook) {
        hook.preDestroy();
        destroy();
        hook.postDestroy();
    }

    @Override
    public void register(ServerConfig config) {
        String appName = (String) config.getInterfaceId();
        if (!registryConfig.isRegister()) {
            // do some log
            return;
        }
        if (config.isRegister()) {
            // 注册服务端节点
            try {
                String url = ZookeeperRegistryHelper.convertProviderToUrls(config);
                if (StringUtils.isNotBlank(url)) {
                    String providerPath = buildProviderPath(rootPath, config);
                    // do some logger
                    url = URLEncoder.encode(url, "UTF-8");
                    String providerUrl = providerPath + CONTEXT_SEP + url;
                    getAndCheckZkClient().create().creatingParentContainersIfNeeded()
                            .withMode(ephemeralNode ? CreateMode.EPHEMERAL : CreateMode.PERSISTENT) // 是否永久节点
//                                .forPath(providerUrl, config.isDynamic() ? PROVIDER_ONLINE : PROVIDER_OFFLINE); // 是否默认上下线
                            .forPath(providerUrl, PROVIDER_ONLINE);
                    // do some log
                    providerUrls.put(config, url);
                    // do some log
                }
            } catch (Exception e) {
                throw new ChildRpcRuntimeException("Failed to register provider to zookeeperRegistry!", e);
            }
        }
    }

    @Override
    public void unRegister(ServerConfig config) {
        String appName = (String) config.getInterfaceId();
        if (!registryConfig.isRegister()) {
            // 注册中心不注册 do some log
            return;
        }
        // 反注册服务端节点
        if (config.isRegister()) {
            try {
                String url = providerUrls.remove(config);
                String providerPath = buildProviderPath(rootPath, config);
                url = URLEncoder.encode(url, "UTF-8");
                getAndCheckZkClient().delete().forPath(providerPath + CONTEXT_SEP + url);
                // do some log
            } catch (Exception e) {
                throw new ChildRpcRuntimeException("Failed to unregister provider to zookeeperRegistry!", e);
            }
        }
    }

    @Override
    public void batchUnRegister(List<ServerConfig> configs) {
//        // 一个一个来，后续看看要不要使用curator的事务
        for (ServerConfig config : configs) {
            unRegister(config);
        }
    }

    @Override
    public List<String> subscribe(final ClientConfig config) {
        String appName = config.getInterfaceId();
        if (!registryConfig.isSubscribe()) {
            // 注册中心不订阅
            LOGGER.info("服务未订阅 [{}]", appName);
            return new ArrayList<>();
        }
        // 注册Consumer节点
        if (config.isRegister()) {
            try {
                if (StringUtils.isBlank(consumerUrls.get(config))) {
                    String consumerPath = buildConsumerPath(rootPath, config);
                    String url = ZookeeperRegistryHelper.convertConsumerToUrl(config);
                    String encodeUrl = URLEncoder.encode(url, "UTF-8");
                    getAndCheckZkClient().create().creatingParentContainersIfNeeded()
                            // Consumer临时节点
                            .withMode(CreateMode.EPHEMERAL)
                            .forPath(consumerPath + CONTEXT_SEP + encodeUrl);
                    consumerUrls.put(config, url);
                }
            } catch (Exception e) {
                throw new ChildRpcRuntimeException("Failed to register consumer to zookeeperRegistry!", e);
            }
        }
        if (config.isSubscribe()) {
            // 订阅Providers节点
            try {
                final String providerPath = buildProviderPath(rootPath, config);
                // 监听配置节点下 子节点增加、子节点删除、子节点Data修改事件
                PathChildrenCache pathChildrenCache = new PathChildrenCache(zkClient, providerPath, true);
                pathChildrenCache.start(PathChildrenCache.StartMode.BUILD_INITIAL_CACHE);
                List<String> providerInfos = ZookeeperRegistryHelper.convertUrlsToProviders(providerPath, pathChildrenCache.getCurrentData());
                return providerInfos;
            } catch (Exception e) {
                throw new ChildRpcRuntimeException("Failed to subscribe provider from zookeeperRegistry!", e);
            }
        }
        return null;
    }

    @Override
    public void unSubscribe(ClientConfig config) {
        // 反注册服务端节点
        if (config.isRegister()) {
            try {
                String url = consumerUrls.remove(config);
                if (url != null) {
                    String consumerPath = buildConsumerPath(rootPath, config);
                    url = URLEncoder.encode(url, "UTF-8");
                    getAndCheckZkClient().delete().forPath(consumerPath + CONTEXT_SEP + url);
                }
            } catch (Exception e) {
//                if (!RpcRunningState.isShuttingDown()) {
                throw new ChildRpcRuntimeException("Failed to unregister consumer to zookeeperRegistry!", e);
//                }
            }
        }
    }

    @Override
    public void batchUnSubscribe(List<ClientConfig> configs) {
        // 一个一个来，后续看看要不要使用curator的事务
        for (ClientConfig config : configs) {
            unSubscribe(config);
        }
    }

    protected CuratorFramework getZkClient() {
        return zkClient;
    }

    private CuratorFramework getAndCheckZkClient() {
        if (zkClient == null || zkClient.getState() != CuratorFrameworkState.STARTED) {
            throw new ChildRpcRuntimeException("Zookeeper client is not available");
        }
        return zkClient;
    }
}
