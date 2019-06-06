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
            Context context,
            OkHttpClient.Builder builder,
            Class<? extends AbstractHttpMockGenerator> generatorClass,
            boolean enable
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
        return builder.addInterceptor(new HttpMockInterceptor(context, mockList, enable));
    }
}
