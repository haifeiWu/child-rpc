package cn.whforever.example.zookeeper;

import cn.whforever.core.config.RegistryConfig;
import cn.whforever.core.registry.zk.ZookeeperRegistry;
import cn.whforever.core.rpc.RpcConstants;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author wuhaifei 2019-08-01
 */
public class ZkRegistryTest {

    @Test
    public void initTest() {
        RegistryConfig registryConfig = new RegistryConfig()
                .setProtocol(RpcConstants.ZOOKEEPER)
                .setAddress("127.0.0.1:2181");

        ZookeeperRegistry registry = new ZookeeperRegistry(registryConfig);
        registry.init();
        boolean started = registry.start();
        Assert.assertEquals(true, started);
    }
}
