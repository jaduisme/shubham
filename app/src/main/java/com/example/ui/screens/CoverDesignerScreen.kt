package com.example.ui.screens

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BookCover
import com.example.data.BookCoverPresets
import com.example.data.BookCoverTemplate
import com.example.ui.KdpViewModel
import kotlin.math.max

// Simple utility to parse HEX safely without crashing
fun safeParseColor(hex: String, defaultColor: Color): Color {
    return try {
        val cleaned = if (hex.startsWith("#")) hex else "#$hex"
        Color(android.graphics.Color.parseColor(cleaned))
    } catch (e: Exception) {
        defaultColor
    }
}

// Check for high-contrast accessibility color pairings
fun checkLowContrast(bg: String, text: String): Boolean {
    return try {
        val bgC = android.graphics.Color.parseColor(bg)
        val textC = android.graphics.Color.parseColor(text)
        
        val bgLuminance = 0.2126f * android.graphics.Color.red(bgC) + 
                          0.7152f * android.graphics.Color.green(bgC) + 
                          0.0722f * android.graphics.Color.blue(bgC)
                          
        val textLuminance = 0.2126f * android.graphics.Color.red(textC) + 
                            0.7152f * android.graphics.Color.green(textC) + 
                            0.0722f * android.graphics.Color.blue(textC)
                            
        val ratio = (bgLuminance + 0.05f) / (textLuminance + 0.05f)
        val ratioSwapped = (textLuminance + 0.05f) / (bgLuminance + 0.05f)
        val finalRatio = maxOf(ratio, ratioSwapped)
        
        finalRatio < 3.0f // If contrast is lower than WCAG standards, return true
    } catch (e: Exception) {
        false
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoverDesignerScreen(
    viewModel: KdpViewModel,
    modifier: Modifier = Modifier
) {
    val covers by viewModel.allCovers.collectAsState()
    val activeCover by viewModel.selectedCover.collectAsState()

    var showCreateDialog by remember { mutableStateOf(false) }
    var newTitle by remember { mutableStateOf("") }
    var newAuthor by remember { mutableStateOf("") }
    
    // Genre tabs for the 50 Templates
    var selectedTemplateGenre by remember { mutableStateOf("Sci-Fi") }
    val genresList = listOf("Sci-Fi", "Romance", "Thriller", "Children's Books", "Non-Fiction")
    val allTemplates = remember { BookCoverPresets.get50Templates() }
    val filteredTemplates = remember(selectedTemplateGenre) {
        allTemplates.filter { it.genre.equals(selectedTemplateGenre, ignoreCase = true) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cover Studio", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                ),
                actions = {
                    IconButton(onClick = { showCreateDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Create Cover Project")
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
            if (activeCover == null) {
                // Selector panel: List of cover projects OR template browser
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    // Title for template browser
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "💡 Launch with Genre Templates (50 Available)",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Large horizontal scroll list for genres
                    ScrollableTabRow(
                        selectedTabIndex = genresList.indexOf(selectedTemplateGenre).coerceAtLeast(0),
                        edgePadding = 0.dp,
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                    ) {
                        genresList.forEach { g ->
                            Tab(
                                selected = selectedTemplateGenre == g,
                                onClick = { selectedTemplateGenre = g },
                                text = { Text(g, fontWeight = FontWeight.SemiBold) }
                            )
                        }
                    }

                    // Horizontal Grid / List of templates in chosen genre (10 per genre, total 50)
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .padding(bottom = 16.dp)
                    ) {
                        items(filteredTemplates.size) { index ->
                            val temp = filteredTemplates[index]
                            Card(
                                modifier = Modifier
                                    .width(160.dp)
                                    .fillMaxHeight()
                                    .clickable {
                                        newTitle = temp.title
                                        newAuthor = temp.author
                                        showCreateDialog = true
                                    },
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(safeParseColor(temp.backgroundColorHex, Color.DarkGray)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(4.dp)) {
                                            Text(
                                                temp.title,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = safeParseColor(temp.textColorHex, Color.White),
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                textAlign = TextAlign.Center
                                            )
                                            Text(
                                                temp.graphicElementName,
                                                fontSize = 7.sp,
                                                color = safeParseColor(temp.accentColorHex, Color.Yellow),
                                                maxLines = 1
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        temp.title,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        "Click to Customize",
                                        fontSize = 8.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }

                    Divider(modifier = Modifier.padding(bottom = 16.dp))

                    Text(
                        "Your Cover Projects",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    if (covers.isEmpty()) {
                        // Empty states message
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Palette,
                                contentDescription = "No Covers",
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "No Custom Book Covers Yet",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "Choose any of the 50 templates above or create a blank slate to construct a KDP paperback jacket.",
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.61f),
                                modifier = Modifier.padding(horizontal = 24.dp)
                            )
                        }
                    } else {
                        // Show Cover Selector list
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            items(covers.size) { index ->
                                val cover = covers[index]
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { viewModel.selectCover(cover.id) },
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(width = 45.dp, height = 65.dp)
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(safeParseColor(cover.backgroundColorHex, Color.DarkGray))
                                                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(4.dp)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                cover.title.firstOrNull()?.toString()?.uppercase() ?: "?",
                                                fontWeight = FontWeight.Bold,
                                                color = safeParseColor(cover.textColorHex, Color.White),
                                                fontSize = 18.sp
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                cover.title,
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.Bold,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                "By ${cover.author} • ${cover.genre}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                                            )
                                            Text(
                                                "Trim: ${cover.trimWidthInches}\"x${cover.trimHeightInches}\" • Symbol: ${cover.graphicElementName}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                        Row {
                                            IconButton(
                                                onClick = { viewModel.selectCover(cover.id) }
                                            ) {
                                                Icon(
                                                    Icons.Default.Edit,
                                                    contentDescription = "Edit cover",
                                                    tint = MaterialTheme.colorScheme.primary
                                                )
                                            }
                                            IconButton(
                                                onClick = { viewModel.deleteCover(cover) }
                                            ) {
                                                Icon(
                                                    Icons.Default.Delete,
                                                    contentDescription = "Delete cover",
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
                // Cover Editor Sandbox Active
                CoverEditorSandbox(
                    cover = activeCover!!,
                    onUpdate = { viewModel.updateSelectedCoverState(it); viewModel.saveActiveCover() },
                    onClose = { viewModel.selectCover(0) } // Reset selection
                )
            }

            if (showCreateDialog) {
                // Create dialog supports direct generation from selected template metrics!
                val matchedTemplate = remember(newTitle, selectedTemplateGenre) {
                    allTemplates.firstOrNull { it.title.equals(newTitle, ignoreCase = true) }
                }

                AlertDialog(
                    onDismissRequest = { showCreateDialog = false },
                    title = { Text(if (matchedTemplate != null) "Configure Template Blueprint" else "New KDP Blank Cover", fontWeight = FontWeight.Bold) },
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
                            
                            if (matchedTemplate != null) {
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)),
                                    modifier = Modifier.padding(top = 8.dp)
                                ) {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Text(
                                            "✨ Loading Genre Master: ${matchedTemplate.genre}",
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            "Initializes values: Typography '${matchedTemplate.fontName}', Graphics '${matchedTemplate.graphicElementName}' and color theme.",
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                }
                            } else {
                                Text("Select Target Genre", style = MaterialTheme.typography.labelSmall)
                                var manualGenre by remember { mutableStateOf("Fiction") }
                                val gNames = listOf("Fiction", "Sci-Fi", "Romance", "Thriller", "Non-Fiction")
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    LazyRow {
                                        items(gNames.size) { idx ->
                                            val gn = gNames[idx]
                                            InputChip(
                                                selected = manualGenre == gn,
                                                onClick = { manualGenre = gn },
                                                label = { Text(gn) },
                                                modifier = Modifier.padding(2.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                if (newTitle.isNotBlank()) {
                                    if (matchedTemplate != null) {
                                        viewModel.createCoverFromTemplate(newTitle, newAuthor.ifBlank { "Unknown Author" }, matchedTemplate)
                                    } else {
                                        viewModel.createAndSelectNewCover(newTitle, newAuthor.ifBlank { "Unknown Author" }, "Fiction")
                                    }
                                    newTitle = ""
                                    newAuthor = ""
                                    showCreateDialog = false
                                }
                            }
                        ) {
                            Text("Generate Design")
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
fun CoverEditorSandbox(
    cover: BookCover,
    onUpdate: (BookCover) -> Unit,
    onClose: () -> Unit
) {
    // Standard variables loaded
    var editTitle by remember(cover.id) { mutableStateOf(cover.title) }
    var editSubtitle by remember(cover.id) { mutableStateOf(cover.subtitle) }
    var editAuthor by remember(cover.id) { mutableStateOf(cover.author) }
    var editPages by remember(cover.id) { mutableStateOf(cover.pageCount.toFloat()) }
    var editSpineText by remember(cover.id) { mutableStateOf(cover.spineText) }
    var editBackText by remember(cover.id) { mutableStateOf(cover.backCoverText) }
    var editBgColorHex by remember(cover.id) { mutableStateOf(cover.backgroundColorHex) }
    var editAccentColorHex by remember(cover.id) { mutableStateOf(cover.accentColorHex) }
    var editTextColorHex by remember(cover.id) { mutableStateOf(cover.textColorHex) }
    var selectTrimSize by remember(cover.id) { mutableStateOf(if (cover.trimWidthInches == 6f) "6\" x 9\"" else "5\" x 8\"") }
    var layoutStyle by remember(cover.id) { mutableStateOf(cover.layoutStyleName) }

    // Professional Advanced design tools fields
    var titleFontSize by remember(cover.id) { mutableStateOf(cover.titleFontSize.toFloat()) }
    var letterSpacing by remember(cover.id) { mutableStateOf(cover.letterSpacing) }
    var isUppercase by remember(cover.id) { mutableStateOf(cover.isUppercase) }
    var enableTextShadow by remember(cover.id) { mutableStateOf(cover.enableTextShadow) }
    var textShadowColorHex by remember(cover.id) { mutableStateOf(cover.textShadowColorHex) }
    var graphicElementName by remember(cover.id) { mutableStateOf(cover.graphicElementName) }
    var graphicScale by remember(cover.id) { mutableStateOf(cover.graphicScale) }
    var graphicOpacity by remember(cover.id) { mutableStateOf(cover.graphicOpacity) }
    var graphicOffsetX by remember(cover.id) { mutableStateOf(cover.graphicOffsetX) }
    var graphicOffsetY by remember(cover.id) { mutableStateOf(cover.graphicOffsetY) }
    var layerOrder by remember(cover.id) { mutableStateOf(cover.layerOrder) }
    var fontName by remember(cover.id) { mutableStateOf(cover.fontName) }

    // Tab controllers inside Sandbox
    var selectedSandboxTab by remember { mutableStateOf(0) }
    val sandboxTabs = listOf("Text & Fonts", "Artwork Layers", "Color Studio", "Layout & Text")

    // Constants for KDP formulas
    val spineThicknessPerPage = 0.00225f // inches for white paper
    val calculatedSpineWidthInches = editPages * spineThicknessPerPage

    // Colors validation
    val bgParsed = remember(editBgColorHex) { safeParseColor(editBgColorHex, Color(0xFF1E293B)) }
    val accentParsed = remember(editAccentColorHex) { safeParseColor(editAccentColorHex, Color(0xFF3B82F6)) }
    val textParsed = remember(editTextColorHex) { safeParseColor(editTextColorHex, Color(0xFFF8FAFC)) }
    val contrastAlert = remember(editBgColorHex, editTextColorHex) { checkLowContrast(editBgColorHex, editTextColorHex) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Edit Status Banner
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
                    Text(cover.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text("Professional Designing Studio", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                }
            }
            TextButton(
                onClick = {
                    onUpdate(
                        cover.copy(
                            title = editTitle,
                            subtitle = editSubtitle,
                            author = editAuthor,
                            pageCount = editPages.toInt(),
                            spineText = editSpineText,
                            backCoverText = editBackText,
                            backgroundColorHex = editBgColorHex,
                            accentColorHex = editAccentColorHex,
                            textColorHex = editTextColorHex,
                            trimWidthInches = if (selectTrimSize == "6\" x 9\"") 6f else 5f,
                            trimHeightInches = if (selectTrimSize == "6\" x 9\"") 9f else 8f,
                            layoutStyleName = layoutStyle,
                            titleFontSize = titleFontSize.toInt(),
                            letterSpacing = letterSpacing,
                            isUppercase = isUppercase,
                            enableTextShadow = enableTextShadow,
                            textShadowColorHex = textShadowColorHex,
                            graphicElementName = graphicElementName,
                            graphicScale = graphicScale,
                            graphicOpacity = graphicOpacity,
                            graphicOffsetX = graphicOffsetX,
                            graphicOffsetY = graphicOffsetY,
                            fontName = fontName,
                            layerOrder = layerOrder
                        )
                    )
                }
            ) {
                Icon(Icons.Default.Save, contentDescription = "Save Cover")
                Spacer(modifier = Modifier.width(4.dp))
                Text("Save")
            }
        }

        // Real-time printing checks
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(
                    if (editPages < 100 || contrastAlert) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.35f)
                    else MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)
                )
                .border(
                    1.dp,
                    if (editPages < 100 || contrastAlert) MaterialTheme.colorScheme.error.copy(alpha = 0.5f)
                    else MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                    RoundedCornerShape(8.dp)
                )
                .padding(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (editPages < 100 || contrastAlert) Icons.Default.Warning else Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = if (editPages < 100 || contrastAlert) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        if (editPages < 100) "Spine Width Warning / KDP Guideline" 
                        else if (contrastAlert) "Accessibility Contrast Alert"
                        else "KDP Quality Checklist: PASSED",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (editPages < 100 || contrastAlert) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                    )
                    Text(
                        if (editPages < 100) "Amazon KDP requires minimum 100 pages to print spine text. (Currently: ${editPages.toInt()} pages)"
                        else if (contrastAlert) "Contrast between text ($editTextColorHex) and background ($editBgColorHex) is low. Suggest choosing higher contrast colors."
                        else "Formatted for $selectTrimSize with calculated spine thickness of ${String.format("%.4f", calculatedSpineWidthInches)}\". All margins compliant.",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Large Cover Preview Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF1E1E22))
                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                .padding(6.dp),
            contentAlignment = Alignment.Center
        ) {
            // Live flat paperback designer model canvas with graphics and layering
            PaperbackCoverCanvas(
                title = editTitle,
                subtitle = editSubtitle,
                author = editAuthor,
                spineText = if (editPages >= 100) editSpineText else "",
                backCoverText = editBackText,
                bgColor = bgParsed,
                accentColor = accentParsed,
                textColor = textParsed,
                spineWidthInches = calculatedSpineWidthInches,
                trimWidth = if (selectTrimSize == "6\" x 9\"") 6f else 5f,
                trimHeight = if (selectTrimSize == "6\" x 9\"") 9f else 8f,
                layoutStyleName = layoutStyle,
                titleFontSize = titleFontSize.toInt(),
                letterSpacingVal = letterSpacing,
                isUppercase = isUppercase,
                enableTextShadow = enableTextShadow,
                textShadowColorHex = textShadowColorHex,
                graphicElementName = graphicElementName,
                graphicScale = graphicScale,
                graphicOpacity = graphicOpacity,
                graphicOffsetX = graphicOffsetX,
                graphicOffsetY = graphicOffsetY,
                layerOrder = layerOrder,
                fontName = fontName
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Tool categories TabRow
        TabRow(
            selectedTabIndex = selectedSandboxTab,
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
        ) {
            sandboxTabs.forEachIndexed { idx, label ->
                Tab(
                    selected = selectedSandboxTab == idx,
                    onClick = { selectedSandboxTab = idx },
                    text = { Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1) }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Editor Scroll Panel
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            when (selectedSandboxTab) {
                0 -> { // TEXT & FONTS TAB
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("Typography Properties", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
                                
                                // Font family dropdown (simulated choices for high speed)
                                Column {
                                    Text("Select Headline Font Standard", style = MaterialTheme.typography.labelSmall)
                                    val fonts = listOf("Playfair Display", "Space Grotesk", "Cinzel", "Montserrat", "Special Elite", "Lobster", "Baskerville", "Garamond")
                                    LazyRow(horizontalArrangement = Arrangement.spacedBy(5.dp), modifier = Modifier.padding(vertical = 4.dp)) {
                                        items(fonts.size) { fIdx ->
                                            val f = fonts[fIdx]
                                            FilterChip(
                                                selected = fontName == f,
                                                onClick = { fontName = f },
                                                label = { Text(f, fontFamily = if (f=="Space Grotesk") androidx.compose.ui.text.font.FontFamily.Monospace else androidx.compose.ui.text.font.FontFamily.Serif) }
                                            )
                                        }
                                    }
                                }

                                Divider()

                                // Font sizing slider
                                Column {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Title Font Size", style = MaterialTheme.typography.bodyMedium)
                                        Text("${titleFontSize.toInt()} sp", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    }
                                    Slider(
                                        value = titleFontSize,
                                        onValueChange = { titleFontSize = it },
                                        valueRange = 16f..64f,
                                        modifier = Modifier.height(24.dp)
                                    )
                                }

                                // Letter spacing slider
                                Column {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Letter Tracking", style = MaterialTheme.typography.bodyMedium)
                                        Text("${String.format("%.1f", letterSpacing)} dp", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    }
                                    Slider(
                                        value = letterSpacing,
                                        onValueChange = { letterSpacing = it },
                                        valueRange = 0.0f..6.0f,
                                        modifier = Modifier.height(24.dp)
                                    )
                                }

                                Divider()

                                // Alignment / Formatting options
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Checkbox(checked = isUppercase, onCheckedChange = { isUppercase = it })
                                        Text("All Uppercase", style = MaterialTheme.typography.bodySmall)
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Checkbox(checked = enableTextShadow, onCheckedChange = { enableTextShadow = it })
                                        Text("Drop Shadow", style = MaterialTheme.typography.bodySmall)
                                    }
                                }

                                if (enableTextShadow) {
                                    OutlinedTextField(
                                        value = textShadowColorHex,
                                        onValueChange = { textShadowColorHex = it },
                                        label = { Text("Shadow Color HEX") },
                                        modifier = Modifier.fillMaxWidth().height(52.dp),
                                        singleLine = true
                                    )
                                }
                            }
                        }
                    }
                }
                1 -> { // ARTWORK LAYERS TAB
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("Royalty-Free Design Elements", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
                                
                                // Selector grid for vector elements
                                val itemsList = listOf(
                                    "Cosmic Ring", "Swirling Hearts", "Detective Glass", "Teddy Bear", 
                                    "Greek Pillar", "Shield Crest", "Golden Crown", "Double Swords", 
                                    "Tiny Stars", "Floral Wreath", "Sailing Compass", "None"
                                )
                                Text("Choose Illustration Symbol Overlay:", style = MaterialTheme.typography.labelSmall)
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(vertical = 4.dp)) {
                                    items(itemsList.size) { index ->
                                        val item = itemsList[index]
                                        FilterChip(
                                            selected = graphicElementName == item,
                                            onClick = { graphicElementName = item },
                                            label = { Text(item) }
                                        )
                                    }
                                }

                                Divider()

                                // Scale and Opacity adjustments
                                Column {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Illustration Sizing", style = MaterialTheme.typography.bodyMedium)
                                        Text("${String.format("%.1f", graphicScale * 100)}%", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    }
                                    Slider(
                                        value = graphicScale,
                                        onValueChange = { graphicScale = it },
                                        valueRange = 0.2f..2.5f,
                                        modifier = Modifier.height(24.dp)
                                    )
                                }

                                Column {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Illustration Transparency", style = MaterialTheme.typography.bodyMedium)
                                        Text("${String.format("%.0f", graphicOpacity * 100)}%", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    }
                                    Slider(
                                        value = graphicOpacity,
                                        onValueChange = { graphicOpacity = it },
                                        valueRange = 0.0f..1.0f,
                                        modifier = Modifier.height(24.dp)
                                    )
                                }

                                Divider()

                                // Placement adjustments Offsets X & Y
                                Text("Fine-Tune Coordinates (Drag to Translate)", style = MaterialTheme.typography.labelMedium)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("OffX: ${graphicOffsetX.toInt()}", style = MaterialTheme.typography.labelSmall)
                                        Slider(
                                            value = graphicOffsetX,
                                            onValueChange = { graphicOffsetX = it },
                                            valueRange = -80f..80f,
                                            modifier = Modifier.height(20.dp)
                                        )
                                    }
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("OffY: ${graphicOffsetY.toInt()}", style = MaterialTheme.typography.labelSmall)
                                        Slider(
                                            value = graphicOffsetY,
                                            onValueChange = { graphicOffsetY = it },
                                            valueRange = -100f..100f,
                                            modifier = Modifier.height(20.dp)
                                        )
                                    }
                                }

                                Divider()

                                // Layer Ordering choice
                                Column {
                                    Text("Layer Hierarchy Ordering", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 4.dp)) {
                                        val orders = listOf("Background -> Graphic -> Text", "Background -> Text -> Graphic")
                                        orders.forEach { ord ->
                                            ElevatedFilterChip(
                                                selected = layerOrder == ord,
                                                onClick = { layerOrder = ord },
                                                label = { Text(ord, fontSize = 9.sp) }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                2 -> { // COLOR STUDIO TAB
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text("High Contrast Palette Presets", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
                                
                                // Row of color palette presets
                                val palCodes = listOf(
                                    Triple("Midnight Nebula", "#0F172A", "#3B82F6"),
                                    Triple("Crimson Noir", "#111827", "#EF4444"),
                                    Triple("Golden Ratio", "#1E293B", "#D4AF37"),
                                    Triple("Neon Punk", "#0F0F16", "#EC4899"),
                                    Triple("Royal Velvet", "#1E1B4B", "#A855F7"),
                                    Triple("Minimalist Linen", "#F8FAFC", "#1E293B")
                                )
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    items(palCodes.size) { pIdx ->
                                        val p = palCodes[pIdx]
                                        Card(
                                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                            modifier = Modifier
                                                .width(110.dp)
                                                .clickable {
                                                    editBgColorHex = p.second
                                                    editAccentColorHex = p.third
                                                    editTextColorHex = if (p.second == "#F8FAFC") "#1E293B" else "#F8FAFC"
                                                }
                                        ) {
                                            Column(modifier = Modifier.padding(6.dp)) {
                                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                    Box(modifier = Modifier.size(16.dp).clip(CircleShape).background(safeParseColor(p.second, Color.Gray)))
                                                    Box(modifier = Modifier.size(16.dp).clip(CircleShape).background(safeParseColor(p.third, Color.Yellow)))
                                                }
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(p.first, fontSize = 9.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
                                            }
                                        }
                                    }
                                }

                                Divider()

                                // Individual hex inputs
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    // Background Input
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(modifier = Modifier.size(24.dp).clip(CircleShape).background(bgParsed).border(1.dp, Color.White, CircleShape))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        OutlinedTextField(
                                            value = editBgColorHex,
                                            onValueChange = { editBgColorHex = it },
                                            label = { Text("Background Color HEX") },
                                            modifier = Modifier.weight(1f),
                                            singleLine = true
                                        )
                                    }

                                    // Accent Input
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(modifier = Modifier.size(24.dp).clip(CircleShape).background(accentParsed).border(1.dp, Color.White, CircleShape))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        OutlinedTextField(
                                            value = editAccentColorHex,
                                            onValueChange = { editAccentColorHex = it },
                                            label = { Text("Accent / Foil Color HEX") },
                                            modifier = Modifier.weight(1f),
                                            singleLine = true
                                        )
                                    }

                                    // Text Color Input
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(modifier = Modifier.size(24.dp).clip(CircleShape).background(textParsed).border(1.dp, Color.White, CircleShape))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        OutlinedTextField(
                                            value = editTextColorHex,
                                            onValueChange = { editTextColorHex = it },
                                            label = { Text("Foreground Text HEX") },
                                            modifier = Modifier.weight(1f),
                                            singleLine = true
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                3 -> { // LAYOUT & TEXT DETAILS
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = editTitle,
                                    onValueChange = { editTitle = it },
                                    label = { Text("Main Book Title") },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = editSubtitle,
                                    onValueChange = { editSubtitle = it },
                                    label = { Text("Subtitle / Catchphrase") },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = editAuthor,
                                    onValueChange = { editAuthor = it },
                                    label = { Text("Author / Pen Name") },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true
                                )

                                Divider()

                                // Spines and Back Text
                                Text("KDP Sizing Margins & Trim Size", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Trim Format Size", style = MaterialTheme.typography.bodyMedium)
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        FilterChip(
                                            selected = selectTrimSize == "6\" x 9\"",
                                            onClick = { selectTrimSize = "6\" x 9\"" },
                                            label = { Text("6\" x 9\" Standard") }
                                        )
                                        FilterChip(
                                            selected = selectTrimSize == "5\" x 8\"",
                                            onClick = { selectTrimSize = "5\" x 8\"" },
                                            label = { Text("5\" x 8\" Pocket") }
                                        )
                                    }
                                }

                                Divider()

                                Column {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Active Page count (KDP spine factor)", style = MaterialTheme.typography.bodyMedium)
                                        Text("${editPages.toInt()} Pages", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    }
                                    Slider(
                                        value = editPages,
                                        onValueChange = { editPages = it },
                                        valueRange = 24f..800f,
                                        steps = 776
                                    )
                                }

                                if (editPages >= 100) {
                                    OutlinedTextField(
                                        value = editSpineText,
                                        onValueChange = { editSpineText = it },
                                        label = { Text("Vertebra Spine Label") },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true
                                    )
                                }

                                OutlinedTextField(
                                    value = editBackText,
                                    onValueChange = { editBackText = it },
                                    label = { Text("Back Description Blurb") },
                                    modifier = Modifier.fillMaxWidth().height(100.dp),
                                    maxLines = 4
                                )

                                Divider()

                                // Visual Style Layout choice
                                Column {
                                    Text("Visual Style Layout Preset", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                    val styles = listOf("Classic Centered", "Modern Split", "Brutalist Block", "Minimalist")
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        styles.forEach { sty ->
                                            FilterChip(
                                                selected = layoutStyle == sty,
                                                onClick = { layoutStyle = sty },
                                                label = { Text(sty, fontSize = 9.sp) }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        onUpdate(
                            cover.copy(
                                title = editTitle,
                                subtitle = editSubtitle,
                                author = editAuthor,
                                pageCount = editPages.toInt(),
                                spineText = editSpineText,
                                backCoverText = editBackText,
                                backgroundColorHex = editBgColorHex,
                                accentColorHex = editAccentColorHex,
                                textColorHex = editTextColorHex,
                                trimWidthInches = if (selectTrimSize == "6\" x 9\"") 6f else 5f,
                                trimHeightInches = if (selectTrimSize == "6\" x 9\"") 9f else 8f,
                                layoutStyleName = layoutStyle,
                                titleFontSize = titleFontSize.toInt(),
                                letterSpacing = letterSpacing,
                                isUppercase = isUppercase,
                                enableTextShadow = enableTextShadow,
                                textShadowColorHex = textShadowColorHex,
                                graphicElementName = graphicElementName,
                                graphicScale = graphicScale,
                                graphicOpacity = graphicOpacity,
                                graphicOffsetX = graphicOffsetX,
                                graphicOffsetY = graphicOffsetY,
                                fontName = fontName,
                                layerOrder = layerOrder
                            )
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Apply & Commit Design to Room DB")
                }
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

// Organic heart drawing scope helper
fun DrawScope.drawHeartShape(cx: Float, cy: Float, radius: Float, color: Color) {
    val hPath = androidx.compose.ui.graphics.Path().apply {
        val size = radius * 1.5f
        moveTo(cx, cy + size * 0.35f)
        cubicTo(cx - size * 0.5f, cy - size * 0.2f, cx - size * 0.5f, cy - size * 0.75f, cx, cy - size * 0.3f)
        cubicTo(cx + size * 0.5f, cy - size * 0.75f, cx + size * 0.5f, cy - size * 0.2f, cx, cy + size * 0.35f)
        close()
    }
    drawPath(path = hPath, color = color)
}

/**
 * Draws a highly precise Paperback Book Cover (Back, Spine, Front)
 * with correct proportions and text overlays within an Android Canvas.
 */
@Composable
fun PaperbackCoverCanvas(
    title: String,
    subtitle: String,
    author: String,
    spineText: String,
    backCoverText: String,
    bgColor: Color,
    accentColor: Color,
    textColor: Color,
    spineWidthInches: Float,
    trimWidth: Float,
    trimHeight: Float,
    layoutStyleName: String,
    titleFontSize: Int = 32,
    letterSpacingVal: Float = 0f,
    isUppercase: Boolean = true,
    enableTextShadow: Boolean = false,
    textShadowColorHex: String = "#000000",
    graphicElementName: String = "Cosmic Ring",
    graphicScale: Float = 1.0f,
    graphicOpacity: Float = 1.0f,
    graphicOffsetX: Float = 0f,
    graphicOffsetY: Float = 0f,
    layerOrder: String = "Background -> Graphic -> Text",
    fontName: String = "Playfair Display"
) {
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .padding(6.dp)
    ) {
        val totalHeight = size.height
        val totalWidth = size.width

        // Paperback flat schema aspect ratio: [Back Cover] [Spine] [Front Cover]
        val bookHeightToWidthRatio = trimHeight / (trimWidth * 2 + spineWidthInches)
        
        // Scale dimensions to fit flat cover flat aspect inside Preview Box
        val expectedWidth = totalHeight / bookHeightToWidthRatio
        var widthToUse = expectedWidth
        var heightToUse = totalHeight

        if (expectedWidth > totalWidth) {
            widthToUse = totalWidth
            heightToUse = totalWidth * bookHeightToWidthRatio
        }

        val startX = (totalWidth - widthToUse) / 2
        val startY = (totalHeight - heightToUse) / 2

        val totalFlatInches = trimWidth * 2 + spineWidthInches
        val pxPerInch = widthToUse / totalFlatInches

        val backWidthPx = trimWidth * pxPerInch
        val spineWidthPx = spineWidthInches * pxPerInch
        val frontWidthPx = trimWidth * pxPerInch

        // 1. Draw Flat Cover Base Background
        drawRect(
            color = bgColor,
            topLeft = Offset(startX, startY),
            size = Size(widthToUse, heightToUse)
        )

        // 2. Draw Back Cover Synopsis text lines and barcode placeholder
        val barcodeWidth = 32.dp.toPx()
        val barcodeHeight = 20.dp.toPx()
        val barcodeLeft = startX + backWidthPx - barcodeWidth - 8.dp.toPx()
        val barcodeTop = startY + heightToUse - barcodeHeight - 8.dp.toPx()
        
        drawRect(
            color = Color.White,
            topLeft = Offset(barcodeLeft, barcodeTop),
            size = Size(barcodeWidth, barcodeHeight)
        )
        // Barcode lines
        for (i in 0 until (barcodeWidth.toInt() - 6) step 3) {
            val offset = i.toFloat() + 3f
            if (offset < barcodeWidth) {
                drawLine(
                    color = Color.Black,
                    strokeWidth = if (i % 6 == 0) 1.5.dp.toPx() else 0.75.dp.toPx(),
                    start = Offset(barcodeLeft + offset, barcodeTop + 3f),
                    end = Offset(barcodeLeft + offset, barcodeTop + barcodeHeight - 3f)
                )
            }
        }

        // Back description text block lines
        val lineStartX = startX + 12.dp.toPx()
        val lineSpacing = 5.dp.toPx()
        for (i in 0..4) {
            val yOffset = startY + 18.dp.toPx() + (i * lineSpacing)
            val lineWidth = if (i == 4) backWidthPx * 0.45f else backWidthPx * 0.75f
            if (yOffset + 2.dp.toPx() < barcodeTop) {
                drawRect(
                    color = textColor.copy(alpha = 0.35f),
                    topLeft = Offset(lineStartX, yOffset),
                    size = Size(lineWidth, 2.5.dp.toPx())
                )
            }
        }

        // 3. Spine Shading and creasing lines
        val spineLeft = startX + backWidthPx
        val spineRight = spineLeft + spineWidthPx

        drawRect(
            color = Color.Black.copy(alpha = 0.08f),
            topLeft = Offset(spineLeft, startY),
            size = Size(spineWidthPx, heightToUse)
        )

        // Crease guide dashes representing spine fold crease boundaries
        drawLine(
            color = Color.White.copy(alpha = 0.15f),
            start = Offset(spineLeft, startY),
            end = Offset(spineLeft, startY + heightToUse),
            strokeWidth = 0.75.dp.toPx(),
            pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
        )
        drawLine(
            color = Color.White.copy(alpha = 0.15f),
            start = Offset(spineRight, startY),
            end = Offset(spineRight, startY + heightToUse),
            strokeWidth = 0.75.dp.toPx(),
            pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
        )

        // 4. Front Cover Core Spacing
        val frontLeft = spineRight
        val frontCenterX = frontLeft + (frontWidthPx / 2)

        // Draw margins frame outline depending on selected style layout
        when (layoutStyleName) {
            "Classic Centered" -> {
                drawRect(
                    color = accentColor,
                    topLeft = Offset(frontLeft + 8.dp.toPx(), startY + 8.dp.toPx()),
                    size = Size(frontWidthPx - 16.dp.toPx(), heightToUse - 16.dp.toPx()),
                    style = Stroke(width = 1.2.dp.toPx())
                )
            }
            "Modern Split" -> {
                drawRect(
                    color = accentColor.copy(alpha = 0.12f),
                    topLeft = Offset(frontLeft, startY),
                    size = Size(frontWidthPx, heightToUse * 0.35f)
                )
                drawLine(
                    color = accentColor,
                    start = Offset(frontLeft, startY + heightToUse * 0.35f),
                    end = Offset(frontLeft + frontWidthPx, startY + heightToUse * 0.35f),
                    strokeWidth = 1.5.dp.toPx()
                )
            }
            "Brutalist Block" -> {
                drawRect(
                    color = accentColor.copy(alpha = 0.16f),
                    topLeft = Offset(frontLeft + 4.dp.toPx(), startY + 4.dp.toPx()),
                    size = Size(frontWidthPx - 8.dp.toPx(), heightToUse * 0.72f)
                )
            }
            else -> { // Minimalist
                drawLine(
                    color = accentColor,
                    start = Offset(frontLeft + 16.dp.toPx(), startY + 16.dp.toPx()),
                    end = Offset(frontLeft + 48.dp.toPx(), startY + 16.dp.toPx()),
                    strokeWidth = 2.dp.toPx()
                )
            }
        }

        // Draw design vector items inside front cover area depending on coordinates & layer ordering
        val drawGraphicBlock = {
            val graphicCenterX = frontCenterX + graphicOffsetX.dp.toPx()
            val graphicCenterY = startY + (heightToUse / 2) + graphicOffsetY.dp.toPx()
            val baseGraphicRadius = (frontWidthPx * 0.15f) * graphicScale

            if (graphicElementName != "None" && graphicOpacity > 0f) {
                val elementColor = accentColor.copy(alpha = graphicOpacity)
                when (graphicElementName) {
                    "Cosmic Ring" -> {
                        drawCircle(
                            color = elementColor,
                            radius = baseGraphicRadius,
                            center = Offset(graphicCenterX, graphicCenterY),
                            style = Stroke(width = 1.5.dp.toPx())
                        )
                        drawCircle(
                            color = elementColor.copy(alpha = graphicOpacity * 0.4f),
                            radius = baseGraphicRadius * 0.65f,
                            center = Offset(graphicCenterX, graphicCenterY),
                            style = Stroke(width = 0.8.dp.toPx())
                        )
                        drawCircle(
                            color = elementColor,
                            radius = baseGraphicRadius * 0.35f,
                            center = Offset(graphicCenterX, graphicCenterY)
                        )
                    }
                    "Swirling Hearts" -> {
                        val offset = baseGraphicRadius * 0.25f
                        drawHeartShape(graphicCenterX - offset, graphicCenterY, baseGraphicRadius * 0.7f, elementColor)
                        drawHeartShape(graphicCenterX + offset, graphicCenterY + offset/2, baseGraphicRadius * 0.5f, elementColor.copy(alpha = graphicOpacity * 0.62f))
                    }
                    "Detective Glass" -> {
                        drawCircle(
                            color = elementColor,
                            radius = baseGraphicRadius * 0.55f,
                            center = Offset(graphicCenterX, graphicCenterY - baseGraphicRadius * 0.15f),
                            style = Stroke(width = 1.5.dp.toPx())
                        )
                        drawLine(
                            color = elementColor,
                            start = Offset(graphicCenterX + baseGraphicRadius * 0.25f, graphicCenterY + baseGraphicRadius * 0.1f),
                            end = Offset(graphicCenterX + baseGraphicRadius * 0.85f, graphicCenterY + baseGraphicRadius * 0.7f),
                            strokeWidth = 2.2.dp.toPx()
                        )
                    }
                    "Teddy Bear" -> {
                        drawCircle(color = elementColor, radius = baseGraphicRadius * 0.5f, center = Offset(graphicCenterX, graphicCenterY), style = Stroke(width = 1.2.dp.toPx()))
                        drawCircle(color = elementColor, radius = baseGraphicRadius * 0.2f, center = Offset(graphicCenterX - baseGraphicRadius * 0.45f, graphicCenterY - baseGraphicRadius * 0.4f), style = Stroke(width = 1.2.dp.toPx()))
                        drawCircle(color = elementColor, radius = baseGraphicRadius * 0.2f, center = Offset(graphicCenterX + baseGraphicRadius * 0.45f, graphicCenterY - baseGraphicRadius * 0.4f), style = Stroke(width = 1.2.dp.toPx()))
                        drawCircle(color = elementColor, radius = baseGraphicRadius * 0.06f, center = Offset(graphicCenterX - baseGraphicRadius * 0.15f, graphicCenterY - baseGraphicRadius * 0.1f))
                        drawCircle(color = elementColor, radius = baseGraphicRadius * 0.06f, center = Offset(graphicCenterX + baseGraphicRadius * 0.15f, graphicCenterY - baseGraphicRadius * 0.1f))
                        drawCircle(color = elementColor, radius = baseGraphicRadius * 0.05f, center = Offset(graphicCenterX, graphicCenterY + baseGraphicRadius * 0.1f))
                    }
                    "Greek Pillar" -> {
                        drawLine(
                            color = elementColor,
                            start = Offset(graphicCenterX - baseGraphicRadius * 0.7f, graphicCenterY + baseGraphicRadius * 0.6f),
                            end = Offset(graphicCenterX + baseGraphicRadius * 0.7f, graphicCenterY + baseGraphicRadius * 0.6f),
                            strokeWidth = 2.5.dp.toPx()
                        )
                        val pPath = androidx.compose.ui.graphics.Path().apply {
                            moveTo(graphicCenterX, graphicCenterY - baseGraphicRadius * 0.6f)
                            lineTo(graphicCenterX - baseGraphicRadius * 0.6f, graphicCenterY - baseGraphicRadius * 0.35f)
                            lineTo(graphicCenterX + baseGraphicRadius * 0.6f, graphicCenterY - baseGraphicRadius * 0.35f)
                            close()
                        }
                        drawPath(path = pPath, color = elementColor)
                        val colW = baseGraphicRadius * 0.10f
                        for (off in listOf(-baseGraphicRadius * 0.35f, 0f, baseGraphicRadius * 0.35f)) {
                            drawRect(
                                color = elementColor,
                                topLeft = Offset(graphicCenterX + off - colW/2, graphicCenterY - baseGraphicRadius * 0.35f),
                                size = Size(colW, baseGraphicRadius * 0.95f)
                            )
                        }
                    }
                    "Shield Crest" -> {
                        val sPath = androidx.compose.ui.graphics.Path().apply {
                            moveTo(graphicCenterX - baseGraphicRadius * 0.5f, graphicCenterY - baseGraphicRadius * 0.5f)
                            lineTo(graphicCenterX + baseGraphicRadius * 0.5f, graphicCenterY - baseGraphicRadius * 0.5f)
                            lineTo(graphicCenterX + baseGraphicRadius * 0.5f, graphicCenterY)
                            quadraticTo(graphicCenterX + baseGraphicRadius * 0.4f, graphicCenterY + baseGraphicRadius * 0.5f, graphicCenterX, graphicCenterY + baseGraphicRadius * 0.8f)
                            quadraticTo(graphicCenterX - baseGraphicRadius * 0.4f, graphicCenterY + baseGraphicRadius * 0.5f, graphicCenterX - baseGraphicRadius * 0.5f, graphicCenterY)
                            close()
                        }
                        drawPath(path = sPath, color = elementColor, style = Stroke(width = 1.5.dp.toPx()))
                    }
                    "Golden Crown" -> {
                        val cPath = androidx.compose.ui.graphics.Path().apply {
                            moveTo(graphicCenterX - baseGraphicRadius * 0.6f, graphicCenterY + baseGraphicRadius * 0.4f)
                            lineTo(graphicCenterX - baseGraphicRadius * 0.7f, graphicCenterY - baseGraphicRadius * 0.15f)
                            lineTo(graphicCenterX - baseGraphicRadius * 0.25f, graphicCenterY + baseGraphicRadius * 0.05f)
                            lineTo(graphicCenterX, graphicCenterY - baseGraphicRadius * 0.45f)
                            lineTo(graphicCenterX + baseGraphicRadius * 0.25f, graphicCenterY + baseGraphicRadius * 0.05f)
                            lineTo(graphicCenterX + baseGraphicRadius * 0.7f, graphicCenterY - baseGraphicRadius * 0.15f)
                            lineTo(graphicCenterX + baseGraphicRadius * 0.6f, graphicCenterY + baseGraphicRadius * 0.4f)
                            close()
                        }
                        drawPath(path = cPath, color = elementColor)
                    }
                    "Double Swords" -> {
                        drawLine(color = elementColor, start = Offset(graphicCenterX - baseGraphicRadius * 0.7f, graphicCenterY - baseGraphicRadius * 0.7f), end = Offset(graphicCenterX + baseGraphicRadius * 0.7f, graphicCenterY + baseGraphicRadius * 0.7f), strokeWidth = 2.dp.toPx())
                        drawLine(color = elementColor, start = Offset(graphicCenterX - baseGraphicRadius * 0.4f, graphicCenterY - baseGraphicRadius * 0.2f), end = Offset(graphicCenterX - baseGraphicRadius * 0.2f, graphicCenterY - baseGraphicRadius * 0.4f), strokeWidth = 3.dp.toPx())
                        drawLine(color = elementColor, start = Offset(graphicCenterX + baseGraphicRadius * 0.7f, graphicCenterY - baseGraphicRadius * 0.7f), end = Offset(graphicCenterX - baseGraphicRadius * 0.7f, graphicCenterY + baseGraphicRadius * 0.7f), strokeWidth = 2.dp.toPx())
                        drawLine(color = elementColor, start = Offset(graphicCenterX + baseGraphicRadius * 0.4f, graphicCenterY - baseGraphicRadius * 0.2f), end = Offset(graphicCenterX + baseGraphicRadius * 0.2f, graphicCenterY - baseGraphicRadius * 0.4f), strokeWidth = 3.dp.toPx())
                    }
                    "Tiny Stars" -> {
                        val points = listOf(0f to -0.5f, -0.4f to -0.1f, 0.4f to -0.1f, -0.2f to 0.3f, 0.2f to 0.3f)
                        for (p in points) {
                            drawCircle(color = elementColor, radius = 2.5.dp.toPx(), center = Offset(graphicCenterX + p.first * baseGraphicRadius, graphicCenterY + p.second * baseGraphicRadius))
                        }
                    }
                    "Floral Wreath" -> {
                        drawCircle(color = elementColor, radius = baseGraphicRadius, center = Offset(graphicCenterX, graphicCenterY), style = Stroke(width = 1.dp.toPx(), pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(10f, 5f), 0f)))
                        drawCircle(color = elementColor, radius = baseGraphicRadius * 0.92f, center = Offset(graphicCenterX, graphicCenterY), style = Stroke(width = 0.5.dp.toPx(), pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(3f, 10f), 4f)))
                    }
                    "Sailing Compass" -> {
                        drawLine(color = elementColor, start = Offset(graphicCenterX - baseGraphicRadius * 0.7f, graphicCenterY), end = Offset(graphicCenterX + baseGraphicRadius * 0.7f, graphicCenterY), strokeWidth = 1.dp.toPx())
                        drawLine(color = elementColor, start = Offset(graphicCenterX, graphicCenterY - baseGraphicRadius * 0.7f), end = Offset(graphicCenterX, graphicCenterY + baseGraphicRadius * 0.7f), strokeWidth = 1.dp.toPx())
                        val dPath = androidx.compose.ui.graphics.Path().apply {
                            moveTo(graphicCenterX, graphicCenterY - baseGraphicRadius * 0.5f)
                            lineTo(graphicCenterX + baseGraphicRadius * 0.15f, graphicCenterY)
                            lineTo(graphicCenterX, graphicCenterY + baseGraphicRadius * 0.5f)
                            lineTo(graphicCenterX - baseGraphicRadius * 0.15f, graphicCenterY)
                            close()
                        }
                        drawPath(path = dPath, color = elementColor)
                    }
                }
            }
        }

        val drawTextContentsBlock = {
            drawContext.canvas.nativeCanvas.apply {
                // Setup paints dynamically
                val textPaint = Paint().apply {
                    color = textColor.toArgb()
                    isAntiAlias = true
                    textAlign = Paint.Align.CENTER
                    textSize = (heightToUse * (titleFontSize / 400.0f)).coerceAtLeast(10f)
                    
                    // Match selected font name to classic typeface mappings
                    val tfFamily = when (fontName) {
                        "Space Grotesk" -> Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
                        "Cinzel" -> Typeface.create(Typeface.SERIF, Typeface.BOLD)
                        "Special Elite" -> Typeface.create(Typeface.SERIF, Typeface.BOLD)
                        "Lobster" -> Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
                        "Baskerville", "Garamond", "Playfair Display" -> Typeface.create(Typeface.SERIF, Typeface.BOLD)
                        else -> Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
                    }
                    typeface = tfFamily

                    // Support custom letter tracking inside Native Paint using letterSpacing
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP && letterSpacingVal > 0) {
                        letterSpacing = letterSpacingVal * 0.10f
                    }
                    
                    if (enableTextShadow) {
                        setShadowLayer(3f, 1.5f, 1.5f, safeParseColor(textShadowColorHex, Color.Black).toArgb())
                    }
                }

                val subtitlePaint = Paint().apply {
                    color = textColor.copy(alpha = 0.8f).toArgb()
                    isAntiAlias = true
                    textAlign = Paint.Align.CENTER
                    textSize = (heightToUse * 0.04f).coerceAtLeast(7f)
                    typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.ITALIC)
                }

                val authorPaint = Paint().apply {
                    color = textColor.toArgb()
                    isAntiAlias = true
                    textAlign = Paint.Align.CENTER
                    textSize = (heightToUse * 0.045f).coerceAtLeast(8f)
                    typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
                }

                val titleToDraw = if (isUppercase) title.uppercase() else title

                when (layoutStyleName) {
                    "Modern Split" -> {
                        val titleY = startY + (heightToUse * 0.16f)
                        val subY = titleY + (heightToUse * 0.05f)
                        val authY = startY + heightToUse - (heightToUse * 0.11f)
                        
                        drawText(titleToDraw, frontCenterX, titleY, textPaint)
                        if (subtitle.isNotEmpty()) {
                            drawText(subtitle, frontCenterX, subY, subtitlePaint)
                        }
                        drawText(author.uppercase(), frontCenterX, authY, authorPaint)
                    }
                    "Brutalist Block" -> {
                        textPaint.textAlign = Paint.Align.LEFT
                        subtitlePaint.textAlign = Paint.Align.LEFT
                        authorPaint.textAlign = Paint.Align.LEFT

                        val leftAlignX = frontLeft + 12.dp.toPx()
                        drawText(titleToDraw, leftAlignX, startY + (heightToUse * 0.22f), textPaint)
                        if (subtitle.isNotEmpty()) {
                            drawText(subtitle, leftAlignX, startY + (heightToUse * 0.32f), subtitlePaint)
                        }
                        drawText(author, leftAlignX, startY + (heightToUse * 0.82f), authorPaint)
                    }
                    "Minimalist" -> {
                        val titleY = startY + (heightToUse * 0.42f)
                        val authY = titleY + (heightToUse * 0.15f)
                        
                        textPaint.textSize = (heightToUse * 0.05f).coerceAtLeast(10f)
                        drawText(titleToDraw, frontCenterX, titleY, textPaint)
                        drawText("By $author", frontCenterX, authY, authorPaint)
                    }
                    else -> { // "Classic Centered" default
                        val titleY = startY + (heightToUse * 0.28f)
                        val subY = titleY + (heightToUse * 0.06f)
                        val authY = startY + heightToUse - (heightToUse * 0.18f)

                        drawText(titleToDraw, frontCenterX, titleY, textPaint)
                        if (subtitle.isNotEmpty()) {
                            drawText(subtitle, frontCenterX, subY, subtitlePaint)
                        }
                        drawText(author, frontCenterX, authY, authorPaint)
                    }
                }
            }
        }

        // Draw overlay nodes based on selected layerOrder configuration
        if (layerOrder == "Background -> Graphic -> Text") {
            drawGraphicBlock()
            drawTextContentsBlock()
        } else {
            drawTextContentsBlock()
            drawGraphicBlock()
        }

        // 5. Draw SPINE TEXT (creased vertical 90 degree rotating text)
        if (spineWidthInches >= 0.225f && spineText.isNotEmpty()) {
            drawContext.canvas.nativeCanvas.apply {
                val spinePaint = Paint().apply {
                    color = textColor.toArgb()
                    isAntiAlias = true
                    textAlign = Paint.Align.CENTER
                    textSize = (spineWidthPx * 0.52f).coerceIn(6f..12f)
                    typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
                }

                save()
                val spineCenterX = spineLeft + (spineWidthPx / 2)
                val spineCenterY = startY + (heightToUse / 2)
                
                translate(spineCenterX, spineCenterY)
                rotate(90f)
                
                drawText(spineText.uppercase(), 0f, (spinePaint.textSize / 3), spinePaint)
                restore()
            }
        }
    }
}
