package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.graphics.Paint
import android.graphics.Typeface
import com.example.data.Chapter
import com.example.data.Manuscript
import com.example.data.ManuscriptPresets
import com.example.ui.KdpViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManuscriptFormatterScreen(
    viewModel: KdpViewModel,
    modifier: Modifier = Modifier
) {
    val manuscripts by viewModel.allManuscripts.collectAsState()
    val activeManuscript by viewModel.selectedManuscript.collectAsState()
    val chapters by viewModel.activeChapters.collectAsState()
    val selectedChapter by viewModel.selectedChapter.collectAsState()

    var showCreateDialog by remember { mutableStateOf(false) }
    var newTitle by remember { mutableStateOf("") }
    var newAuthor by remember { mutableStateOf("") }
    var newGenre by remember { mutableStateOf("Fiction") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manuscript Room", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                ),
                actions = {
                    IconButton(onClick = { showCreateDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Add New book")
                    }
                }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (activeManuscript == null) {
                if (manuscripts.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = "No Books",
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                            modifier = Modifier.size(72.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Format Your Manuscript",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Configure standard print margins, adjust binding gutters, write scene chapters, and inspect standard page compliance before publishing to KDP.",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = { showCreateDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Create Book Project")
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Text(
                            "Select an Active Book",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(manuscripts.size) { index ->
                                val m = manuscripts[index]
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { viewModel.selectManuscript(m.id) },
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                m.title,
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                "By ${m.author} • ${m.genre}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                                            )
                                            Text(
                                                "Trim: ${m.trimWidthInches}\"x${m.trimHeightInches}\" • Margins: ${m.innerMarginInches}\" Gutter / ${m.outerMarginInches}\" Side • Indent: ${m.paragraphIndentInches}\"",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.secondary
                                            )
                                        }
                                        Row {
                                            IconButton(
                                                onClick = { viewModel.selectManuscript(m.id) }
                                            ) {
                                                Icon(
                                                    Icons.Default.Edit,
                                                    contentDescription = "Format manuscript",
                                                    tint = MaterialTheme.colorScheme.primary
                                                )
                                            }
                                            IconButton(
                                                onClick = { viewModel.deleteManuscript(m) }
                                            ) {
                                                Icon(
                                                    Icons.Default.Delete,
                                                    contentDescription = "Delete book",
                                                    tint = MaterialTheme.colorScheme.error
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // Formatting Sandbox Active
                ManuscriptWorkspace(
                    manuscript = activeManuscript!!,
                    chapters = chapters,
                    selectedChapter = selectedChapter,
                    onUpdateManuscript = { viewModel.updateSelectedManuscriptState(it); viewModel.saveActiveManuscript() },
                    onSelectChapter = { viewModel.selectChapter(it) },
                    onAddChapter = { viewModel.addChapterToActiveManuscript() },
                    onUpdateChapter = { title, content -> viewModel.updateActiveChapterContent(title, content) },
                    onDeleteChapter = { viewModel.deleteChapter(it) },
                    onClose = { viewModel.selectManuscript(0) }
                )
            }

            if (showCreateDialog) {
                AlertDialog(
                    onDismissRequest = { showCreateDialog = false },
                    title = { Text("New KDP Book Project", fontWeight = FontWeight.Bold) },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = newTitle,
                                onValueChange = { newTitle = it },
                                label = { Text("Book Title") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = newAuthor,
                                onValueChange = { newAuthor = it },
                                label = { Text("Author Name") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            val genres = listOf("Fiction", "Self-Help / Non-Fiction", "Poetry", "Short Stories", "Biography")
                            Text("Select Template Styling", style = MaterialTheme.typography.labelSmall)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                LazyColumn(
                                    modifier = Modifier.height(100.dp)
                                ) {
                                    items(genres.size) { index ->
                                        val g = genres[index]
                                        InputChip(
                                            selected = newGenre == g,
                                            onClick = { newGenre = g },
                                            label = { Text(g) },
                                            modifier = Modifier.padding(2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                if (newTitle.isNotBlank()) {
                                    viewModel.createAndSelectNewManuscript(newTitle, newAuthor, newGenre)
                                    newTitle = ""
                                    newAuthor = ""
                                    showCreateDialog = false
                                }
                            }
                        ) {
                            Text("Create")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showCreateDialog = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManuscriptWorkspace(
    manuscript: Manuscript,
    chapters: List<Chapter>,
    selectedChapter: Chapter?,
    onUpdateManuscript: (Manuscript) -> Unit,
    onSelectChapter: (Chapter) -> Unit,
    onAddChapter: () -> Unit,
    onUpdateChapter: (String, String) -> Unit,
    onDeleteChapter: (Chapter) -> Unit,
    onClose: () -> Unit
) {
    // Manuscript Layout state variables
    var marginGutter by remember(manuscript.id) { mutableStateOf(manuscript.innerMarginInches) }
    var marginSide by remember(manuscript.id) { mutableStateOf(manuscript.outerMarginInches) }
    var fontSize by remember(manuscript.id) { mutableStateOf(manuscript.baseFontSize.toFloat()) }
    var fontName by remember(manuscript.id) { mutableStateOf(manuscript.fontName) }
    var lineSpacing by remember(manuscript.id) { mutableStateOf(manuscript.lineSpacingMultiplier) }
    var enableDropCaps by remember(manuscript.id) { mutableStateOf(manuscript.enableDropCaps) }
    var paragraphIndent by remember(manuscript.id) { mutableStateOf(manuscript.paragraphIndentInches) }
    var trimSizeSelection by remember(manuscript.id) { mutableStateOf(if (manuscript.trimWidthInches == 6f) "6\" x 9\"" else if (manuscript.trimWidthInches == 5f) "5\" x 8\"" else "5.5\" x 8.5\"") }

    var chapterTitleEdit by remember(selectedChapter?.id) { mutableStateOf(selectedChapter?.title ?: "") }
    var chapterContentEdit by remember(selectedChapter?.id) { mutableStateOf(selectedChapter?.content ?: "") }

    // Hardcover page estimates
    val totalWordsEstimate = remembersWordsEstimate(chapters)
    val estimatedPages = remember(totalWordsEstimate, fontSize, lineSpacing) {
        val baseMultiplier = 350f / (fontSize / 11f) / lineSpacing
        maxOf(24, (totalWordsEstimate / baseMultiplier).toInt())
    }

    // KDP Gutter Compliance Rule Checks:
    val minGutterRequiredInches = remember(estimatedPages) {
        when {
            estimatedPages <= 150 -> 0.375f
            estimatedPages <= 300 -> 0.500f
            estimatedPages <= 500 -> 0.625f
            else -> 0.750f
        }
    }
    val passesGutterCheck = marginGutter >= minGutterRequiredInches

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Workspace Header Banner
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onClose) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(manuscript.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text("Auto-Template Room", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
                }
            }
            TextButton(
                onClick = {
                    onUpdateManuscript(
                        manuscript.copy(
                            innerMarginInches = marginGutter,
                            outerMarginInches = marginSide,
                            baseFontSize = fontSize.toInt(),
                            fontName = fontName,
                            lineSpacingMultiplier = lineSpacing,
                            enableDropCaps = enableDropCaps,
                            paragraphIndentInches = paragraphIndent,
                            trimWidthInches = if (trimSizeSelection == "6\" x 9\"") 6f else if (trimSizeSelection == "5\" x 8\"") 5f else 5.5f,
                            trimHeightInches = if (trimSizeSelection == "6\" x 9\"") 9f else if (trimSizeSelection == "5\" x 8\"") 8f else 8.5f
                        )
                    )
                }
            ) {
                Icon(Icons.Default.Save, contentDescription = "Apply layout")
                Spacer(modifier = Modifier.width(4.dp))
                Text("Save Setup")
            }
        }

        // Live Gutter Binding Safety Alert
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(
                    if (!passesGutterCheck) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.35f)
                    else MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)
                )
                .border(
                    1.dp,
                    if (!passesGutterCheck) MaterialTheme.colorScheme.error.copy(alpha = 0.5f)
                    else MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                    RoundedCornerShape(8.dp)
                )
                .padding(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (!passesGutterCheck) Icons.Default.Warning else Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = if (!passesGutterCheck) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        if (!passesGutterCheck) "KDP Binding Warning: Inside Margin Gutter!" else "KDP Layout Integrity Checklist: PASSED",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (!passesGutterCheck) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                    )
                    Text(
                        if (!passesGutterCheck) "Estimated $estimatedPages pages requires minimum ${minGutterRequiredInches}\" Gutter fold margin to prevent binding text-loss. (Current: ${String.format("%.3f", marginGutter)}\")"
                        else "Formatted for $trimSizeSelection with estimated $estimatedPages pages (minimum gutter required: ${minGutterRequiredInches}\").",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }

        // 10 KDP Formatting Presets Bar (Horizontal Carousel)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
        ) {
            Text(
                "Apply Layout Standard Preset (10 Available):",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                val presetsList = ManuscriptPresets.get10Presets()
                items(presetsList.size) { index ->
                    val p = presetsList[index]
                    val isMatched = fontName.equals(p.fontName, ignoreCase = true) && 
                                    Math.abs(marginGutter - p.innerMarginInches) < 0.01f &&
                                    Math.abs(fontSize - p.baseFontSize) < 0.1f
                                    
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (isMatched) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier
                            .width(200.dp)
                            .clickable {
                                marginGutter = p.innerMarginInches
                                marginSide = p.outerMarginInches
                                fontSize = p.baseFontSize.toFloat()
                                fontName = p.fontName
                                lineSpacing = p.lineSpacingMultiplier
                                paragraphIndent = p.paragraphIndentInches
                                enableDropCaps = p.enableDropCaps
                                trimSizeSelection = if (p.trimWidthInches == 6f) "6\" x 9\"" else if (p.trimWidthInches == 5f) "5\" x 8\"" else "5.5\" x 8.5\""
                            }
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(p.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
                                if (isMatched) {
                                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.primary)
                                }
                            }
                            Text(
                                p.description,
                                fontSize = 8.sp,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Split view layout: Page preview, sliders, edit elements
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Simulated Book Page representation
            Box(
                modifier = Modifier
                    .weight(0.42f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFFCFAF2))
                    .border(1.dp, Color(0xFFE5DECE), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                BookPageCanvas(
                    title = manuscript.title,
                    chapterNum = selectedChapter?.number ?: 1,
                    chapterTitle = selectedChapter?.title ?: "Chapter 1",
                    gutterInches = marginGutter,
                    sideMarginInches = marginSide,
                    fontSize = fontSize.toInt(),
                    fontName = fontName,
                    hasDropCaps = enableDropCaps,
                    trimWidth = if (trimSizeSelection == "6\" x 9\"") 6f else if (trimSizeSelection == "5\" x 8\"") 5f else 5.5f,
                    trimHeight = if (trimSizeSelection == "6\" x 9\"") 9f else if (trimSizeSelection == "5\" x 8\"") 8f else 8.5f,
                    paragraphIndentInches = paragraphIndent
                )
            }

            // Quick Formatting sliders panel
            LazyColumn(
                modifier = Modifier
                    .weight(0.58f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                item {
                    Text("Margin & Trim Dimensions", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
                item {
                    Column {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Binding Gutter (Inner)", style = MaterialTheme.typography.labelSmall)
                            Text("${String.format("%.3f", marginGutter)}\"", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                        Slider(
                            value = marginGutter,
                            onValueChange = { marginGutter = it },
                            valueRange = 0.25f..1.0f,
                            modifier = Modifier.height(20.dp)
                        )
                    }
                }
                item {
                    Column {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Side Margin (Outer)", style = MaterialTheme.typography.labelSmall)
                            Text("${String.format("%.3f", marginSide)}\"", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                        Slider(
                            value = marginSide,
                            onValueChange = { marginSide = it },
                            valueRange = 0.25f..1.0f,
                            modifier = Modifier.height(20.dp)
                        )
                    }
                }
                item {
                    Column {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Paragraph Indentation", style = MaterialTheme.typography.labelSmall)
                            Text("${String.format("%.2f", paragraphIndent)}\"", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                        Slider(
                            value = paragraphIndent,
                            onValueChange = { paragraphIndent = it },
                            valueRange = 0.0f..0.6f,
                            modifier = Modifier.height(20.dp)
                        )
                    }
                }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Drop Caps", style = MaterialTheme.typography.labelSmall, modifier = Modifier.weight(1f))
                        Switch(
                            checked = enableDropCaps,
                            onCheckedChange = { enableDropCaps = it },
                            modifier = Modifier.scale(0.7f)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Chapters tabs bar
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Table of Chapters (${chapters.size})",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
                IconButton(
                    onClick = onAddChapter,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(Icons.Default.AddCircle, contentDescription = "Add chapter", tint = MaterialTheme.colorScheme.primary)
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(chapters.size) { index ->
                    val ch = chapters[index]
                    val isSel = selectedChapter?.id == ch.id
                    AssistChip(
                        onClick = { onSelectChapter(ch) },
                        label = { Text("Ch ${ch.number}: ${ch.title}") },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = if (isSel) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                        ),
                        trailingIcon = {
                            if (isSel && chapters.size > 1) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Delete",
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clickable { onDeleteChapter(ch) },
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Manuscript drafting / formatting text boxes
        if (selectedChapter != null) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = chapterTitleEdit,
                        onValueChange = { 
                            chapterTitleEdit = it
                            onUpdateChapter(it, chapterContentEdit)
                        },
                        label = { Text("Chapter Title") },
                        modifier = Modifier.weight(0.55f),
                        singleLine = true
                    )
                    
                    // Simple Font family picker dropdown
                    Card(
                        modifier = Modifier.weight(0.45f),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                    ) {
                        Column(
                            modifier = Modifier
                                .clickable {
                                    fontName = when (fontName) {
                                        "Garamond" -> "Baskerville"
                                        "Baskerville" -> "Classic Serif"
                                        "Classic Serif" -> "Space Grotesk"
                                        "Space Grotesk" -> "Montserrat"
                                        "Montserrat" -> "Lobster"
                                        else -> "Garamond"
                                    }
                                }
                                .padding(6.dp)
                                .fillMaxHeight(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Active Font", style = MaterialTheme.typography.labelSmall, fontSize = 8.sp)
                            Text(fontName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Line density parameters inside sandbox draft
                Row(modifier = Modifier.fillMaxWidth().height(42.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Font Sizing
                    Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                        Text("Size: ", style = MaterialTheme.typography.labelSmall)
                        Slider(
                            value = fontSize,
                            onValueChange = { fontSize = it },
                            valueRange = 8f..16f,
                            modifier = Modifier.weight(1f)
                        )
                        Text("${fontSize.toInt()}pt", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                    
                    // Line Spacing Multiplier
                    Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                        Text("Space: ", style = MaterialTheme.typography.labelSmall)
                        Slider(
                            value = lineSpacing,
                            onValueChange = { lineSpacing = it },
                            valueRange = 1.0f..2.5f,
                            modifier = Modifier.weight(1f)
                        )
                        Text("${String.format("%.2f", lineSpacing)}x", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Chapter content editor box
                OutlinedTextField(
                    value = chapterContentEdit,
                    onValueChange = { 
                        chapterContentEdit = it
                        onUpdateChapter(chapterTitleEdit, it)
                    },
                    placeholder = { Text("Begin drafting your chapter text here...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    shape = RoundedCornerShape(8.dp)
                )
                Spacer(modifier = Modifier.height(14.dp))
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Creating initial chapters...", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

/**
 * Calculates estimated wording in chapters
 */
@Composable
fun remembersWordsEstimate(chapters: List<Chapter>): Int {
    return remember(chapters) {
        chapters.sumOf { chapter ->
            chapter.content.split("\\s+".toRegex()).filter { it.isNotBlank() }.size
        }
    }
}

/**
 * Draws a gorgeous simulation of a paperback book page with correct margins,
 * gutter markings, drop caps, header elements, and guide grids.
 */
@Composable
fun BookPageCanvas(
    title: String,
    chapterNum: Int,
    chapterTitle: String,
    gutterInches: Float,
    sideMarginInches: Float,
    fontSize: Int,
    fontName: String,
    hasDropCaps: Boolean,
    trimWidth: Float,
    trimHeight: Float,
    paragraphIndentInches: Float = 0.25f
) {
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        val w = size.width
        val h = size.height

        // Proportionate book page layout
        val pageAspect = trimHeight / trimWidth
        val estimatedWidth = h / pageAspect
        var pw = estimatedWidth
        var ph = h

        if (estimatedWidth > w) {
            pw = w
            ph = w * pageAspect
        }

        val px = (w - pw) / 2
        val py = (h - ph) / 2

        // Draw Paper base of sheet
        drawRect(
            color = Color(0xFFFCFAF2),
            topLeft = Offset(px, py),
            size = Size(pw, ph)
        )

        // Draw Page Outline Frame Border
        drawRect(
            color = Color(0xFFE3DCCF),
            topLeft = Offset(px, py),
            size = Size(pw, ph),
            style = Stroke(width = 1.dp.toPx())
        )

        // Calculate Pixel equivalents of parameters
        val marginGutterPx = (gutterInches / trimWidth) * pw
        val marginSidePx = (sideMarginInches / trimWidth) * pw
        val marginTopPx = (0.75f / trimHeight) * ph
        val marginBottomPx = (0.75f / trimHeight) * ph

        // Draw Gutter creases (shading where binder glue sits)
        drawRect(
            color = Color.Black.copy(alpha = 0.04f),
            topLeft = Offset(px, py),
            size = Size(marginGutterPx, ph)
        )
        drawLine(
            color = Color.Black.copy(alpha = 0.08f),
            start = Offset(px + marginGutterPx, py),
            end = Offset(px + marginGutterPx, py + ph),
            strokeWidth = 1.dp.toPx()
        )

        // Draw margins boundaries overlay safely
        val safeLeft = px + marginGutterPx
        val safeRight = px + pw - marginSidePx
        val safeTop = py + marginTopPx
        val safeBottom = py + ph - marginBottomPx

        drawRect(
            color = Color(0xFF9CA3AF).copy(alpha = 0.15f),
            topLeft = Offset(safeLeft, safeTop),
            size = Size(safeRight - safeLeft, safeBottom - safeTop),
            style = Stroke(width = 0.75.dp.toPx(), pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(4f, 4f), 0f))
        )

        // Running header element text simulation
        drawContext.canvas.nativeCanvas.apply {
            val elementPaint = Paint().apply {
                color = android.graphics.Color.GRAY
                isAntiAlias = true
                textAlign = Paint.Align.CENTER
                textSize = (ph * 0.032f).coerceAtLeast(5f)
                typeface = Typeface.create(Typeface.SERIF, Typeface.NORMAL)
            }
            // Title running header
            val headerY = py + (ph * 0.06f)
            val headerX = px + (pw / 2) + (marginGutterPx / 2) - (marginSidePx / 2)
            drawText(title.uppercase(), headerX, headerY, elementPaint)

            // Running footer page number
            val footerY = py + ph - (ph * 0.04f)
            drawText("7", headerX, footerY, elementPaint)

            // Chapter header on page
            val chHeaderY = safeTop + 12.dp.toPx()
            elementPaint.color = android.graphics.Color.BLACK
            elementPaint.textSize = (ph * 0.042f).coerceAtLeast(7f)
            drawText("CHAPTER $chapterNum", headerX, chHeaderY, elementPaint)

            elementPaint.typeface = Typeface.create(Typeface.SERIF, Typeface.ITALIC)
            elementPaint.textSize = (ph * 0.034f).coerceAtLeast(6f)
            drawText(chapterTitle, headerX, chHeaderY + 8.dp.toPx(), elementPaint)

            // Simulated Content Body block columns
            val blockLeft = safeLeft + 3.dp.toPx()
            val blockRight = safeRight - 3.dp.toPx()
            val blockWidth = blockRight - blockLeft
            val textStartTop = chHeaderY + 24.dp.toPx()

            var currentLineY = textStartTop

            // Draw stylized Drop Cap Box indicator if enabled
            if (hasDropCaps) {
                val capPaint = Paint().apply {
                    color = android.graphics.Color.DKGRAY
                    isAntiAlias = true
                    textSize = (ph * 0.11f).coerceAtLeast(18f)
                    typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
                }
                drawText("O", blockLeft + 1.dp.toPx(), textStartTop + 10.dp.toPx(), capPaint)

                // First few lines indented because of drop-cap
                val capIndent = (ph * 0.08f).coerceAtLeast(12f)
                for (i in 0..1) {
                    drawRect(
                        color = Color.DarkGray.copy(alpha = 0.25f),
                        topLeft = Offset(blockLeft + capIndent, currentLineY),
                        size = Size(blockWidth - capIndent, 1.8.dp.toPx())
                    )
                    currentLineY += 7.dp.toPx()
                }
            }

            // Normal body content block lines utilizing paragraph indentation spacing
            var paraLineCount = 0
            val indentPixels = (paragraphIndentInches / trimWidth) * pw

            while (currentLineY + 5.dp.toPx() < safeBottom) {
                // If it's a new paragraph, offset starting X coordinate
                val isNewParagraph = paraLineCount % 5 == 0 && paragraphIndentInches > 0f
                val lineStart = if (isNewParagraph) blockLeft + indentPixels else blockLeft
                val availableWidth = if (isNewParagraph) blockWidth - indentPixels else blockWidth
                
                val endOffset = if (currentLineY + 12.dp.toPx() >= safeBottom) availableWidth * 0.65f else availableWidth
                
                drawRect(
                    color = Color.DarkGray.copy(alpha = 0.25f),
                    topLeft = Offset(lineStart, currentLineY),
                    size = Size(endOffset, 1.8.dp.toPx())
                )
                currentLineY += 7.dp.toPx()
                paraLineCount++
            }
        }
    }
}
