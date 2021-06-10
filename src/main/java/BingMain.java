import com.google.gson.Gson;
import okhttp3.*;
import okhttp3.logging.HttpLoggingInterceptor;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Executors;

public class BingMain {

    private static OkHttpClient httpClient;
    private long start =System.nanoTime();
    private int counter = 0;

    public static void main(String[] args) {
        new BingMain().run();
    }

    private void run() {


        HttpLoggingInterceptor logging = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                System.out.println(String.format(
                        "[%d]: %s", Thread.currentThread().getId(), message));
            }
        });

        logging.setLevel(HttpLoggingInterceptor.Level.BASIC);

        httpClient = new OkHttpClient.Builder()
                .addInterceptor(logging)
                //.dispatcher(new Dispatcher(Executors.newSingleThreadExecutor()))
                .build();

                Request request = new Request.Builder()
                .get()
                .url("https://www.bing.com/HPImageArchive.aspx?format=js&idx=0&n=8&mkt=en-US")
                .build();

        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse( Call call,  Response response) throws IOException {
                String json = response.body().string();
                Gson gson = new Gson();
                BingResponse bingResponse = gson.fromJson(json, BingResponse.class);
                for (BingImage image : bingResponse.images) {
                    String fullUrl = "https://bing.com/" + image.partOfUrl;
                    downloadImage(fullUrl);

                }
                counter--;
                reportTime();
            }
        };

        counter++;
        httpClient.newCall(request).enqueue(callback);

    }

    private void downloadImage(String imageUrl) throws IOException {
        final long startDownload = System.nanoTime();
        Request request = new Request.Builder()
                .get()
                .url(imageUrl)
                .build();

        String imageFile = request.url().queryParameter("id");

        Callback callback = new Callback() {
            @Override
            public void onFailure( Call call,  IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse( Call call,  Response response) throws IOException {
                InputStream inputStream = response.body().byteStream();
                try (OutputStream outputStream = new FileOutputStream(imageFile)) {
                    while (true) {
                        int readByte = inputStream.read();
                        if (readByte == -1) {
                            break;
                        }
                        outputStream.write(readByte);
                    }
                }
                final long endDownload = System.nanoTime();
                final double duration = endDownload-startDownload;
                System.out.println(String.format("Time: %.1fms", duration/1000000.0));
                counter--;
                reportTime();
            }

        };
        counter++;
        httpClient.newCall(request).enqueue(callback);
    }

    private void reportTime() {
        if (counter==0) {
            final long end = System.nanoTime();
            final double duration = end - start;
            System.out.println(String.format("FullTime: %.1fs", duration / 1_000_000_000.0));
        }
    }

}
