package com.example.finals.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface FlashcardDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insert(Flashcard card);

    @Update
    int update(Flashcard card);

    @Delete
    int delete(Flashcard card);

    @Query("SELECT * FROM flashcards ORDER BY updatedAt DESC")
    List<Flashcard> getAll();

    @Query("SELECT * FROM flashcards WHERE id = :id LIMIT 1")
    Flashcard findById(long id);

    @Query("SELECT * FROM flashcards WHERE categoryId = :categoryId ORDER BY updatedAt DESC")
    List<Flashcard> findByCategory(long categoryId);
}

