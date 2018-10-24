package cn.whforever.core.remote.server;

import cn.whforever.core.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author wuhf
 */
public abstract class AbstractChildServer {
    private static final Logger logger = LoggerFactory.getLogger(AbstractChildServer.class);

    public abstract void start(Config config) throws Exception;

    public abstract void destroy() throws Exception;
}
