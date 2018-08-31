package cn.whforever.core.rpc;

import java.io.Serializable;

/**
 * RPC常量类
 *
 * @author wuhaifei
 * @date 2018/08/30
 */
public class RpcResponse implements Serializable {
    private static final long serialVersionUID = 7329530374415722876L;

    private String requestId;
    private Throwable error;
    private Object result;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public Throwable getError() {
        return error;
    }

    public void setError(Throwable error) {
        this.error = error;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(128);
        sb.append("RpcResponse[");
        sb.append("child-rpc exception=").append(error).append(", ");
        sb.append("child-rpc requestId=").append(requestId).append(", ");
        sb.append("RpcResponse=").append(result).append("]");
        return sb.toString();
    }
}
