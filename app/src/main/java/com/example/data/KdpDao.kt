package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface KdpDao {

    // --- Book Covers ---
    @Query("SELECT * FROM book_covers ORDER BY createdAt DESC")
    fun getAllBookCovers(): Flow<List<BookCover>>

    @Query("SELECT * FROM book_covers WHERE id = :id")
    fun getBookCoverByIdFlow(id: Int): Flow<BookCover?>

    @Query("SELECT * FROM book_covers WHERE id = :id")
    suspend fun getBookCoverById(id: Int): BookCover?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookCover(cover: BookCover): Long

    @Delete
    suspend fun deleteBookCover(cover: BookCover)


    // --- Manuscripts ---
    @Query("SELECT * FROM manuscripts ORDER BY createdAt DESC")
    fun getAllManuscripts(): Flow<List<Manuscript>>

    @Query("SELECT * FROM manuscripts WHERE id = :id")
    fun getManuscriptByIdFlow(id: Int): Flow<Manuscript?>

    @Query("SELECT * FROM manuscripts WHERE id = :id")
    suspend fun getManuscriptById(id: Int): Manuscript?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertManuscript(manuscript: Manuscript): Long

    @Delete
    suspend fun deleteManuscript(manuscript: Manuscript)


    // --- Chapters ---
    @Query("SELECT * FROM chapters WHERE manuscriptId = :manuscriptId ORDER BY number ASC, id ASC")
    fun getChaptersForManuscriptFlow(manuscriptId: Int): Flow<List<Chapter>>

    @Query("SELECT * FROM chapters WHERE id = :id")
    suspend fun getChapterById(id: Int): Chapter?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChapter(chapter: Chapter): Long

    @Delete
    suspend fun deleteChapter(chapter: Chapter)

    @Query("DELETE FROM chapters WHERE manuscriptId = :manuscriptId")
    suspend fun deleteChaptersForManuscript(manuscriptId: Int)
}
