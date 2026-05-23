package com.example.data

data class ManuscriptFormattingPreset(
    val name: String,
    val description: String,
    val trimWidthInches: Float,
    val trimHeightInches: Float,
    val innerMarginInches: Float, // Gutter
    val outerMarginInches: Float, // Side margin
    val lineSpacingMultiplier: Float,
    val baseFontSize: Int,
    val fontName: String,
    val paragraphIndentInches: Float,
    val enableDropCaps: Boolean
)

object ManuscriptPresets {

    fun get10Presets(): List<ManuscriptFormattingPreset> {
        return listOf(
            ManuscriptFormattingPreset(
                name = "Classic Novel (6\"x9\")",
                description = "Perfect for mainstream fiction and full-length novels. Elegant font sizing with standard KDP-safe gutters.",
                trimWidthInches = 6.0f,
                trimHeightInches = 9.0f,
                innerMarginInches = 0.75f,
                outerMarginInches = 0.50f,
                lineSpacingMultiplier = 1.15f,
                baseFontSize = 11,
                fontName = "Garamond",
                paragraphIndentInches = 0.25f,
                enableDropCaps = true
            ),
            ManuscriptFormattingPreset(
                name = "Compact Pocket (5\"x8\")",
                description = "Economical mass-market pocket-book sizing. Narrow gutters to maximize word densities.",
                trimWidthInches = 5.0f,
                trimHeightInches = 8.0f,
                innerMarginInches = 0.50f,
                outerMarginInches = 0.375f,
                lineSpacingMultiplier = 1.05f,
                baseFontSize = 10,
                fontName = "Baskerville",
                paragraphIndentInches = 0.20f,
                enableDropCaps = true
            ),
            ManuscriptFormattingPreset(
                name = "Trade Paperback (5.5\"x8.5\")",
                description = "The standard US Trade book standard. Offers an excellent balance of page count and reading speed.",
                trimWidthInches = 5.5f,
                trimHeightInches = 8.5f,
                innerMarginInches = 0.625f,
                outerMarginInches = 0.50f,
                lineSpacingMultiplier = 1.12f,
                baseFontSize = 11,
                fontName = "Classic Serif",
                paragraphIndentInches = 0.22f,
                enableDropCaps = true
            ),
            ManuscriptFormattingPreset(
                name = "Storybook Square (8.5\"x8.5\")",
                description = "Large square print standard for fully illustrated children's books and picture narratives.",
                trimWidthInches = 8.5f,
                trimHeightInches = 8.5f,
                innerMarginInches = 0.50f,
                outerMarginInches = 0.50f,
                lineSpacingMultiplier = 1.30f,
                baseFontSize = 14,
                fontName = "Lobster",
                paragraphIndentInches = 0.0f,
                enableDropCaps = false
            ),
            ManuscriptFormattingPreset(
                name = "Memoir / History (6\"x9\")",
                description = "Prestige memoir layout. Generous margins and beautiful type spacing for relaxed parsing.",
                trimWidthInches = 6.0f,
                trimHeightInches = 9.0f,
                innerMarginInches = 0.825f,
                outerMarginInches = 0.625f,
                lineSpacingMultiplier = 1.20f,
                baseFontSize = 12,
                fontName = "Playfair Display",
                paragraphIndentInches = 0.30f,
                enableDropCaps = true
            ),
            ManuscriptFormattingPreset(
                name = "Academic Letter (8.5\"x11\")",
                description = "Double-spaced layout styled to fit US educational thesis and research paper submissions.",
                trimWidthInches = 8.5f,
                trimHeightInches = 11.0f,
                innerMarginInches = 1.00f,
                outerMarginInches = 1.00f,
                lineSpacingMultiplier = 2.00f,
                baseFontSize = 12,
                fontName = "Classic Serif",
                paragraphIndentInches = 0.50f,
                enableDropCaps = false
            ),
            ManuscriptFormattingPreset(
                name = "Poetry Verse (5.5\"x8.5\")",
                description = "Extremely wide outer side borders designed specifically to keep stanza lines centered.",
                trimWidthInches = 5.5f,
                trimHeightInches = 8.5f,
                innerMarginInches = 1.20f,
                outerMarginInches = 1.20f,
                lineSpacingMultiplier = 1.25f,
                baseFontSize = 11,
                fontName = "Baskerville",
                paragraphIndentInches = 0.0f,
                enableDropCaps = false
            ),
            ManuscriptFormattingPreset(
                name = "Business Block (6\"x9\")",
                description = "Modern and sleek self-help layout. Uses flush-left blocks without indentations for technical speed.",
                trimWidthInches = 6.0f,
                trimHeightInches = 9.0f,
                innerMarginInches = 0.75f,
                outerMarginInches = 0.60f,
                lineSpacingMultiplier = 1.20f,
                baseFontSize = 11,
                fontName = "Montserrat",
                paragraphIndentInches = 0.0f,
                enableDropCaps = false
            ),
            ManuscriptFormattingPreset(
                name = "Sci-Fi Digest (5\"x8\")",
                description = "Compact science fiction/pulp design with futuristic geometric typeface indicators.",
                trimWidthInches = 5.0f,
                trimHeightInches = 8.0f,
                innerMarginInches = 0.55f,
                outerMarginInches = 0.45f,
                lineSpacingMultiplier = 1.10f,
                baseFontSize = 10,
                fontName = "Space Grotesk",
                paragraphIndentInches = 0.25f,
                enableDropCaps = true
            ),
            ManuscriptFormattingPreset(
                name = "Tech Guide (7\"x10\")",
                description = "Wide manual layout with spacious dimensions to print computer codes and instructional graphics.",
                trimWidthInches = 7.0f,
                trimHeightInches = 10.0f,
                innerMarginInches = 0.80f,
                outerMarginInches = 0.70f,
                lineSpacingMultiplier = 1.15f,
                baseFontSize = 10,
                fontName = "Montserrat",
                paragraphIndentInches = 0.20f,
                enableDropCaps = false
            )
        )
    }
}
