package com.vivo.peter.provider;

public interface ServiceProvider {
    <T> void addService(T service);
    Object getService(String serviceName);
}
