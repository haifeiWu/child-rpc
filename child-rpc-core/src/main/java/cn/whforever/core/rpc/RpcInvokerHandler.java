package cn.whforever.core.rpc;

import cn.whforever.core.exception.ChildRpcRuntimeException;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wuhf
 * @Date 2018/9/1 18:10
 **/
public class RpcInvokerHandler {
    public static Map<String, Object> serviceMap = new HashMap<String, Object>();

    public static RpcResponse invokeService(RpcRequest request) {
        Object serviceBean = serviceMap.get(request.getClassName());

        RpcResponse response = new RpcResponse();
        response.setRequestId(request.getRequestId());
        try {

            Class<?> serviceClass = serviceBean.getClass();
            String methodName = request.getMethodName();
            Class<?>[] parameterTypes = request.getParameterTypes();
            Object[] parameters = request.getParameters();

            Method method = serviceClass.getMethod(methodName, parameterTypes);
            method.setAccessible(true);
            Object result = method.invoke(serviceBean, parameters);

            response.setResult(result);
        } catch (Throwable t) {
            t.printStackTrace();
            response.setError(new ChildRpcRuntimeException(t));
        }

        return response;
    }
}
