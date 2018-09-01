package cn.whforever.core.rpc;

import java.text.MessageFormat;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeoutException;

/**
 * netty的连接超时操作.
 *
 * @author wuhf
 * @Date 2018/8/31 19:06
 **/
public class RpcCallbackFuture {

    /**
     *  过期，失效
     */
    public static ConcurrentMap<String, RpcCallbackFuture> futurePool = new ConcurrentHashMap<String, RpcCallbackFuture>();

    /**
     * net codec
     */
    private RpcRequest request;
    private RpcResponse response;
    /**
     * future lock
     */
    private boolean isDone = false;
    private Object lock = new Object();

    public RpcCallbackFuture(RpcRequest request) {
        this.request = request;
        futurePool.put(request.getRequestId(), this);
    }
    public RpcResponse getResponse() {
        return response;
    }
    public void setResponse(RpcResponse response) {
        this.response = response;
        // notify future lock
        synchronized (lock) {
            isDone = true;
            lock.notifyAll();
        }
    }

    public RpcResponse get(long timeoutMillis) throws InterruptedException, TimeoutException {
        if (!isDone) {
            synchronized (lock) {
                try {
                    lock.wait(timeoutMillis);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    throw e;
                }
            }
        }

        if (!isDone) {
            throw new TimeoutException(MessageFormat.format(">>>>>>>>>>>> child-rpc, netty request timeout at:{0}, request:{1}", System.currentTimeMillis(), request.toString()));
        }
        return response;
    }

}
