package com.vivo.peter;

import com.vivo.peter.annotation.ServiceScan;
import com.vivo.peter.serializer.KryoSerializer;

@ServiceScan("com.vivo.peter.impl")
public class TestServer {
    public static void main(String[] args) {
        Server server = new NettyServer("127.0.0.1", 9999);
        server.start();
    }
}
