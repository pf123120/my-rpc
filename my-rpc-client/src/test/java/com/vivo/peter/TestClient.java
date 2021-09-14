package com.vivo.peter;

public class TestClient {
    public static void main(String[] args) {
        Client client = new NettyClient();
        ClientProxy clientProxy = new ClientProxy(client);
        HelloService helloService = clientProxy.getProxy(HelloService.class);
        String hello = helloService.Hello(new HelloObject(1, "hello"));
        System.out.println(hello);
    }
}
