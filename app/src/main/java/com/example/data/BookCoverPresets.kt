package com.example.data

data class BookCoverTemplate(
    val templateId: String,
    val title: String,
    val subtitle: String,
    val author: String,
    val genre: String, // "Sci-Fi", "Romance", "Thriller", "Children's Books", "Non-Fiction"
    val backgroundColorHex: String,
    val accentColorHex: String,
    val textColorHex: String,
    val fontName: String, // "Playfair Display", "Space Grotesk", "Cinzel", "Montserrat", "Special Elite", "Lobster"
    val layoutStyleName: String, // "Classic Centered", "Modern Split", "Brutalist Block", "Minimalist"
    val graphicElementName: String, // "Cosmic Ring", "Swirling Hearts", "Detective Glass", "Teddy Bear", "Greek Pillar", etc.
    val titleFontSize: Int = 32,
    val letterSpacing: Float = 1.5f,
    val isUppercase: Boolean = true,
    val enableTextShadow: Boolean = true,
    val textShadowColorHex: String = "#000000",
    val graphicScale: Float = 1.0f,
    val graphicOpacity: Float = 0.85f,
    val graphicOffsetY: Float = -20f
)

object BookCoverPresets {

    fun get50Templates(): List<BookCoverTemplate> {
        val list = mutableListOf<BookCoverTemplate>()

        // ==================== SCI-FI (10 templates) ====================
        val scifiData = listOf(
            Triple("NEO-TOKYO 2099", "Chronicles of the Synth Rebellion", "Space Grotesk"),
            Triple("EVENT HORIZON", "Beyond the Boundary of Singularities", "Space Grotesk"),
            Triple("STELLAR REVENANT", "The Lost Armada of Orion", "Cinzel"),
            Triple("QUANTUM VOID", "Entangled Realities of the Void", "Space Grotesk"),
            Triple("CHRONO TRIGGER", "A Paradoxical Journey Through Time", "Montserrat"),
            Triple("CYBERPUNK NOIR", "Silicon Souls and Rain-Slicked Streets", "Space Grotesk"),
            Triple("BIO-MECH GENESIS", "The Evolution of Carbon and Steel", "Montserrat"),
            Triple("ASTEROID COURIER", "Dangerous Cargo in the Asteroid Belt", "Space Grotesk"),
            Triple("ROBOTICS EMPIRE", "When the Machines Built the Stars", "Cinzel"),
            Triple("ECLIPSE OF SOL", "The Last Day of the Solar System", "Space Grotesk")
        )
        scifiData.forEachIndexed { i, data ->
            val index = i + 1
            val bg = when(index % 4) {
                0 -> "#0B0F19" // Void Dark
                1 -> "#08101D" // Neon Blue Dark
                2 -> "#120B24" // Purple Nebula
                else -> "#0F0F16" // Charcoal
            }
            val accent = when(index % 4) {
                0 -> "#00F2FE" // Bright Cyan
                1 -> "#EC4899" // Hot Pink
                2 -> "#8B5CF6" // Purple Glow
                else -> "#10B981" // Radioactive Green
            }
            list.add(
                BookCoverTemplate(
                    templateId = "scifi_$index",
                    title = data.first,
                    subtitle = data.second,
                    author = "A. J. Brandon",
                    genre = "Sci-Fi",
                    backgroundColorHex = bg,
                    accentColorHex = accent,
                    textColorHex = "#F8FAFC",
                    fontName = data.third,
                    layoutStyleName = if (index % 3 == 0) "Modern Split" else if (index % 3 == 1) "Classic Centered" else "Brutalist Block",
                    graphicElementName = when (index % 4) {
                        0 -> "Cosmic Ring"
                        1 -> "Silicon Circuit"
                        2 -> "Quantum Grid"
                        else -> "Event Horizon"
                    },
                    titleFontSize = 36 - (index % 4),
                    letterSpacing = 2.0f + (index % 3),
                    isUppercase = true,
                    enableTextShadow = true,
                    textShadowColorHex = "#020617"
                )
            )
        }

        // ==================== ROMANCE (10 templates) ====================
        val romanceData = listOf(
            Triple("FLORAL WHISPER", "A Regency Love Story in Kent", "Playfair Display"),
            Triple("MIDNIGHT IN PARIS", "Eiffel Towers and Secret Rendezvous", "Lobster"),
            Triple("BURNING EMBERS", "When Rivals Spark an Unquenchable Fire", "Playfair Display"),
            Triple("LOVE ON THE REEF", "A Summer of Sunsets and Soulmates", "Lobster"),
            Triple("THE DUCHESS IN DISGUISE", "A Ballroom Scandal of False Identities", "Playfair Display"),
            Triple("COFFEE & RAINBOWS", "Warm Brews and Slow Morning Kisses", "Lobster"),
            Triple("STARS ALIGNED", "Astrology, Destiny, and Unexpected Chemistry", "Playfair Display"),
            Triple("SECRET SONNET", "Poetic Romance in a Coastal Town", "Playfair Display"),
            Triple("ACCIDENTALLY YOURS", "A Fake Marriage Romantic Comedy", "Lobster"),
            Triple("CHASING REDROSE", "Unlocking Her Heart's Velvet Chamber", "Playfair Display")
        )
        romanceData.forEachIndexed { i, data ->
            val index = i + 1
            val bg = when(index % 4) {
                0 -> "#FFF1F2" // Rose Water Light
                1 -> "#1F0F14" // Deep Burgundy
                2 -> "#FDF2F8" // Blush Pink Light
                else -> "#2A1215" // Crimson Noir
            }
            val accent = when(index % 4) {
                0 -> "#EC4899" // Rose Pink
                1 -> "#EF4444" // Cardinal Red
                2 -> "#F59E0B" // Champagne Gold
                else -> "#D946EF" // Orchid
            }
            val text = if (bg.startsWith("#F")) "#1E293B" else "#FFF1F2"
            list.add(
                BookCoverTemplate(
                    templateId = "romance_$index",
                    title = data.first,
                    subtitle = data.second,
                    author = "Penelope Reed",
                    genre = "Romance",
                    backgroundColorHex = bg,
                    accentColorHex = accent,
                    textColorHex = text,
                    fontName = data.third,
                    layoutStyleName = if (index % 2 == 0) "Classic Centered" else "Minimalist",
                    graphicElementName = "Swirling Hearts",
                    titleFontSize = 30 + (index % 3),
                    letterSpacing = 0.5f,
                    isUppercase = index % 3 == 0,
                    enableTextShadow = !bg.startsWith("#F"),
                    textShadowColorHex = "#27272A"
                )
            )
        }

        // ==================== THRILLER (10 templates) ====================
        val thrillerData = listOf(
            Triple("SHATTERED GLASS", "The Case of the Missing Heiress", "Special Elite"),
            Triple("COLD CASE FILES", "Some Murders Refuse to Stay Buried", "Montserrat"),
            Triple("THE MAN IN SHADOW", "He Sees Your Secrets from the Dark", "Special Elite"),
            Triple("CRIME WAVE", "A Gripping Serial Killer Investigation", "Montserrat"),
            Triple("RED CONSPIRACY", "Treason, Lies, and High-Stakes Betrayal", "Montserrat"),
            Triple("SILENT RETRIBUTION", "Vengeance Has a Cold, Slow Pulse", "Special Elite"),
            Triple("THE ALIBI TREE", "Lies Rooted Deep in Customary Soil", "Cinzel"),
            Triple("TERMINAL RUN", "The Race Against an Invisible Clock", "Montserrat"),
            Triple("BEHIND LOCKED DOORS", "Her Captor is Closer Than She Thinks", "Special Elite"),
            Triple("FINAL VERDICT", "Justice is Blind, But the Jury is Bought", "Cinzel")
        )
        thrillerData.forEachIndexed { i, data ->
            val index = i + 1
            val bg = when(index % 4) {
                0 -> "#09090B" // Obsidian
                1 -> "#18181B" // Charcoal Zinc
                2 -> "#180C0C" // Blood Cast Dark
                else -> "#0B1515" // Creepy Teal
            }
            val accent = when(index % 4) {
                0 -> "#DC2626" // Blood Red
                1 -> "#EAB308" // Hazard Yellow
                2 -> "#F97316" // Ember Orange
                else -> "#EF4444" // Crimson
            }
            list.add(
                BookCoverTemplate(
                    templateId = "thriller_$index",
                    title = data.first,
                    subtitle = data.second,
                    author = "K. D. Vance",
                    genre = "Thriller",
                    backgroundColorHex = bg,
                    accentColorHex = accent,
                    textColorHex = "#F4F4F5",
                    fontName = data.third,
                    layoutStyleName = if (index % 3 == 0) "Brutalist Block" else if (index % 3 == 1) "Modern Split" else "Classic Centered",
                    graphicElementName = "Detective Glass",
                    titleFontSize = 34 + (index % 3),
                    letterSpacing = 1.0f + (index % 2),
                    isUppercase = true,
                    enableTextShadow = true,
                    textShadowColorHex = "#000000"
                )
            )
        }

        // ==================== CHILDREN'S BOOKS (10 templates) ====================
        val childrenData = listOf(
            Triple("TEDDY'S CLOUD CASTLE", "A Bedtime Adventure in the Sky", "Lobster"),
            Triple("THE LITTLE STAR THAT SHONE", "Finding Your True Light in the Dark", "Lobster"),
            Triple("FOREST SAFARI", "The Day the Squirrels Met the Bears", "Montserrat"),
            Triple("SPARKLE THE UNICORN", "The Search for the Lost Rainbow Horn", "Lobster"),
            Triple("PUPPY'S FIRST SNOW", "Cold Paws and Warm Cozy Fireplaces", "Lobster"),
            Triple("THE ALIEN WHO LOVED JAM", "An Intergalactic Cozy Breakfast", "Montserrat"),
            Triple("MONSTER HOUSE PARTY", "Even Big Green Goblins Love to Dance", "Lobster"),
            Triple("WAVELENGTH WHALE", "A Deep Sea Song of Harmony", "Lobster"),
            Triple("DINO'S BIG STOMP", "A Tiny T-Rex Learning to Tread Softly", "Montserrat"),
            Triple("THE FLYING BICYCLE", "Sailing Above the Pine Trees of Eden", "Lobster")
        )
        childrenData.forEachIndexed { i, data ->
            val index = i + 1
            val bg = when(index % 4) {
                0 -> "#FEF08A" // Soft Yellow
                1 -> "#BFDBFE" // Soft Blue
                2 -> "#FBCFE8" // Soft Pink
                else -> "#CCFBF1" // Soft Mint Green
            }
            val accent = when(index % 4) {
                0 -> "#F97316" // Orange
                1 -> "#2563EB" // Bold Blue
                2 -> "#EC4899" // Hot Pink
                else -> "#0D9488" // Emerald Teal
            }
            list.add(
                BookCoverTemplate(
                    templateId = "children_$index",
                    title = data.first,
                    subtitle = data.second,
                    author = "Auntie Bella",
                    genre = "Children's Books",
                    backgroundColorHex = bg,
                    accentColorHex = accent,
                    textColorHex = "#1E293B",
                    fontName = data.third,
                    layoutStyleName = "Classic Centered",
                    graphicElementName = "Teddy Bear",
                    titleFontSize = 28 + (index % 3),
                    letterSpacing = 0.5f,
                    isUppercase = false,
                    enableTextShadow = false,
                    textShadowColorHex = "#FFFFFF"
                )
            )
        }

        // ==================== NON-FICTION (10 templates) ====================
        val nonfictionData = listOf(
            Triple("ATOMIC GROWTH", "10 Habits for Modern KDP Authors", "Cinzel"),
            Triple("THE FINANCIAL COMPASS", "Navigating the Changing Wealth Tide", "Montserrat"),
            Triple("CREATIVE FOCUS", "Mindfulness and Deep Work in the AI Era", "Cinzel"),
            Triple("EPIC LEADERSHIP", "How Real Leaders Orchestrate Change", "Montserrat"),
            Triple("NUTRITION DECODED", "Evaluating Bio-Hacking and Human Vitality", "Cinzel"),
            Triple("THE LAUREL PATHWAY", "A Academic Narrative of Greco-Roman Law", "Cinzel"),
            Triple("DIGITAL NOMAD blueprint", "Work Anywhere and Own Your Freedom", "Montserrat"),
            Triple("LAUNCH PAD METRICS", "SaaS Growth Playbook from Seed to Scale", "Montserrat"),
            Triple("DESIGN ARCHITECTURE", "A Standard Textbook of Material Form", "Cinzel"),
            Triple("MODERN STOICISM", "Finding Invincible Calm in Chaos", "Cinzel")
        )
        nonfictionData.forEachIndexed { i, data ->
            val index = i + 1
            val bg = when(index % 4) {
                0 -> "#0F172A" // Deep Corporate Navy
                1 -> "#F8FAFC" // Alabaster Slate Light
                2 -> "#022C22" // Emerald Academic Dark
                else -> "#1C1917" // Stone Sand Charcoal
            }
            val accent = when(index % 4) {
                0 -> "#3B82F6" // Electric Blue
                1 -> "#D4AF37" // Laurel Gold
                2 -> "#10B981" // Radiant Teal
                else -> "#F59E0B" // Rich Amber
            }
            val text = if (bg.startsWith("#F")) "#0F172A" else "#F1F5F9"
            list.add(
                BookCoverTemplate(
                    templateId = "nonfiction_$index",
                    title = data.first,
                    subtitle = data.second,
                    author = "Dr. Henry Slate",
                    genre = "Non-Fiction",
                    backgroundColorHex = bg,
                    accentColorHex = accent,
                    textColorHex = text,
                    fontName = data.third,
                    layoutStyleName = if (index % 3 == 0) "Minimalist" else if (index % 3 == 1) "Classic Centered" else "Modern Split",
                    graphicElementName = "Greek Pillar",
                    titleFontSize = 32 - (index % 3),
                    letterSpacing = 2.5f,
                    isUppercase = true,
                    enableTextShadow = !bg.startsWith("#F"),
                    textShadowColorHex = "#0F172A"
                )
            )
        }

        return list
    }
}
