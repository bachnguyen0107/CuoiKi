package com.example.finals.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insert(Category category);

    @Query("SELECT * FROM categories ORDER BY name ASC")
    List<Category> getAll();

    @Query("SELECT COUNT(*) FROM categories")
    int count();

    @Query("SELECT * FROM categories WHERE id = :id LIMIT 1")
    Category findById(long id);

    @Query("DELETE FROM categories")
    void clear();
}

