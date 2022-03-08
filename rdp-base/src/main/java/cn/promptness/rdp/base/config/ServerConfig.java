package cn.promptness.rdp.base.config;

import java.util.List;

public class ServerConfig {

    private String serverIp;
    private int serverPort;
    private long serverLimit;
    private List<String> clientKey;

    public long getServerLimit() {
        return serverLimit;
    }

    public void setServerLimit(long serverLimit) {
        this.serverLimit = serverLimit;
    }

    public String getServerIp() {
        return serverIp;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public List<String> getClientKey() {
        return clientKey;
    }

    public void setClientKey(List<String> clientKey) {
        this.clientKey = clientKey;
    }
}
