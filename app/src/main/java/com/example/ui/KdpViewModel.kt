package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed interface AiState {
    object Idle : AiState
    object Loading : AiState
    data class Success(val response: String) : AiState
    data class Error(val message: String) : AiState
}

class KdpViewModel(val repository: KdpRepository) : ViewModel() {

    // --- State Observables ---
    val allCovers: StateFlow<List<BookCover>> = repository.allBookCovers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allManuscripts: StateFlow<List<Manuscript>> = repository.allManuscripts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedCover = MutableStateFlow<BookCover?>(null)
    val selectedCover: StateFlow<BookCover?> = _selectedCover.asStateFlow()

    private val _selectedManuscript = MutableStateFlow<Manuscript?>(null)
    val selectedManuscript: StateFlow<Manuscript?> = _selectedManuscript.asStateFlow()

    private val _activeChapters = MutableStateFlow<List<Chapter>>(emptyList())
    val activeChapters: StateFlow<List<Chapter>> = _activeChapters.asStateFlow()

    private val _selectedChapter = MutableStateFlow<Chapter?>(null)
    val selectedChapter: StateFlow<Chapter?> = _selectedChapter.asStateFlow()

    private val _aiState = MutableStateFlow<AiState>(AiState.Idle)
    val aiState: StateFlow<AiState> = _aiState.asStateFlow()

    // --- Actions ---

    // Covers
    fun createAndSelectNewCover(title: String, author: String, genre: String) {
        viewModelScope.launch {
            val defaultColor = when (genre.lowercase()) {
                "sci-fi / fantasy", "sci-fi" -> "#0F172A" to "#8B5CF6" // Indigo/Slate
                "thriller", "mystery" -> "#111827" to "#EF4444" // Deep Black/Red
                "romance" -> "#FFF5F5" to "#EC4899" // Ivory/Pink
                "self-help", "business" -> "#1E293B" to "#F59E0B" // Navy/Amber
                else -> "#27272A" to "#D4AF37" // Zinc/Gold
            }

            val newCover = BookCover(
                title = title,
                author = author,
                genre = genre,
                backgroundColorHex = defaultColor.first,
                accentColorHex = defaultColor.second,
                textColorHex = if (defaultColor.first == "#FFF5F5") "#1F2937" else "#F9FAFB"
            )
            val newId = repository.saveBookCover(newCover).toInt()
            _selectedCover.value = newCover.copy(id = newId)
        }
    }

    fun createCoverFromTemplate(title: String, author: String, template: BookCoverTemplate) {
        viewModelScope.launch {
            val newCover = BookCover(
                title = title,
                author = author,
                genre = template.genre,
                backgroundColorHex = template.backgroundColorHex,
                accentColorHex = template.accentColorHex,
                textColorHex = template.textColorHex,
                fontName = template.fontName,
                layoutStyleName = template.layoutStyleName,
                spineText = "THE SPINE OF THE BOOK",
                backCoverText = "Synopsis here...\n\nPublished by Amazon KDP.",
                titleFontSize = template.titleFontSize,
                letterSpacing = template.letterSpacing,
                isUppercase = template.isUppercase,
                enableTextShadow = template.enableTextShadow,
                textShadowColorHex = template.textShadowColorHex,
                graphicElementName = template.graphicElementName,
                graphicScale = template.graphicScale,
                graphicOpacity = template.graphicOpacity,
                graphicOffsetX = 0f,
                graphicOffsetY = template.graphicOffsetY
            )
            val newId = repository.saveBookCover(newCover).toInt()
            _selectedCover.value = newCover.copy(id = newId)
        }
    }

    fun selectCover(id: Int) {
        viewModelScope.launch {
            val cover = repository.getBookCoverById(id)
            _selectedCover.value = cover
        }
    }

    fun updateSelectedCoverState(updated: BookCover) {
        _selectedCover.value = updated
    }

    fun saveActiveCover() {
        _selectedCover.value?.let { cover ->
            viewModelScope.launch {
                repository.saveBookCover(cover)
            }
        }
    }

    fun deleteCover(cover: BookCover) {
        viewModelScope.launch {
            repository.deleteBookCover(cover)
            if (_selectedCover.value?.id == cover.id) {
                _selectedCover.value = null
            }
        }
    }

    // Manuscripts
    fun createAndSelectNewManuscript(title: String, author: String, genre: String) {
        viewModelScope.launch {
            val newManuscript = Manuscript(
                title = title,
                author = author,
                genre = genre
            )
            val mId = repository.saveManuscript(newManuscript).toInt()
            _selectedManuscript.value = newManuscript.copy(id = mId)
            
            // Create a default first chapter
            val firstChapter = Chapter(
                manuscriptId = mId,
                number = 1,
                title = "Introduction",
                content = "Write your first chapter opening here."
            )
            repository.saveChapter(firstChapter)
            loadChaptersForManuscript(mId)
        }
    }

    fun selectManuscript(id: Int) {
        viewModelScope.launch {
            val m = repository.getManuscriptById(id)
            _selectedManuscript.value = m
            if (m != null) {
                loadChaptersForManuscript(m.id)
            } else {
                _activeChapters.value = emptyList()
                _selectedChapter.value = null
            }
        }
    }

    fun updateSelectedManuscriptState(updated: Manuscript) {
        _selectedManuscript.value = updated
    }

    fun saveActiveManuscript() {
        _selectedManuscript.value?.let { m ->
            viewModelScope.launch {
                repository.saveManuscript(m)
            }
        }
    }

    fun deleteManuscript(manuscript: Manuscript) {
        viewModelScope.launch {
            repository.deleteManuscript(manuscript)
            if (_selectedManuscript.value?.id == manuscript.id) {
                _selectedManuscript.value = null
                _activeChapters.value = emptyList()
                _selectedChapter.value = null
            }
        }
    }

    // Chapters
    fun loadChaptersForManuscript(mId: Int) {
        viewModelScope.launch {
            repository.getChaptersForManuscriptFlow(mId).collect { list ->
                _activeChapters.value = list
                // If a chapter is selected and it’s in the list, update it
                val currentSel = _selectedChapter.value
                if (currentSel != null) {
                    _selectedChapter.value = list.firstOrNull { it.id == currentSel.id }
                } else {
                    _selectedChapter.value = list.firstOrNull()
                }
            }
        }
    }

    fun selectChapter(chapter: Chapter) {
        _selectedChapter.value = chapter
    }

    fun addChapterToActiveManuscript() {
        val m = _selectedManuscript.value ?: return
        viewModelScope.launch {
            val nextNum = (_activeChapters.value.maxByOrNull { it.number }?.number ?: 0) + 1
            val newChapter = Chapter(
                manuscriptId = m.id,
                number = nextNum,
                title = "Chapter $nextNum",
                content = "Start drafting your chapter content here..."
            )
            val newId = repository.saveChapter(newChapter).toInt()
            _selectedChapter.value = newChapter.copy(id = newId)
        }
    }

    fun updateActiveChapterContent(title: String, content: String) {
        val chapter = _selectedChapter.value ?: return
        val updated = chapter.copy(title = title, content = content)
        _selectedChapter.value = updated
        viewModelScope.launch {
            repository.saveChapter(updated)
        }
    }

    fun deleteChapter(chapter: Chapter) {
        viewModelScope.launch {
            repository.deleteChapter(chapter)
            if (_selectedChapter.value?.id == chapter.id) {
                _selectedChapter.value = _activeChapters.value.firstOrNull { it.id != chapter.id }
            }
        }
    }

    // --- Gemini AI Features ---

    fun resetAiState() {
        _aiState.value = AiState.Idle
    }

    /**
     * Ask Gemini to generate cover design concept instructions.
     */
    fun brainstormCoverConcept(bookTitle: String, argGenre: String, keywordBrief: String) {
        _aiState.value = AiState.Loading
        viewModelScope.launch {
            val system = """
                You are a professional Amazon KDP book cover designer. 
                Your goal is to suggest high-fidelity color concepts, layouts, and typeface pairings for KDP.
                Be concise and structured. Use clear formatting.
            """.trimIndent()

            val p = """
                Suggest a cover design blueprint for:
                Title: "$bookTitle"
                Genre: $argGenre
                Visual Brief / Keywords: $keywordBrief
                
                Please include:
                1. Dominant Background HEX Palette Suggestion (and why it works).
                2. Accent/Spot Color HEX Palette Suggestion.
                3. Font style guidelines (Header and Subtitle font suggestions, e.g., Serif, Modern Minimalist, Playful, Bold Sans-serif).
                4. Paperback layout ideas (Front graphic concept, back cover blurb layout spacing, and spine details). 
                5. A brief text prompt that the user can use for generating background illustrations.
            """.trimIndent()

            val response = GeminiService.askGemini(p, system, temperature = 0.8f)
            if (response.contains("Error:")) {
                _aiState.value = AiState.Error(response)
            } else {
                _aiState.value = AiState.Success(response)
            }
        }
    }

    /**
     * Ask Gemini to write a high-converting Kindle / paperback blurb.
     */
    fun generateKdpBlurb(bookTitle: String, protagonist: String, targetGenre: String, keyConflict: String, selectionTone: String) {
        _aiState.value = AiState.Loading
        viewModelScope.launch {
            val system = """
                You are an elite Amazon Copywriter who specializes in writing book descriptions (blurbs) that trigger high sales conversions for self-published indie authors.
                Use appropriate marketing structure: Punchy bold opening hook, intriguing story setup, key sell features/bullet-points of the book, and a clear Call To Action.
            """.trimIndent()

            val p = """
                Write an Amazon KDP Book Description (blurb) for:
                Book Title: "$bookTitle"
                Genre: $targetGenre
                Tone Selection: $selectionTone (e.g. hooky, emotional, intellectual, mysterious)
                Protagonist Details: $protagonist
                Core Conflict / Solution offered: $keyConflict
                
                Ensure the response utilizes clean formatting with clear paragraph breaks, bold headings where appropriate (formatted as **Text**), and lists using bullet points so it is copy-paste ready for KDP HTML boxes.
            """.trimIndent()

            val response = GeminiService.askGemini(p, system, temperature = 0.7f)
            if (response.contains("Error:")) {
                _aiState.value = AiState.Error(response)
            } else {
                _aiState.value = AiState.Success(response)
            }
        }
    }

    /**
     * Ask Gemini to suggest categories & secret SEO keywords.
     */
    fun findCategoriesKeywords(bookSummary: String, targetGenre: String) {
        _aiState.value = AiState.Loading
        viewModelScope.launch {
            val system = """
                You are an Amazon KDP self-publishing pricing & SEO metadata strategist.
                Your task is to find profitable, high-relevance search keywords (backend search terms) and appropriate Amazon Bookstore categories.
            """.trimIndent()

            val p = """
                Book Genre: $targetGenre
                Summary: $bookSummary
                
                Based on this, deliver:
                1. Exactly 7 optimized KDP Backend Search Phrases (each phrase must be under 50 characters, optimized for customer search volume and relevance, no duplicates).
                2. Three specialized, high-converting Amazon Browse Paths / Categories for self-publishing registration.
                3. An optimal pricing recommendation for eBook, paperback, and hardcover formats, along with royalty margin estimates.
                4. A brief, strategic tip on how to launch in this niche to maximize Amazon algorithms!
            """.trimIndent()

            val response = GeminiService.askGemini(p, system, temperature = 0.65f)
            if (response.contains("Error:")) {
                _aiState.value = AiState.Error(response)
            } else {
                _aiState.value = AiState.Success(response)
            }
        }
    }

    /**
     * Ask Gemini to proofread/assess a chapter of formatting.
     */
    fun auditChapterFormatting(chapterTitle: String, textToAudit: String) {
        _aiState.value = AiState.Loading
        viewModelScope.launch {
            val system = """
                You are a book formatter and typography consultant for traditional and self-published print books.
                You evaluate chapter content for pacing, spacing, visual reading flow on paper, drop cap appropriateness, and formatting polish.
            """.trimIndent()

            val p = """
                Chapter Title: "$chapterTitle"
                Chapter Text Sample:
                ---
                $textToAudit
                ---
                
                Review this text for the following print layout parameters:
                1. Opening layout structure (First word, letter capitalizations, drop-cap recommendations).
                2. Readability & Visual pacing (Where are lists, short lines, or dense paragraphs that might look intimidating on standard 6x9 print paper).
                3. Suggested edits to enhance sentence rhythm, vocabulary density, and avoid widows/orphans.
                4. Book design spacing recommendations (specific margins, line height multiplier, or ornamental separators '***' to use for scene transitions).
            """.trimIndent()

            val response = GeminiService.askGemini(p, system, temperature = 0.7f)
            if (response.contains("Error:")) {
                _aiState.value = AiState.Error(response)
            } else {
                _aiState.value = AiState.Success(response)
            }
        }
    }
}

class KdpViewModelFactory(private val repository: KdpRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(KdpViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return KdpViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
