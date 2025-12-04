package com.example.finals.quiz;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.finals.R;
import com.example.finals.data.AppDatabase;
import com.example.finals.data.Flashcard;
import com.example.finals.data.FlashcardDao;
import com.example.finals.data.QuizHistory;
import com.example.finals.data.QuizHistoryDao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuizActivity extends AppCompatActivity {

    private TextView tvTimer, tvQuestion, tvProgress;
    private EditText edtAnswer;
    private Button btnSubmit, btnSkip;

    private final List<Flashcard> questions = new ArrayList<>();
    private int index = 0;
    private int correct = 0;
    private CountDownTimer timer;
    private long startedAt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_quiz);

        tvTimer = findViewById(R.id.tvTimer);
        tvQuestion = findViewById(R.id.tvQuestion);
        tvProgress = findViewById(R.id.tvProgress);
        edtAnswer = findViewById(R.id.edtAnswer);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnSkip = findViewById(R.id.btnSkip);

        btnSubmit.setOnClickListener(v -> checkAnswer());
        btnSkip.setOnClickListener(v -> nextQuestion());

        startedAt = System.currentTimeMillis();
        loadQuestionsAndStart();
    }

    private void loadQuestionsAndStart() {
        AsyncTask.execute(() -> {
            FlashcardDao dao = AppDatabase.getInstance(this).flashcardDao();
            List<Flashcard> all = dao.getAll();
            if (all.isEmpty()) {
                runOnUiThread(() -> Toast.makeText(this, R.string.no_flashcards_for_quiz, Toast.LENGTH_SHORT).show());
                return;
            }
            Collections.shuffle(all);
            questions.clear();
            questions.addAll(all);
            runOnUiThread(() -> {
                startTimer();
                showCurrentQuestion();
            });
        });
    }

    private void startTimer() {
        // 2-minute quiz timer
        long durationMs = 2 * 60 * 1000;
        timer = new CountDownTimer(durationMs, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long seconds = millisUntilFinished / 1000;
                tvTimer.setText(getString(R.string.time_left_s, seconds));
            }

            @Override
            public void onFinish() {
                finishQuiz();
            }
        };
        timer.start();
    }

    private void showCurrentQuestion() {
        if (index >= questions.size()) {
            finishQuiz();
            return;
        }
        Flashcard card = questions.get(index);
        tvQuestion.setText(card.question);
        tvProgress.setText(getString(R.string.quiz_progress, index + 1, questions.size()));
        edtAnswer.setText("");
    }

    private void checkAnswer() {
        String a = edtAnswer.getText().toString().trim();
        if (TextUtils.isEmpty(a)) {
            edtAnswer.setError(getString(R.string.error_answer_required));
            return;
        }
        Flashcard card = questions.get(index);
        if (a.equalsIgnoreCase(card.answer)) {
            correct++;
        }
        nextQuestion();
    }

    private void nextQuestion() {
        index++;
        showCurrentQuestion();
    }

    private void finishQuiz() {
        if (timer != null) timer.cancel();
        int total = questions.size();
        long finishedAt = System.currentTimeMillis();
        AsyncTask.execute(() -> {
            QuizHistoryDao dao = AppDatabase.getInstance(this).quizHistoryDao();
            dao.insert(new QuizHistory(startedAt, finishedAt, total, correct));
            runOnUiThread(() -> {
                Toast.makeText(this, getString(R.string.quiz_result_toast, correct, total), Toast.LENGTH_LONG).show();
                finish();
            });
        });
    }
}

