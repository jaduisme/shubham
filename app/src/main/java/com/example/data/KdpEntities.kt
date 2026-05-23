package com.example.data

import androidx.room.*

@Entity(tableName = "book_covers")
data class BookCover(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val subtitle: String = "",
    val author: String = "",
    val genre: String = "Fiction",
    val trimWidthInches: Float = 6f,
    val trimHeightInches: Float = 9f,
    val pageCount: Int = 120,
    val themeName: String = "Corporate Slate",
    val backgroundColorHex: String = "#1E293B",
    val accentColorHex: String = "#3B82F6",
    val textColorHex: String = "#F8FAFC",
    val fontName: String = "Playfair Display",
    val spineText: String = "",
    val backCoverText: String = "About the book...",
    val layoutStyleName: String = "Classic Centered", // Classic Centered, Modern Split, Brutalist Block, Minimalist
    val logoAssetPath: String = "",
    val hasBarcodePlaceholder: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    
    // Suite of Professional Design Tools properties
    val titleFontSize: Int = 32,
    val letterSpacing: Float = 0f,
    val isUppercase: Boolean = true,
    val enableTextShadow: Boolean = false,
    val textShadowColorHex: String = "#000000",
    val graphicElementName: String = "Cosmic Ring", // Cosmic Ring, Swirling Hearts, Vintage Swirls, Teddy Bear, Stars, Shield
    val graphicScale: Float = 1.0f,
    val graphicOpacity: Float = 1.0f,
    val graphicOffsetX: Float = 0f,
    val graphicOffsetY: Float = 0f,
    val layerOrder: String = "Background -> Graphic -> Text" // "Background -> Graphic -> Text" or "Background -> Text -> Graphic"
)

@Entity(tableName = "manuscripts")
data class Manuscript(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val author: String = "",
    val genre: String = "Fiction",
    val targetWordCount: Int = 50000,
    val trimWidthInches: Float = 6f,
    val trimHeightInches: Float = 9f,
    val innerMarginInches: Float = 0.75f,
    val outerMarginInches: Float = 0.5f,
    val topMarginInches: Float = 0.75f,
    val bottomMarginInches: Float = 0.75f,
    val lineSpacingMultiplier: Float = 1.15f,
    val baseFontSize: Int = 11,
    val fontName: String = "Garamond",
    val hasBleed: Boolean = false,
    val enableDropCaps: Boolean = true,
    val description: String = "",
    val paragraphIndentInches: Float = 0.25f, // Added paragraph indentation preset support
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "chapters",
    foreignKeys = [
        ForeignKey(
            entity = Manuscript::class,
            parentColumns = ["id"],
            childColumns = ["manuscriptId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("manuscriptId")]
)
data class Chapter(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val manuscriptId: Int,
    val number: Int,
    val title: String,
    val content: String,
    val createdAt: Long = System.currentTimeMillis()
)
