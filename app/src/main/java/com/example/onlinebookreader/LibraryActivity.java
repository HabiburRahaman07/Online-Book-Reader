package com.example.onlinebookreader;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

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
    private ArrayAdapter<String> categoryAdapter;

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
        categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);

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

        bookAdapter = new BookAdapter(displayedBooks, book -> {
            // Handle book click
        });
        recyclerView.setAdapter(bookAdapter);

        fetchBooks();
    }

    private void fetchBooks() {
        db.collection("books").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                allBooks.clear(); // Clear the list to avoid duplication
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Book book = document.toObject(Book.class);
                    // Assuming Firestore document contains a field "coverImageUrl"
                    book.setCoverImageUrl(document.getString("coverImageUrl"));
                    allBooks.add(book);
                }
                categories.clear();
                categories.addAll(getCategoriesFromBooks(allBooks));
                categoryAdapter.notifyDataSetChanged();

                // Initially display all books
                displayedBooks.clear();
                displayedBooks.addAll(allBooks);
                bookAdapter.notifyDataSetChanged();
            } else {
                // Handle the error
                Toast.makeText(LibraryActivity.this, "Failed to load books: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
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
        bookAdapter.notifyDataSetChanged();
    }
}
