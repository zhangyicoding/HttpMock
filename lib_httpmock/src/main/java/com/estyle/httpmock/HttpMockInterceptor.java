package com.estyle.httpmock;

import android.content.Context;

import com.estyle.httpmock.common.MockEntity;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.http.RealResponseBody;
import okio.Buffer;

public class HttpMockInterceptor implements Interceptor {

    private Context mContext;
    private List<MockEntity> mMockList;
    private boolean mEnable;// 全局启用假数据
    private long mDelayMillis;// 网络延迟，单位ms

    public HttpMockInterceptor(
            Context context,
            List<MockEntity> mockList,
            boolean enable,
            long delayMillis
    ) {
        mContext = context;
        mMockList = mockList;
        mEnable = enable;
        mDelayMillis = delayMillis < 0 ? 0 : delayMillis;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request oldRequest = chain.request();
        if (!mEnable) return chain.proceed(oldRequest);

        String url = oldRequest.url().toString();

        // 根据url获取对应mock
        MockEntity mock = null;
        for (MockEntity entity : mMockList) {
            if (url.contains(entity.getUrl())) {
                mock = entity;
                break;
            }
        }
        if (!mock.isEnable()) return chain.proceed(oldRequest);

        // 启用mock，则读取assets中的json文件
        InputStream open = mContext.getAssets()
                .open("httpmock_debug/" + mock.getFileName());
        byte[] buff = new byte[1024];
        int len;
        StringBuilder sb = new StringBuilder();
        while ((len = open.read(buff)) != -1) {
            sb.append(new String(buff, 0, len));
        }
        open.close();

        // 模拟网络延迟，单位ms
        try {
            Thread.sleep(mDelayMillis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 创建Response
        Buffer buffer = new Buffer().writeUtf8(sb.toString());
        return new Response.Builder()
                .request(oldRequest)
                .protocol(Protocol.HTTP_2)
                .code(200)
                .message("MOCK")
                .body(new RealResponseBody(
                        "application/json; charset=UTF-8",
                        buffer.size(),
                        buffer
                ))
                .build();
    }
}
