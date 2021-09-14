package com.vivo.peter.registry;

import com.vivo.peter.exception.RpcError;
import com.vivo.peter.exception.RpcException;
import com.vivo.peter.loadBalancer.LoadBalancer;
import com.vivo.peter.loadBalancer.RandomLoadBalancer;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.List;

@Slf4j
public class NacosServiceRegistry implements ServiceRegistry {
    private static final String SERVER_ADDR = "127.0.0.1:8848";
    private static final NamingService namingService;
    private LoadBalancer loadBalancer;

    public NacosServiceRegistry() {
        loadBalancer = new RandomLoadBalancer();
    }

    public NacosServiceRegistry(LoadBalancer loadBalancer) {
        this.loadBalancer = loadBalancer;
    }

    static {
        try {
            namingService = NamingFactory.createNamingService(SERVER_ADDR);
        } catch (NacosException e) {
            log.error("连接到Nacos时有错误发生: ", e);
            throw new RpcException(RpcError.FAILED_TO_CONNECT_TO_SERVICE_REGISTRY);
        }
    }

    @Override
    public void register(String serviceName, InetSocketAddress inetSocketAddress) {
        try {
            namingService.registerInstance(serviceName, inetSocketAddress.getHostName(), inetSocketAddress.getPort());
        } catch (NacosException e) {
            log.error("注册服务时有错误发生:", e);
            throw new RpcException(RpcError.REGISTER_SERVICE_FAILED);
        }
    }

    @Override
    public InetSocketAddress lookupService(String serviceName) {
        try {
            List<Instance> instances = namingService.getAllInstances(serviceName);
            Instance instance = loadBalancer.getInstance(instances);
            return new InetSocketAddress(instance.getIp(), instance.getPort());
        } catch (NacosException e) {
            log.error("获取服务时有错误发生:", e);
        }
        return null;
    }
}
