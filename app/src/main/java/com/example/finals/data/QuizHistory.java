package com.example.finals.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "quiz_history")
public class QuizHistory {
    @PrimaryKey(autoGenerate = true)
    public long id;

    public long startedAt;
    public long finishedAt;

    public int totalQuestions;
    public int correctCount;

    public QuizHistory(long startedAt, long finishedAt, int totalQuestions, int correctCount) {
        this.startedAt = startedAt;
        this.finishedAt = finishedAt;
        this.totalQuestions = totalQuestions;
        this.correctCount = correctCount;
    }
}

