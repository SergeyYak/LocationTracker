package com.sergei.tracker;

import android.content.Context;

import java.io.IOException;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

class HttpClient {
    final Context context;
    String data;
    MediaType format;
    String url;
    private final OkHttpClient client;
    HttpClient(Context context){
        this.context = context;
        client = getConfiguredOkHttpClient();
    }

    static class ResponseResult {
        boolean isSuccessful;
        String response;
    }

    static class MediaTypes {
        static final MediaType JSON_Format = MediaType.parse("application/json; charset=utf-8");
    }

    ResponseResult Send() throws IOException {

        ResponseResult result = new ResponseResult();
        RequestBody body = RequestBody.create(data, MediaTypes.JSON_Format);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .url(url)
                .build();
        Response response = client.newCall(request).execute();
        result.isSuccessful = response.isSuccessful();
        result.response = Objects.requireNonNull(response.body()).string();
        return result;
    }

    OkHttpClient getConfiguredOkHttpClient() {

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.retryOnConnectionFailure(false);
        builder.connectTimeout(new AppConfig(context).new Server().timeout);
        return builder.build();
    }
}
