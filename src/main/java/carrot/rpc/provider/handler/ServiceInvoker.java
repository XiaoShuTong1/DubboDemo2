package carrot.rpc.provider.handler;

import carrot.rpc.common.Invoker;
import carrot.rpc.common.dto.RpcResponse;
import carrot.rpc.common.dto.RpcRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServiceInvoker implements Invoker {

    private final static Logger logger = LoggerFactory.getLogger(ServiceInvoker.class);

    Map<String, ServiceBean> serviceBeanMap=new ConcurrentHashMap<String, ServiceBean>();

    public void exportService(ServiceBean service){
        serviceBeanMap.put(service.getClazz().getName(),service);
    }

    public RpcResponse invoke(RpcRequest request){
        RpcResponse rpcResponse = new RpcResponse(request.requestId);
        ServiceBean serviceBean = serviceBeanMap.get(request.getServiceName());
        Method m = null;
        try {
            m = serviceBean.getService().getClass().getMethod(request.getMethodName(),request.getParameterTypes());
            Object result = m.invoke(serviceBean.getService(), request.getParameters());
            rpcResponse.setResult(result);
        } catch (NoSuchMethodException e) {
            logger.info("invoke error",e);
        } catch (IllegalAccessException e) {
            logger.info("invoke error",e);
        } catch (InvocationTargetException e) {
            logger.info("invoke error",e);
        }
        return rpcResponse;
    }
}
