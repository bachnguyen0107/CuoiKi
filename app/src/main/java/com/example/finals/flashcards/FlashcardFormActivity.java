package com.example.finals.flashcards;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.finals.R;
import com.example.finals.data.AppDatabase;
import com.example.finals.data.Category;
import com.example.finals.data.CategoryDao;
import com.example.finals.data.Flashcard;
import com.example.finals.data.FlashcardDao;

import java.util.ArrayList;
import java.util.List;

public class FlashcardFormActivity extends AppCompatActivity {

    public static final String EXTRA_CARD_ID = "EXTRA_CARD_ID";

    private EditText edtQuestion, edtAnswer;
    private Spinner spnCategory;
    private Button btnSave, btnCancel;

    private final List<String> categoryNames = new ArrayList<>();
    private final List<Long> categoryIds = new ArrayList<>();

    private long editingId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_flashcard_form);

        edtQuestion = findViewById(R.id.edtQuestion);
        edtAnswer = findViewById(R.id.edtAnswer);
        spnCategory = findViewById(R.id.spnCategory);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);

        btnCancel.setOnClickListener(v -> finish());
        btnSave.setOnClickListener(v -> handleSave());

        setupCategories();
        checkEditing();
    }

    private void setupCategories() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnCategory.setAdapter(adapter);

        AsyncTask.execute(() -> {
            CategoryDao dao = AppDatabase.getInstance(this).categoryDao();
            List<Category> cats = dao.getAll();
            if (cats.isEmpty()) {
                dao.insert(new Category("General"));
                cats = dao.getAll();
            }
            categoryNames.clear();
            categoryIds.clear();
            for (Category c : cats) {
                categoryNames.add(c.name);
                categoryIds.add(c.id);
            }
            runOnUiThread(adapter::notifyDataSetChanged);
        });
    }

    private void checkEditing() {
        long id = getIntent().getLongExtra(EXTRA_CARD_ID, -1);
        if (id == -1) return;
        editingId = id;
        AsyncTask.execute(() -> {
            FlashcardDao dao = AppDatabase.getInstance(this).flashcardDao();
            Flashcard card = dao.findById(id);
            if (card != null) {
                runOnUiThread(() -> {
                    edtQuestion.setText(card.question);
                    edtAnswer.setText(card.answer);
                    if (card.categoryId != null) {
                        int idx = categoryIds.indexOf(card.categoryId);
                        if (idx >= 0) spnCategory.setSelection(idx);
                    }
                });
            }
        });
    }

    private void handleSave() {
        String q = edtQuestion.getText().toString().trim();
        String a = edtAnswer.getText().toString().trim();
        if (TextUtils.isEmpty(q)) { edtQuestion.setError(getString(R.string.error_question_required)); return; }
        if (TextUtils.isEmpty(a)) { edtAnswer.setError(getString(R.string.error_answer_required)); return; }
        int sel = spnCategory.getSelectedItemPosition();
        Long categoryId = sel >= 0 && sel < categoryIds.size() ? categoryIds.get(sel) : null;

        AsyncTask.execute(() -> {
            FlashcardDao dao = AppDatabase.getInstance(this).flashcardDao();
            long now = System.currentTimeMillis();
            if (editingId == -1) {
                Flashcard card = new Flashcard(q, a, categoryId, now, now);
                dao.insert(card);
            } else {
                Flashcard existing = dao.findById(editingId);
                if (existing != null) {
                    existing.question = q;
                    existing.answer = a;
                    existing.categoryId = categoryId;
                    existing.updatedAt = now;
                    dao.update(existing);
                }
            }
            runOnUiThread(() -> {
                Toast.makeText(this, R.string.saved, Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            });
        });
    }
}
