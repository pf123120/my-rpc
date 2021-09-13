package cn.nju.peter.provider;

public interface ServiceProvider {
    <T> void addService(T service);
    Object getService(String serviceName);
}
