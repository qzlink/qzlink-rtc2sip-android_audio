package com.highsip.webrtc2sip.socket;

public class Request {

    private String host;
    private int port;

    private Request(Builder builder) {
        this.host = builder.host;
        this.port = builder.port;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public static class Builder {
        private String host;
        private int port;

        public Builder setHost(String host, int port) {
            this.host = host;
            this.port = port;
            return this;
        }

        public Request build() {
            return new Request(this);
        }
    }
}
