package com.example.finals.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface QuizHistoryDao {
    @Insert
    long insert(QuizHistory history);

    @Query("SELECT * FROM quiz_history ORDER BY finishedAt DESC")
    List<QuizHistory> getAll();
}

