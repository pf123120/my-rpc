package com.vivo.peter.loadBalancer;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;
import java.util.Random;

public class RandomLoadBalancer implements LoadBalancer{
    @Override
    public Instance getInstance(List<Instance> instances) {
        return instances.get(new Random().nextInt(instances.size()));
    }
}
