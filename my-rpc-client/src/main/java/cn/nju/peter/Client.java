package cn.nju.peter;

import cn.nju.peter.request.RpcRequest;
import cn.nju.peter.serializer.CommonSerializer;

public interface Client {
    Object sendRequest(RpcRequest rpcRequest);
}
