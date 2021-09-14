package com.vivo.peter;

import com.vivo.peter.provider.DefaultServiceProvider;
import com.vivo.peter.request.RpcRequest;
import com.vivo.peter.response.RpcResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

@Slf4j
@AllArgsConstructor
public class RequestHandlerThread implements Runnable {
    private Socket socket;

    @Override
    public void run() {
        try (ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream())) {
            RpcRequest rpcRequest = (RpcRequest) inputStream.readObject();
            String interfaceName = rpcRequest.getInterfaceName();
            Object service = new DefaultServiceProvider().getService(interfaceName);
            Object result = new RequestHandler().handle(rpcRequest, service);
            outputStream.writeObject(RpcResponse.success(result));
            outputStream.flush();
        } catch (IOException | ClassNotFoundException e) {
            log.error("调用或发送时有错误发生：", e);
        }
    }
}
