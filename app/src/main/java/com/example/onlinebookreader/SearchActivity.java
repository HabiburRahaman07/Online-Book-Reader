package com.example.onlinebookreader;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SearchActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private BookAdapter bookAdapter;
    private final List<Book> bookList = new ArrayList<>();
    private final List<Book> filteredBookList = new ArrayList<>();
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        db = FirebaseFirestore.getInstance();
        RecyclerView searchRecyclerView = findViewById(R.id.searchRecyclerView);
        searchView = findViewById(R.id.searchView);
        searchRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        bookAdapter = new BookAdapter(filteredBookList, book -> {
            Intent intent = new Intent(SearchActivity.this, BookDetailsActivity.class);
            intent.putExtra("book", book);
            startActivity(intent);
        });
        searchRecyclerView.setAdapter(bookAdapter);


        fetchBooks();

        searchView.setOnClickListener(v -> searchView.setIconified(false));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterBooks(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterBooks(newText);
                return true;
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            NavigationHelper.navigate(SearchActivity.this, item.getItemId());
            return true;
        });

        // Set the selected item in the BottomNavigationView
        bottomNavigationView.setSelectedItemId(R.id.navigation_search);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void fetchBooks() {
        db.collection("books").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                bookList.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Book book = document.toObject(Book.class);
                    bookList.add(book);
                }
                filteredBookList.clear();
                filteredBookList.addAll(bookList);
                bookAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(SearchActivity.this, "Failed to load books: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void filterBooks(String query) {
        String lowerCaseQuery = query.toLowerCase();
        filteredBookList.clear();
        filteredBookList.addAll(bookList.stream()
                .filter(book -> book.getTitle().toLowerCase().contains(lowerCaseQuery) ||
                        book.getAuthor().toLowerCase().contains(lowerCaseQuery) ||
                        book.getCategory().toLowerCase().contains(lowerCaseQuery))
                .collect(Collectors.toList()));
        bookAdapter.notifyDataSetChanged();
    }
}
