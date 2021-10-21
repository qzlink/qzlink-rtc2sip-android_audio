package com.highsip.webrtc2sip.socket;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.highsip.webrtc2sip.cache.DataCache;
import com.highsip.webrtc2sip.executor.ThreadExecutor;
import com.highsip.webrtc2sip.util.DebugLog;

import java.net.Socket;

public class SocketConnectManage {
    private static final String TAG = SocketConnectManage.class.getSimpleName();
    private static SocketConnectManage socketConnectManage = new SocketConnectManage();

    private static final int WHAT_RECONNECT_CODE = 14;

    private static Request mRequest;
    //最大重连次数
    private static int maxReconnectCount = Integer.MAX_VALUE;

    private static int reconnectCount = 0;
    //连接监听
    private static SocketListener mSocketListener;
    //
    private static SocketClient mSocketClient;

    private static int currentIdleCount;

    private SocketConnectManage() {

    }

    private static Handler connectHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == WHAT_RECONNECT_CODE) {
                if (mSocketClient == null || !mSocketClient.isConnect()) {
                    connect(mRequest, mSocketListener);
                }
            }
        }
    };

    public static SocketConnectManage getInstance() {
        return socketConnectManage;
    }

    private static SocketListener handlerSocketListener = new SocketListener() {
        @Override
        public void onOpen(Socket socket, Request request) {
            DebugLog.e(TAG, "onOpen");
            currentIdleCount = 0;
            mSocketListener.onOpen(socket, request);
        }

        @Override
        public void onMessage(Socket socket, String text) {
            DebugLog.e(TAG, "onMessage = " + text);
            currentIdleCount = 0;
            mSocketListener.onMessage(socket, text);
        }

        @Override
        public void onIdle(Socket socket) {
            DebugLog.e(TAG, "onIdle currentIdleCount = " + currentIdleCount);
            currentIdleCount++;

            if (currentIdleCount >= 3) {
                reconnect();
            }

            mSocketListener.onIdle(socket);

            String hbJson = DataCache.getInstance().getHBReq();
            sendMessage(hbJson);
        }

        @Override
        public void onClosing(Socket socket) {
            DebugLog.e(TAG, "onClosing");
            mSocketListener.onClosing(socket);
        }

        @Override
        public void onClosed(Socket socket) {
            DebugLog.e(TAG, "onClosed");
            mSocketListener.onClosed(socket);
        }

        @Override
        public void onFailure(Socket socket) {
            DebugLog.e(TAG, "onFailure");
            mSocketListener.onFailure(socket);

            delayReconnect();
        }
    };

    /**
     * 连接
     *
     * @param request
     * @param socketListener
     */
    public static void connect(final Request request, SocketListener socketListener) {
        DebugLog.e(TAG, "connect");
        mRequest = request;
        mSocketListener = socketListener;

        reconnectCount = 0;

        doConnect();
    }

    /**
     * 发送消息
     *
     * @param msg
     */
    public static void sendMessage(String msg) {
        Log.e("登录信令", "---" + msg + "---");
        sendMessage(msg, null);
    }

    /**
     * 发送消息
     *
     * @param msg
     * @param back
     */
    public static void sendMessage(String msg, SendBack back) {
        if (isConnection()) {
            mSocketClient.send(msg, back);
        }
    }

    /**
     * 重连
     */
    public static void reconnect() {
        DebugLog.e(TAG, "reconnect");
        if (reconnectCount >= maxReconnectCount || isConnection())
            return;

        disconnect();

        doConnect();

        reconnectCount++;
    }

    private static void doConnect() {
        DebugLog.e(TAG, "doConnect");
        ThreadExecutor.executeNormal(new Runnable() {
            @Override
            public void run() {
                if (mSocketClient == null || !mSocketClient.isConnect()) {
                    mSocketClient = new SocketClient.Builder().build();
                    mSocketClient.newSocket(mRequest, handlerSocketListener);
                }
            }
        });
    }

    /**
     * 延迟重连
     */
    private static void delayReconnect() {
        DebugLog.e(TAG, "delayReconnect");
        connectHandler.removeMessages(WHAT_RECONNECT_CODE);
        connectHandler.sendEmptyMessageDelayed(WHAT_RECONNECT_CODE, 5000);
    }

    /**
     * 关闭连接
     */
    public static void disconnect() {
        DebugLog.e(TAG, "disconnect");
        mSocketClient.disconnect();
    }

    /**
     * 连接状态
     *
     * @return
     */
    public static boolean isConnection() {
        return mSocketClient != null && mSocketClient.isConnect();
    }

    /**
     * 设置最大重连次数
     *
     * @param maxReconnectCount
     */
    public static void setMaxReconnectCount(int maxReconnectCount) {
        SocketConnectManage.maxReconnectCount = maxReconnectCount;
    }
}
