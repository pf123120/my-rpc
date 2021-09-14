package com.vivo.peter;

import com.vivo.peter.request.RpcRequest;
import com.vivo.peter.serializer.CommonSerializer;

public interface Client {
    Object sendRequest(RpcRequest rpcRequest);
}
