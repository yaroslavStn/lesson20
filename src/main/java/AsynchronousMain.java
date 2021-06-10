import okhttp3.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

public class AsynchronousMain {

    public static void main(String[] args) {

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .get()
                .url("https://www.google.com/")
                .build();

        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("Callback::onFailure");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String s = response.body().string();
              // System.out.println(s);
               PrintWriter printWriter = new PrintWriter(new FileOutputStream("page2.txt"));
               printWriter.write(s);

            }
        };

        client.newCall(request).enqueue(callback);



    }
}
