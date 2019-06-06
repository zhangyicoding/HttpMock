package com.estyle.httpmock;

import android.content.Context;

import okhttp3.OkHttpClient;

public class HttpMock {

    public static OkHttpClient.Builder addHttpMockInterceptor(
            OkHttpClient.Builder builder,
            Context context,
            boolean enable,
            Class<HttpMockGenerator> generatorClass
    ) {
        return builder;
    }
}
