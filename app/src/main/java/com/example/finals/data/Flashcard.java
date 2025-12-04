package com.example.finals.data;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "flashcards",
        foreignKeys = @ForeignKey(
                entity = Category.class,
                parentColumns = "id",
                childColumns = "categoryId",
                onDelete = ForeignKey.SET_NULL
        ),
        indices = {@Index(value = {"categoryId"})}
)
public class Flashcard {
    @PrimaryKey(autoGenerate = true)
    public long id;

    public String question;
    public String answer;

    public Long categoryId; // nullable

    public long createdAt;
    public long updatedAt;

    public Flashcard(String question, String answer, Long categoryId, long createdAt, long updatedAt) {
        this.question = question;
        this.answer = answer;
        this.categoryId = categoryId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}

