package com.example.kapricho;

public class config {
    String server;

    public config() {
        server = "http://192.168.1.6/kaprichos-server/";
    }

    public String getServer() {
        return server;
    }

    public void setServer(String servidor) {
        this.server = servidor;
    }
}
