package cn.nju.peter;

import cn.nju.peter.annotation.Service;
import cn.nju.peter.annotation.ServiceScan;
import cn.nju.peter.exception.RpcError;
import cn.nju.peter.exception.RpcException;
import cn.nju.peter.provider.DefaultServiceProvider;
import cn.nju.peter.provider.ServiceProvider;
import cn.nju.peter.registry.NacosServiceRegistry;
import cn.nju.peter.registry.ServiceRegistry;
import cn.nju.peter.serializer.CommonSerializer;
import cn.nju.peter.utils.ReflectUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Set;

@Slf4j
public abstract class AbstractServer implements Server {
    protected String hostname;
    protected int port;

    protected ServiceRegistry serviceRegistry;
    protected ServiceProvider serviceProvider;

    public void scanServices() {
        String mainClassName = ReflectUtil.getStackTrace();
        Class<?> startClass;
        try {
            startClass = Class.forName(mainClassName);
            if(!startClass.isAnnotationPresent(ServiceScan.class)) {
                log.error("启动类缺少 @ServiceScan 注解");
                throw new RpcException(RpcError.SERVICE_SCAN_PACKAGE_NOT_FOUND);
            }
        } catch (ClassNotFoundException e) {
            log.error("出现未知错误");
            throw new RpcException(RpcError.UNKNOWN_ERROR);
        }
        String basePackage = startClass.getAnnotation(ServiceScan.class).value();
        if("".equals(basePackage)) {
            basePackage = mainClassName.substring(0, mainClassName.lastIndexOf("."));
        }
        Set<Class<?>> classSet = ReflectUtil.getClasses(basePackage);
        for(Class<?> clazz : classSet) {
            if(clazz.isAnnotationPresent(Service.class)) {
                String serviceName = clazz.getAnnotation(Service.class).name();
                Object obj;
                try {
                    obj = clazz.newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    log.error("创建 " + clazz + " 时有错误发生");
                    continue;
                }
                if("".equals(serviceName)) {
                    Class<?>[] interfaces = clazz.getInterfaces();
                    for (Class<?> oneInterface: interfaces){
                        publishService(obj, oneInterface.getCanonicalName());
                    }
                } else {
                    publishService(obj, serviceName);
                }
            }
        }
    }

    public <T> void publishService(T service, String serviceName) {
        serviceProvider.addService(service);
        serviceRegistry.register(serviceName, new InetSocketAddress(hostname, port));
    }
}
