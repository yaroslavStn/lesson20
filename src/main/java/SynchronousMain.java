import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

public class SynchronousMain {

    public static void main(String[] args) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .get()
                .url("https://www.google.com/")
                .build();

        try (Response response = client.newCall(request).execute()) {
            String s = response.body().string();
            System.out.println(s);
            PrintWriter printWriter = new PrintWriter(new FileOutputStream("page.txt"));
            printWriter.print(s);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
