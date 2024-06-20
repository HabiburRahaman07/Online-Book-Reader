package com.example.onlinebookreader;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class BookDetailsActivity extends AppCompatActivity {

    private ImageView coverImageView;
    private TextView titleTextView, authorTextView, categoryTextView, descriptionTextView;
    private Button readButton, saveButton;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);

        coverImageView = findViewById(R.id.coverImageView);
        titleTextView = findViewById(R.id.titleTextView);
        authorTextView = findViewById(R.id.authorTextView);
        categoryTextView = findViewById(R.id.categoryTextView);
        descriptionTextView = findViewById(R.id.descriptionTextView);
        readButton = findViewById(R.id.readButton);
        saveButton = findViewById(R.id.saveButton);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        Book book = (Book) getIntent().getSerializableExtra("book");

        if (book != null) {
            titleTextView.setText(book.getTitle());
            authorTextView.setText(book.getAuthor());
            categoryTextView.setText(book.getCategory());
            descriptionTextView.setText(book.getDescription());
            Glide.with(this).load(book.getCoverImageUrl()).into(coverImageView);

            // Print PDF link to logcat
            //Log.d("BookDetailsActivity", "PDF Link: " + book.getLink());

            readButton.setOnClickListener(v -> {
                Intent intent = new Intent(BookDetailsActivity.this, ReadingActivity.class);
                intent.putExtra("pdfLink", book.getLink());
                intent.putExtra("bookTitle", book.getTitle());
                startActivity(intent);
            });

            saveButton.setOnClickListener(v -> saveBook(book));
        }
    }

    private void saveBook(Book book) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            db.collection("users").document(currentUser.getUid())
                    .collection("savedBooks").document(book.getTitle())
                    .set(book)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(BookDetailsActivity.this, "Book saved successfully", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(BookDetailsActivity.this, "Failed to save book", Toast.LENGTH_SHORT).show();
                        Log.w("BookDetailsActivity", "Error saving book", e);
                    });
        } else {
            Toast.makeText(this, "You need to be logged in to save books", Toast.LENGTH_SHORT).show();
        }
    }
}
