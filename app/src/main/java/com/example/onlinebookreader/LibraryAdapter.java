package com.example.onlinebookreader;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LibraryAdapter extends RecyclerView.Adapter<LibraryAdapter.CategoryViewHolder> {

    private final Map<String, List<Book>> categorizedBooks;
    private final List<String> categories;

    public LibraryAdapter(Map<String, List<Book>> categorizedBooks) {
        this.categorizedBooks = categorizedBooks;
        this.categories = new ArrayList<>(categorizedBooks.keySet());
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        String category = categories.get(position);
        List<Book> books = categorizedBooks.get(category);
        holder.bind(category, books);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {

        private final TextView categoryTextView;
        private final RecyclerView booksRecyclerView;
        private BooksAdapter booksAdapter;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryTextView = itemView.findViewById(R.id.categoryTextView);
            booksRecyclerView = itemView.findViewById(R.id.bookRecyclerView);
            booksRecyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));
        }

        public void bind(String category, List<Book> books) {
            categoryTextView.setText(category);
            booksAdapter = new BooksAdapter(books);
            booksRecyclerView.setAdapter(booksAdapter);
        }
    }

    class BooksAdapter extends RecyclerView.Adapter<BooksAdapter.BookViewHolder> {

        private final List<Book> books;

        public BooksAdapter(List<Book> books) {
            this.books = books;
        }

        @NonNull
        @Override
        public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book, parent, false);
            return new BookViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
            Book book = books.get(position);
            holder.bind(book);
        }

        @Override
        public int getItemCount() {
            return books.size();
        }

        class BookViewHolder extends RecyclerView.ViewHolder {

            private final TextView titleTextView;

            public BookViewHolder(@NonNull View itemView) {
                super(itemView);
                titleTextView = itemView.findViewById(R.id.titleTextView);  // Ensure the ID matches
            }

            public void bind(Book book) {
                titleTextView.setText(book.getTitle());
                itemView.setOnClickListener(v -> {
                    Context context = itemView.getContext();
                    Intent intent = new Intent(context, BookDetailsActivity.class);
                    intent.putExtra("book", book);
                    context.startActivity(intent);
                });
            }
        }
    }
}
