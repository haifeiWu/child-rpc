package cn.whforever.core.util;

import cn.whforever.core.config.ClientConfig;
import cn.whforever.core.config.Config;
import cn.whforever.core.config.ServerConfig;
import cn.whforever.core.rpc.RpcConstants;

import java.util.HashMap;
import java.util.Map;

public class RegistryUtils {

    /**
     * Convert provider to url.
     *
     * @return the url list
     */
    public static String convertProviderToUrls(ServerConfig serverConfig) {
        @SuppressWarnings("unchecked")
        String url;
        StringBuilder sb = new StringBuilder();
        String host = serverConfig.getHost();
        if (host == null) {
            host = serverConfig.getHost();
            if (NetUtils.isLocalHost(host) || NetUtils.isAnyHost(host)) {
                host = SystemInfo.getLocalHost();
            }
        }

        Map<String, String> metaData = convertProviderToMap(serverConfig);
        //noinspection unchecked
        sb.append(host).append(":")
                .append(serverConfig.getPort());
        url = sb.toString();
        return url;
    }

    //
//    public static List<ProviderInfo> matchProviderInfos(ClientConfig consumerConfig, List<ProviderInfo> providerInfos) {
//        String protocol = consumerConfig.getProtocol();
//        List<ProviderInfo> result = new ArrayList<ProviderInfo>();
//        for (ProviderInfo providerInfo : providerInfos) {
//            if (providerInfo.getProtocolType().equalsIgnoreCase(protocol)
//                && StringUtils.equals(consumerConfig.getUniqueId(),
//                    providerInfo.getAttr(ProviderInfoAttrs.ATTR_UNIQUEID))) {
//                result.add(providerInfo);
//            }
//        }
//        return result;
//    }
//
    public static Map<String, String> convertProviderToMap(ServerConfig providerConfig) {
        Map<String, String> metaData = new HashMap<>(16);
        metaData.put(RpcConstants.CONFIG_KEY_INTERFACE, providerConfig.getInterfaceId());
        metaData.put(RpcConstants.CONFIG_KEY_PROTOCOL, providerConfig.getProtocol());
        return metaData;
    }

    /**
     * Convert consumer to url.
     *
     * @param consumerConfig the ConsumerConfig
     * @return the url list
     */
    public static String convertConsumerToUrl(ClientConfig consumerConfig) {
        String host = SystemInfo.getLocalHost();
        return host;
    }

    /**
     * Gets key pairs.
     *
     * @param key   the key
     * @param value the value
     * @return the key pairs
     */
    public static String getKeyPairs(String key, Object value) {
        if (value != null) {
            return "&" + key + "=" + value.toString();
        } else {
            return "";
        }
    }

    /**
     * 转换 map to url pair
     *
     * @param map 属性
     */
    private static String convertMap2Pair(Map<String, String> map) {

        if (CommonUtils.isEmpty(map)) {
            return StringUtils.EMPTY;
        }

        StringBuilder sb = new StringBuilder(128);
        for (Map.Entry<String, String> entry : map.entrySet()) {
            sb.append(getKeyPairs(entry.getKey(), entry.getValue()));
        }

        return sb.toString();
    }

    public static String buildProviderPath(String rootPath, Config config) {
        return rootPath + "child-rpc/" + config.getInterfaceId() + "/providers";
    }

    public static String buildConsumerPath(String rootPath, Config config) {
        return rootPath + "child-rpc/" + config.getInterfaceId() + "/consumers";
    }

//
//    /**
//     * Read the warmUp weight parameter,
//     * decide whether to switch the state to the preheating period,
//     * and set the corresponding parameters during the preheating period.
//     *
//     * @param providerInfo the provider info
//     */
//    public static void processWarmUpWeight(ProviderInfo providerInfo) {
//
//        String warmupTimeStr = providerInfo.getStaticAttr(ProviderInfoAttrs.ATTR_WARMUP_TIME);
//        String warmupWeightStr = providerInfo.getStaticAttr(ProviderInfoAttrs.ATTR_WARMUP_WEIGHT);
//        String startTimeStr = providerInfo.getStaticAttr(ProviderInfoAttrs.ATTR_START_TIME);
//
//        if (StringUtils.isNotBlank(warmupTimeStr) && StringUtils.isNotBlank(warmupWeightStr) &&
//            StringUtils.isNotBlank(startTimeStr)) {
//
//            long warmupTime = CommonUtils.parseLong(warmupTimeStr, 0);
//            int warmupWeight = CommonUtils.parseInt(warmupWeightStr,
//                Integer.parseInt(providerInfo.getStaticAttr(ProviderInfoAttrs.ATTR_WEIGHT)));
//            long startTime = CommonUtils.parseLong(startTimeStr, 0);
//            long warmupEndTime = startTime + warmupTime;
//
//            // set for dynamic
//            providerInfo.setDynamicAttr(ProviderInfoAttrs.ATTR_WARMUP_WEIGHT, warmupWeight);
//            providerInfo.setDynamicAttr(ProviderInfoAttrs.ATTR_WARM_UP_END_TIME, warmupEndTime);
//            providerInfo.setStatus(ProviderStatus.WARMING_UP);
//        }
//
//        // remove from static
//        providerInfo.getStaticAttrs().remove(ProviderInfoAttrs.ATTR_WARMUP_TIME);
//        providerInfo.getStaticAttrs().remove(ProviderInfoAttrs.ATTR_WARMUP_WEIGHT);
//
//    }
//
//    /**
//     * Init or add list.
//     *
//     * @param <K>
//     *         the key parameter
//     * @param <V>
//     *         the value parameter
//     * @param orginMap
//     *         the orgin map
//     * @param key
//     *         the key
//     * @param needAdd
//     *         the need add
//     */
//    public static <K, V> void initOrAddList(Map<K, List<V>> orginMap, K key, V needAdd) {
//        List<V> listeners = orginMap.get(key);
//        if (listeners == null) {
//            listeners = new CopyOnWriteArrayList<V>();
//            listeners.add(needAdd);
//            orginMap.put(key, listeners);
//        } else {
//            listeners.add(needAdd);
//        }
//    }
//
//    public static String convertInstanceToUrl(String host, int port, Map<String, String> metaData) {
//        if (metaData == null) {
//            metaData = new HashMap<String, String>();
//        }
//        String uri = "";
//        String protocol = metaData.get(RpcConstants.CONFIG_KEY_PROTOCOL);
//        if (StringUtils.isNotEmpty(protocol)) {
//            uri = protocol + "://";
//        }
//        uri += host + ":" + port;
//
//        StringBuilder sb = new StringBuilder();
//        for (Map.Entry<String, String> entry : metaData.entrySet()) {
//            sb.append("&").append(entry.getKey()).append("=").append(entry.getValue());
//        }
//        if (sb.length() > 0) {
//            uri += sb.replace(0, 1, "?").toString();
//        }
//        return uri;
//    }
//
//    public static String getServerHost(ServerConfig server) {
//        String host = server.getVirtualHost();
//        if (host == null) {
//            host = server.getHost();
//            if (NetUtils.isLocalHost(host) || NetUtils.isAnyHost(host)) {
//                host = SystemInfo.getLocalHost();
//            }
//        }
//        return host;
//    }
//
//    public static String buildUniqueName(AbstractInterfaceConfig config, String protocol) {
//        if (RpcConstants.PROTOCOL_TYPE_BOLT.equals(protocol) || RpcConstants.PROTOCOL_TYPE_TR.equals(protocol)) {
//            return ConfigUniqueNameGenerator.getUniqueName(config) + "@DEFAULT";
//        } else {
//            return ConfigUniqueNameGenerator.getUniqueName(config) + "@" + protocol;
//        }
//    }
}
