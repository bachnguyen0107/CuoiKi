package com.example.finals.flashcards;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finals.R;
import com.example.finals.data.AppDatabase;
import com.example.finals.data.Category;
import com.example.finals.data.CategoryDao;
import com.example.finals.data.Flashcard;
import com.example.finals.data.FlashcardDao;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class FlashcardsActivity extends AppCompatActivity implements FlashcardsAdapter.OnItemInteractionListener {

    private RecyclerView recyclerView;
    private FlashcardsAdapter adapter;
    private FloatingActionButton fabAdd;

    private final ActivityResultLauncher<Intent> addOrEditLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    loadFlashcards();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_flashcards);

        recyclerView = findViewById(R.id.recyclerFlashcards);
        fabAdd = findViewById(R.id.fabAdd);

        adapter = new FlashcardsAdapter(new ArrayList<>(), this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        fabAdd.setOnClickListener(v -> openCreateForm());

        ensureDefaultCategory();
        loadFlashcards();
    }

    private void ensureDefaultCategory() {
        AsyncTask.execute(() -> {
            CategoryDao categoryDao = AppDatabase.getInstance(this).categoryDao();
            if (categoryDao.count() == 0) {
                categoryDao.insert(new Category("General"));
            }
        });
    }

    private void loadFlashcards() {
        AsyncTask.execute(() -> {
            FlashcardDao dao = AppDatabase.getInstance(this).flashcardDao();
            List<Flashcard> data = dao.getAll();
            runOnUiThread(() -> adapter.submitList(data));
        });
    }

    private void openCreateForm() {
        Intent i = new Intent(this, FlashcardFormActivity.class);
        addOrEditLauncher.launch(i);
    }

    @Override
    public void onEdit(Flashcard card) {
        Intent i = new Intent(this, FlashcardFormActivity.class);
        i.putExtra(FlashcardFormActivity.EXTRA_CARD_ID, card.id);
        addOrEditLauncher.launch(i);
    }

    @Override
    public void onDelete(Flashcard card) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_flashcard_title)
                .setMessage(R.string.delete_flashcard_message)
                .setPositiveButton(R.string.delete, (dialog, which) -> doDelete(card))
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void doDelete(Flashcard card) {
        AsyncTask.execute(() -> {
            FlashcardDao dao = AppDatabase.getInstance(this).flashcardDao();
            dao.delete(card);
            runOnUiThread(() -> {
                Toast.makeText(this, R.string.deleted, Toast.LENGTH_SHORT).show();
                loadFlashcards();
            });
        });
    }
}
