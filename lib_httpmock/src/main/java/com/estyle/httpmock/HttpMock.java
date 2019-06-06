package com.estyle.httpmock;

import android.content.Context;

import com.estyle.httpmock.common.AbstractHttpMockGenerator;
import com.estyle.httpmock.common.MockEntity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import okhttp3.OkHttpClient;

public class HttpMock {

    public static OkHttpClient.Builder addHttpMockInterceptor(
            OkHttpClient.Builder builder,
            Context context,
            boolean enable,
            long delayMillis,
            Class<? extends AbstractHttpMockGenerator> generatorClass
    ) {
        AbstractHttpMockGenerator generator = null;
        try {
            generator = generatorClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String json = generator.getJSONString();
        Type type = new TypeToken<List<MockEntity>>() {
        }.getType();
        List<MockEntity> mockList = new Gson().fromJson(json, type);
        return builder.addInterceptor(new HttpMockInterceptor(context, mockList, enable, delayMillis));
    }
}
