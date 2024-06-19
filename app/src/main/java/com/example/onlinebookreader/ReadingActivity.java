package com.example.onlinebookreader;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ReadingActivity extends AppCompatActivity {

    private PDFView pdfView;
    private static final String TAG = "ReadingActivity";
    private String pdfLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading);

        pdfView = findViewById(R.id.pdfView);
        pdfLink = getIntent().getStringExtra("pdfLink");

        if (pdfLink != null) {
            downloadAndDisplayPDF(pdfLink);
        } else {
            Toast.makeText(this, "No PDF link provided", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "No PDF link provided");
            finish();
        }
    }

    private void downloadAndDisplayPDF(String pdfUrl) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(pdfUrl).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    Toast.makeText(ReadingActivity.this, "Failed to download PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Failed to download PDF: ", e);
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    InputStream inputStream = null;
                    FileOutputStream outputStream = null;
                    try {
                        inputStream = response.body().byteStream();
                        File file = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "downloaded.pdf");
                        outputStream = new FileOutputStream(file);
                        byte[] buffer = new byte[2048];
                        int length;
                        while ((length = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, length);
                        }
                        runOnUiThread(() -> displayPDF(file));
                    } catch (Exception e) {
                        runOnUiThread(() -> {
                            Toast.makeText(ReadingActivity.this, "Error saving PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "Error saving PDF", e);
                        });
                    } finally {
                        if (inputStream != null) inputStream.close();
                        if (outputStream != null) outputStream.close();
                    }
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(ReadingActivity.this, "Failed to download PDF", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Failed to download PDF, response code: " + response.code());
                    });
                }
            }
        });
    }

    private void displayPDF(File file) {
        pdfView.fromFile(file)
                .defaultPage(0)
                .enableSwipe(true)
                .swipeHorizontal(false)
                .enableAnnotationRendering(true)
                .onError(t -> {
                    Toast.makeText(this, "Error loading PDF: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error loading PDF: ", t);
                })
                .onLoad(nbPages -> {
                    Toast.makeText(ReadingActivity.this, "PDF loaded successfully", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "PDF loaded successfully, number of pages: " + nbPages);
                })
                .onPageError((page, t) -> {
                    Toast.makeText(this, "Error on page " + page + ": " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error on page " + page, t);
                })
                .load();
    }
}
