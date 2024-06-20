package com.example.onlinebookreader;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;

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
    private ProgressBar progressBar;
    private static final String TAG = "ReadingActivity";
    private String pdfLink;
    private String bookTitle;
    private SharedPreferences sharedPreferences;
    private static final String BOOKMARK_PREFS = "bookmark_prefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading);

        pdfView = findViewById(R.id.pdfView);
        progressBar = findViewById(R.id.progressBar);
        pdfLink = getIntent().getStringExtra("pdfLink");
        bookTitle = getIntent().getStringExtra("bookTitle");

        sharedPreferences = getSharedPreferences(BOOKMARK_PREFS, MODE_PRIVATE);

        if (pdfLink != null) {
            progressBar.setVisibility(ProgressBar.VISIBLE);
            downloadAndDisplayPDF(pdfLink);
        } else {
            Toast.makeText(this, "No PDF link provided", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "No PDF link provided");
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_reading, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add_bookmark) {
            int currentPage = pdfView.getCurrentPage();
            saveBookmark(bookTitle, currentPage);
            return true;
        } else if (item.getItemId() == R.id.action_view_bookmarks) {
            loadBookmark(bookTitle);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void downloadAndDisplayPDF(String pdfUrl) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(pdfUrl).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(ProgressBar.GONE);
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
                        runOnUiThread(() -> {
                            progressBar.setVisibility(ProgressBar.GONE);
                            displayPDF(file);
                        });
                    } catch (Exception e) {
                        runOnUiThread(() -> {
                            progressBar.setVisibility(ProgressBar.GONE);
                            Toast.makeText(ReadingActivity.this, "Error saving PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "Error saving PDF", e);
                        });
                    } finally {
                        if (inputStream != null) inputStream.close();
                        if (outputStream != null) outputStream.close();
                    }
                } else {
                    runOnUiThread(() -> {
                        progressBar.setVisibility(ProgressBar.GONE);
                        Toast.makeText(ReadingActivity.this, "Failed to download PDF", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Failed to download PDF, response code: " + response.code());
                    });
                }
            }
        });
    }

    private void displayPDF(File file) {
        int defaultPage = getBookmark(bookTitle);
        pdfView.fromFile(file)
                .defaultPage(defaultPage)
                .enableSwipe(true)
                .swipeHorizontal(false)
                .enableAnnotationRendering(true)
                .scrollHandle(new DefaultScrollHandle(this))
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

    private void saveBookmark(String title, int page) {
        sharedPreferences.edit().putInt(title + "_bookmark", page).apply();
        Toast.makeText(this, "Bookmark saved at page " + page + " for " + title, Toast.LENGTH_SHORT).show();
    }

    private int getBookmark(String title) {
        return sharedPreferences.getInt(title + "_bookmark", 0);
    }

    private void loadBookmark(String title) {
        int bookmarkedPage = getBookmark(title);
        pdfView.jumpTo(bookmarkedPage, true);
        Toast.makeText(this, "Jumped to bookmarked page " + bookmarkedPage + " for " + title, Toast.LENGTH_SHORT).show();
    }
}
