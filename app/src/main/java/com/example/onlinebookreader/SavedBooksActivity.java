package com.example.onlinebookreader;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SavedBooksActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private RecyclerView recyclerView;
    private BookAdapter bookAdapter;
    private List<Book> savedBooksList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_books);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        savedBooksList = new ArrayList<>();
        bookAdapter = new BookAdapter(savedBooksList, this::onBookClick);
        recyclerView.setAdapter(bookAdapter);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        loadSavedBooks();
    }

    private void loadSavedBooks() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            db.collection("users").document(currentUser.getUid())
                    .collection("savedBooks")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (querySnapshot != null) {
                                savedBooksList.clear();
                                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                    Book book = document.toObject(Book.class);
                                    savedBooksList.add(book);
                                }
                                bookAdapter.notifyDataSetChanged();
                            }
                        } else {
                            Toast.makeText(SavedBooksActivity.this, "Failed to load saved books", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void onBookClick(Book book) {
        Intent intent = new Intent(this, BookDetailsActivity.class);
        intent.putExtra("book", book);
        startActivity(intent);
    }
}
