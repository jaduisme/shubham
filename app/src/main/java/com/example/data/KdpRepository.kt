package com.example.data

import kotlinx.coroutines.flow.Flow

class KdpRepository(private val kdpDao: KdpDao) {

    // --- Book Covers ---
    val allBookCovers: Flow<List<BookCover>> = kdpDao.getAllBookCovers()

    fun getBookCoverByIdFlow(id: Int): Flow<BookCover?> = kdpDao.getBookCoverByIdFlow(id)

    suspend fun getBookCoverById(id: Int): BookCover? = kdpDao.getBookCoverById(id)

    suspend fun saveBookCover(cover: BookCover): Long = kdpDao.insertBookCover(cover)

    suspend fun deleteBookCover(cover: BookCover) = kdpDao.deleteBookCover(cover)


    // --- Manuscripts ---
    val allManuscripts: Flow<List<Manuscript>> = kdpDao.getAllManuscripts()

    fun getManuscriptByIdFlow(id: Int): Flow<Manuscript?> = kdpDao.getManuscriptByIdFlow(id)

    suspend fun getManuscriptById(id: Int): Manuscript? = kdpDao.getManuscriptById(id)

    suspend fun saveManuscript(manuscript: Manuscript): Long = kdpDao.insertManuscript(manuscript)

    suspend fun deleteManuscript(manuscript: Manuscript) {
        kdpDao.deleteChaptersForManuscript(manuscript.id)
        kdpDao.deleteManuscript(manuscript)
    }


    // --- Chapters ---
    fun getChaptersForManuscriptFlow(manuscriptId: Int): Flow<List<Chapter>> = 
        kdpDao.getChaptersForManuscriptFlow(manuscriptId)

    suspend fun getChapterById(id: Int): Chapter? = kdpDao.getChapterById(id)

    suspend fun saveChapter(chapter: Chapter): Long = kdpDao.insertChapter(chapter)

    suspend fun deleteChapter(chapter: Chapter) = kdpDao.deleteChapter(chapter)
}
