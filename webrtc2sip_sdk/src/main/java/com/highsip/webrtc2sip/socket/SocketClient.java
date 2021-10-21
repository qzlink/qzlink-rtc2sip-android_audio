package com.highsip.webrtc2sip.socket;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.highsip.webrtc2sip.model.WriteMsgBean;
import com.highsip.webrtc2sip.util.DebugLog;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class SocketClient {
    private static final String TAG = SocketClient.class.getSimpleName();

    //心跳间隔
    private static final int IDLE_INTERVAL = 30000;
    //心跳消息识别码
    private static final int WHAT_IDLE_CODE = 13;
    //写入消息识别码
    private static final int WHAT_WRITE_MSG_CODE = 11;

    private Socket clientSocket;
    private boolean tcpNoDelay;
    private int sendBufferSize;
    private int receiveBufferSize;
    private SocketListener socketListener;
    private Request request;

    private ReadThread readThread = null;
    private WriteThread writeThread = null;

    private InputStream inputStream;
    private BufferedInputStream bis;

    private OutputStream outputStream;
    private BufferedOutputStream bos;

    private boolean connectNormal = false;

    private Handler writeMsgHandler = null;

    private Handler idleSocketHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == WHAT_IDLE_CODE && socketListener != null) {
                DebugLog.e(TAG, "idleSocketHandler");
                socketListener.onIdle(clientSocket);
                rePrepareIdle();
            }
        }
    };

    private SocketClient(Builder builder) {
        initBuildData(builder);
    }

    private void requestConnect() {
        DebugLog.e(TAG, "requestConnect");
        try {
            clientSocket = new Socket();
            clientSocket.connect(new InetSocketAddress(request.getHost(), request.getPort()));
            clientSocket.setTcpNoDelay(tcpNoDelay);
            clientSocket.setSendBufferSize(sendBufferSize);
            clientSocket.setReceiveBufferSize(receiveBufferSize);

            connectNormal = true;

            inputStream = clientSocket.getInputStream();
            bis = new BufferedInputStream(inputStream);

            outputStream = clientSocket.getOutputStream();
            bos = new BufferedOutputStream(outputStream);

            readThread = new ReadThread();
            readThread.start();

            writeThread = new WriteThread();
            writeThread.start();
            Thread.sleep(2000);
            socketListener.onOpen(clientSocket, request);

            rePrepareIdle();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            socketListener.onFailure(clientSocket);
            DebugLog.e(TAG, "requestConnect onFailure");
        }
    }

    private void initBuildData(Builder builder) {
        this.tcpNoDelay = builder.tcpNoDelay;
        this.sendBufferSize = builder.sendBufferSize;
        this.receiveBufferSize = builder.receiveBufferSize;
    }

    private void sendData(String msg, SendBack back) {
        DebugLog.e(TAG, "sendData msg = " + msg);
        WriteMsgBean msgBean = new WriteMsgBean(msg, back);
        if (writeMsgHandler != null) {
            Message message = new Message();
            message.what = WHAT_WRITE_MSG_CODE;
            message.obj = msgBean;
            writeMsgHandler.sendMessage(message);
        }
    }

    private class WriteThread extends Thread {
        @Override
        public void run() {
            Looper.prepare();
            writeMsgHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    DebugLog.e(TAG, "handleMessage = " + msg);
                    if (msg.what == WHAT_WRITE_MSG_CODE) {
                        if (msg.obj instanceof WriteMsgBean) {
                            WriteMsgBean msgBean = (WriteMsgBean) msg.obj;
                            writeMessage(msgBean);
                        }
                    }
                }
            };
            Looper.loop();
        }
    }

    private class ReadThread extends Thread {
        @Override
        public void run() {
            readMessage();
        }
    }

    private void writeMessage(WriteMsgBean msgBean) {
        DebugLog.e(TAG, "writeMessage msg = " + msgBean.getMsg() + "  back = " + msgBean.getSendBack());
        if (msgBean == null) {
            return;
        }
        String msg = msgBean.getMsg();
        SendBack back = msgBean.getSendBack();

        try {
            if (clientSocket != null && clientSocket.isConnected() && bos != null) {
//                String sendMessage = msg + "\n";
                String sendMessage = msg;
                bos.write(sendMessage.getBytes());
                bos.flush();
                if (back != null)
                    back.onBack(SendState.SUCCESS);
            } else {
                if (back != null)
                    back.onBack(SendState.FAILURE);
            }
        } catch (IOException e) {
            if (back != null)
                back.onBack(SendState.FAILURE);
        }
    }

    /**
     * 在预定时间后发送闲置消息
     */
    private void rePrepareIdle() {
        DebugLog.e(TAG, "rePrepareIdle");
        idleSocketHandler.removeMessages(WHAT_IDLE_CODE);
        idleSocketHandler.sendEmptyMessageDelayed(WHAT_IDLE_CODE, IDLE_INTERVAL);
    }


    private void readMessage() {
        DebugLog.e(TAG, "readMessage()");
        if (isConnect()) {
            while (isConnect() && bis != null) {
                if (!connectNormal || !isConnect()) {
                    break;
                }
                String cacheMessage = "";
                byte[] data = new byte[1024];
                int size;
                try {
                    while ((size = bis.read(data)) != -1) {
                        String message = new String(data, 0, size);
                        boolean end = message.endsWith("\n");

                        String[] msgs = message.split("\n");

                        if (msgs != null || msgs.length != 0) {
                            if (msgs.length == 1) {
                                cacheMessage = cacheMessage + msgs[0];
                                if (end) {
                                    socketListener.onMessage(clientSocket, cacheMessage);
                                    cacheMessage = "";
                                    rePrepareIdle();
                                }
                            } else {
                                if (end) {
                                    cacheMessage = cacheMessage + msgs[0];
                                    for (int i = 0; i < msgs.length; i++) {
                                        if (i == 0) {
                                            socketListener.onMessage(clientSocket, cacheMessage);
                                            cacheMessage = "";
                                            rePrepareIdle();
                                        } else {
                                            socketListener.onMessage(clientSocket, msgs[i]);
                                        }
                                    }
                                } else {
                                    cacheMessage = cacheMessage + msgs[0];
                                    for (int i = 0; i < msgs.length; i++) {
                                        if (i == 0) {
                                            socketListener.onMessage(clientSocket, cacheMessage);
                                            cacheMessage = "";
                                            rePrepareIdle();
                                        } else if (i == msgs.length) {
                                            cacheMessage = msgs[i];
                                        } else {
                                            socketListener.onMessage(clientSocket, msgs[i]);
                                            cacheMessage = "";
                                            rePrepareIdle();
                                        }
                                    }
                                }
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();

                    connectNormal = false;

                    clear();

                    socketListener.onFailure(clientSocket);
                    DebugLog.e(TAG, "readMessage() Exception " + e.getMessage());
                }
            }
        }
    }

    private void disconnectSocket() {
        DebugLog.e(TAG, "disconnectSocket()");
        connectNormal = false;
        if (isConnect()) {
            socketListener.onClosing(clientSocket);

            if (writeMsgHandler != null)
                writeMsgHandler.getLooper().quit();

            if (idleSocketHandler != null)
                idleSocketHandler.removeMessages(WHAT_IDLE_CODE);

            if (readThread != null)
                readThread.interrupt();

            if (writeThread != null)
                writeThread.interrupt();

            connectNormal = false;

            try {
                clientSocket.close();
                DebugLog.e(TAG, "clientSocket close()");
            } catch (IOException e) {
                e.printStackTrace();
                DebugLog.e(TAG, "disconnectSocket Exception" + e.getMessage());
            } finally {
                socketListener.onClosed(clientSocket);
            }
        }
    }

    //////////////////////////////////////////**public**////////////////////////////////////////////
    public void newSocket(Request request, SocketListener socketListener) {
        this.socketListener = socketListener;
        this.request = request;

        requestConnect();
    }

    public boolean isConnect() {
        return clientSocket != null && clientSocket.isConnected();
    }

    public void send(String text) {
        send(text, null);
    }

    public void send(String text, SendBack back) {
        sendData(text, back);
    }

    public void disconnect() {
        disconnectSocket();
    }

    public void clear() {
        if (writeMsgHandler != null)
            writeMsgHandler.getLooper().quit();

        if (idleSocketHandler != null)
            idleSocketHandler.removeMessages(WHAT_IDLE_CODE);

        if (readThread != null)
            readThread.interrupt();

        if (writeThread != null)
            writeThread.interrupt();

        if (clientSocket != null) {
            try {
                clientSocket.close();
                clientSocket = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //////////////////////////////////////////**public**////////////////////////////////////////////

    public static class Builder {
        private int timeout = 30000;
        private boolean tcpNoDelay = true;
        private int sendBufferSize = 4096;
        private int receiveBufferSize = 4096;

        public Builder setTimeout(int timeout) {
            this.timeout = timeout;
            return this;
        }

        public Builder setTcpNoDelay(boolean tcpNoDelay) {
            this.tcpNoDelay = tcpNoDelay;
            return this;
        }

        public Builder setSendBufferSize(int sendBufferSize) {
            this.sendBufferSize = sendBufferSize;
            return this;
        }

        public Builder setReceiveBufferSize(int receiveBufferSize) {
            this.receiveBufferSize = receiveBufferSize;
            return this;
        }

        public SocketClient build() {
            return new SocketClient(this);
        }
    }
}
