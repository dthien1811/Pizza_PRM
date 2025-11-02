package fpt.edu.vn.pizzaapp_prm392.services;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.GsonBuilder;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL = "https://dummyjson.com/";
    private static final String TAG = "RETROFIT";
    private static Retrofit retrofit;

    public static Retrofit getClient() {
        if (retrofit == null) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new Interceptor() {
                        @NonNull
                        @Override
                        public Response intercept(@NonNull Chain chain) throws IOException {
                            Request request = chain.request().newBuilder()
                                    .addHeader("User-Agent", "Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36")
                                    .addHeader("Accept", "application/json")
                                    .build();

                            Response response = chain.proceed(request);

                            // LOG RESPONSE ĐỂ XEM CÓ PHẢI HTML KHÔNG
                            ResponseBody body = response.peekBody(1024);
                            String bodyString = body.string();
                            Log.d("RETROFIT", "URL: " + request.url());
                            Log.d("RETROFIT", "Code: " + response.code());
                            Log.d("RETROFIT", "Body: " + bodyString.substring(0, Math.min(300, bodyString.length())));

                            return response;
                        }
                    })
                    .addInterceptor(new LoggingInterceptor())
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(
                            new GsonBuilder()
                                    .setLenient()
                                    .create()
                    ))
                    .client(client)
                    .build();
        }
        return retrofit;
    }

    private static class LoggingInterceptor implements Interceptor {
        @NonNull
        @Override
        public Response intercept(@NonNull Chain chain) throws IOException {
            Request request = chain.request();
            long t1 = System.nanoTime();
            Log.d(TAG, "Gửi: " + request.method() + " " + request.url());

            Response response = chain.proceed(request);
            long t2 = System.nanoTime();

            ResponseBody body = response.peekBody(1024 * 1024); // 1MB
            String bodyString = body.string();
            Log.d(TAG, String.format("Nhận: %s (%.1fms)\n%s",
                    response.code(),
                    (t2 - t1) / 1e6d,
                    bodyString.length() > 500 ? bodyString.substring(0, 500) + "..." : bodyString));

            return response;
        }
    }
}
