package com.example.onlinebookreader;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LibraryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BookAdapter bookAdapter;
    private List<Book> allBooks = new ArrayList<>();
    private List<Book> displayedBooks = new ArrayList<>();
    private Spinner categorySpinner;
    private List<String> categories = new ArrayList<>();

    private FirebaseFirestore db;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            NavigationHelper.navigate(LibraryActivity.this, item.getItemId());
            return true;
        });

        // Set the selected item in the BottomNavigationView
        bottomNavigationView.setSelectedItemId(R.id.navigation_library);

        db = FirebaseFirestore.getInstance();

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        categorySpinner = findViewById(R.id.category_spinner);

        fetchBooks();


    }

    private void fetchBooks() {
        db.collection("books").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                allBooks.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Book book = document.toObject(Book.class);
                    allBooks.add(book);
                }
                categories = getCategoriesFromBooks(allBooks);
                setupCategorySpinner();
                filterBooksByCategory("All");
            } else {
                // Handle error
            }
        });
    }

    private void setupCategorySpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCategory = categories.get(position);
                filterBooksByCategory(selectedCategory);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private List<String> getCategoriesFromBooks(List<Book> books) {
        List<String> categories = new ArrayList<>();
        categories.add("All");
        categories.addAll(books.stream()
                .map(Book::getCategory)
                .distinct()
                .collect(Collectors.toList()));
        return categories;
    }

    private void filterBooksByCategory(String category) {
        if (category.equals("All")) {
            displayedBooks.clear();
            displayedBooks.addAll(allBooks);
        } else {
            displayedBooks.clear();
            displayedBooks.addAll(allBooks.stream()
                    .filter(book -> book.getCategory().equals(category))
                    .collect(Collectors.toList()));
        }
        updateRecyclerView();
    }

    private void updateRecyclerView() {
        if (bookAdapter == null) {
            bookAdapter = new BookAdapter(displayedBooks, book -> {
                // Handle book click
            });
            recyclerView.setAdapter(bookAdapter);
        } else {
            bookAdapter.setBooks(displayedBooks);
        }
    }
}
