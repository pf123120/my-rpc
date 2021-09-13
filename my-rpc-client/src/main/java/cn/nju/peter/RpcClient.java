package cn.nju.peter;

import cn.nju.peter.request.RpcRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

@Slf4j
@AllArgsConstructor
public class RpcClient implements Client {
    private String hostname;
    private int port;

    public Object sendRequest(RpcRequest rpcRequest) {
        try (Socket socket = new Socket(hostname, port)) {
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
            outputStream.writeObject(rpcRequest);
            outputStream.flush();
            Thread.sleep(1000);
            return inputStream.readObject();
        } catch (IOException | ClassNotFoundException | InterruptedException e) {
            log.error("调用时有错误发生：", e);
            return null;
        }
    }
}
