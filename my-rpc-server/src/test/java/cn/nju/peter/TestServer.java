package cn.nju.peter;

import cn.nju.peter.annotation.ServiceScan;
import cn.nju.peter.impl.HelloServiceImpl;
import cn.nju.peter.provider.DefaultServiceProvider;
import cn.nju.peter.provider.ServiceProvider;
import cn.nju.peter.serializer.KryoSerializer;

@ServiceScan("cn.nju.peter.impl")
public class TestServer {
    public static void main(String[] args) {
        Server server = new NettyServer("127.0.0.1", 9999);
        server.start();
    }
}
