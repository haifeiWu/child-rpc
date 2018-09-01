package cn.whforever.core.rpc;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wuhf
 * @Date 2018/9/1 18:10
 **/
public class RpcInvokerHandler {
    private static Map<String, Object> serviceMap = new HashMap<String, Object>();
    public static RpcResponse invokeService(RpcRequest request) {
//        if (serviceBean==null) {
//            serviceBean = serviceMap.get(request.getClassName());
//        }
//        if (serviceBean == null) {
//            // TODO
//        }

        RpcResponse response = new RpcResponse();
        response.setRequestId(request.getRequestId());
        try {
            Class<?> serviceClass = Class.forName(request.getClassName());
            String methodName = request.getMethodName();
            Class<?>[] parameterTypes = request.getParameterTypes();
            Object[] parameters = request.getParameters();

            Method method = serviceClass.getMethod(methodName, parameterTypes);
            method.setAccessible(true);
            Object result = method.invoke(serviceClass.newInstance(), parameters);
            response.setResult(result);
        } catch (Throwable t) {
            t.printStackTrace();
            response.setError(t);
        }

        return response;
    }
}
