package cn.nju.peter.loadBalancer;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;

public class RoundRobinLoadBalancer implements LoadBalancer{
    private int index = 0;

    @Override
    public Instance getInstance(List<Instance> instances) {
        if (index >= instances.size()) {
            index = index % instances.size();
        }

        return instances.get(index++);
    }
}
