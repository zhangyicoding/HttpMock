package com.estyle.httpmock;

import android.content.Context;

import okhttp3.OkHttpClient;

public class HttpMock {

    public static OkHttpClient.Builder addHttpMockInterceptor(
            Context context,
            OkHttpClient.Builder builder,
            Class<HttpMockGenerator> generatorClass,
            boolean enable
    ) {
        return builder;
    }
}
