package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AiState
import com.example.ui.KdpViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiPortalScreen(
    viewModel: KdpViewModel,
    modifier: Modifier = Modifier
) {
    val aiState by viewModel.aiState.collectAsState()
    val context = LocalContext.current

    var selectedTab by remember { mutableStateOf(0) }
    val tabNames = listOf("SEO Finder", "Blurb Builder", "Cover Ideas", "Pacing Audit")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gemini KDP AI Assistant", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Horizontal scrollable Tabs
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                edgePadding = 16.dp,
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth()
            ) {
                tabNames.forEachIndexed { index, name ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { 
                            selectedTab = index
                            viewModel.resetAiState()
                        },
                        text = { Text(name, fontWeight = FontWeight.SemiBold) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Main View Splitting: Left is input forms, Right/Bottom is LLM Response Display
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    when (selectedTab) {
                        0 -> SeoKeywordsForm(viewModel = viewModel)
                        1 -> BlurbGeneratorForm(viewModel = viewModel)
                        2 -> CoverConceptForm(viewModel = viewModel)
                        3 -> ManuscriptPacingAuditForm(viewModel = viewModel)
                    }
                }

                // AI Response Section Drawer
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                        .padding(14.dp)
                ) {
                    when (val state = aiState) {
                        is AiState.Idle -> {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    "Ready to Coprocess",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleSmall
                                )
                                Text(
                                    "Fill in details above and press generate to request specialized metadata drafts or formatting audits from Gemini LLM.",
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.61f),
                                    modifier = Modifier.padding(horizontal = 24.dp)
                                )
                            }
                        }
                        is AiState.Loading -> {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    "Consulting KDP Algorithms & Formatting Rules...",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        is AiState.Success -> {
                            Column(modifier = Modifier.fillMaxSize()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        "Gemini Recommendation Output",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                    IconButton(
                                        onClick = {
                                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                            val clip = ClipData.newPlainText("KDP AI Output", state.response)
                                            clipboard.setPrimaryClip(clip)
                                            Toast.makeText(context, "Copied to clipboard!", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy text", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.background)
                                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                        .padding(10.dp)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .verticalScroll(rememberScrollState())
                                    ) {
                                        Text(
                                            text = state.response,
                                            fontSize = 11.sp,
                                            fontFamily = FontFamily.Monospace,
                                            lineHeight = 16.sp
                                        )
                                    }
                                }
                            }
                        }
                        is AiState.Error -> {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Error,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(40.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "Consultation Failed",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    state.message,
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SeoKeywordsForm(viewModel: KdpViewModel) {
    var genreInput by remember { mutableStateOf("Self-Help & Business") }
    var summaryInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("Metadata Strategy Planner", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        Text("Generates 7 Backend Keywords and Book Category Browse Paths based on competitive niches.", style = MaterialTheme.typography.bodySmall)

        OutlinedTextField(
            value = genreInput,
            onValueChange = { genreInput = it },
            label = { Text("Book Sub-genre") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = summaryInput,
            onValueChange = { summaryInput = it },
            label = { Text("Describe what your book is about...") },
            modifier = Modifier.fillMaxWidth().height(100.dp),
            maxLines = 4
        )

        Button(
            onClick = {
                if (summaryInput.isNotBlank()) {
                    viewModel.findCategoriesKeywords(summaryInput, genreInput)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Icon(Icons.Default.Search, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Find Profitable SEO Tags")
        }
    }
}

@Composable
fun BlurbGeneratorForm(viewModel: KdpViewModel) {
    var titleInput by remember { mutableStateOf("") }
    var protagonistInput by remember { mutableStateOf("") }
    var genreInput by remember { mutableStateOf("Cozy Mystery") }
    var conflictInput by remember { mutableStateOf("") }
    var selectedTone by remember { mutableStateOf("High-Converting Hooky") }

    val tones = listOf("High-Converting Hooky", "Deeply Emotional", "Spunky & Fun", "Intellectual Business")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Sales Copywriter", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        Text("Automates a professional, print-ready Amazon blurb with bold hooks and highlight benefits.", style = MaterialTheme.typography.bodySmall)

        OutlinedTextField(
            value = titleInput,
            onValueChange = { titleInput = it },
            label = { Text("Book Title") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = genreInput,
            onValueChange = { genreInput = it },
            label = { Text("Genre") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = protagonistInput,
            onValueChange = { protagonistInput = it },
            label = { Text("Protagonist or Core Audience") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = conflictInput,
            onValueChange = { conflictInput = it },
            label = { Text("Main Conflict, Journey, or Solution") },
            modifier = Modifier.fillMaxWidth().height(70.dp),
            maxLines = 3
        )

        Text("Select Ad-Copy Tone", style = MaterialTheme.typography.labelSmall)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            LazyColumn(modifier = Modifier.height(60.dp)) {
                items(tones.size) { i ->
                    val t = tones[i]
                    FilterChip(
                        selected = selectedTone == t,
                        onClick = { selectedTone = t },
                        label = { Text(t, fontSize = 10.sp) },
                        modifier = Modifier.padding(2.dp)
                    )
                }
            }
        }

        Button(
            onClick = {
                if (titleInput.isNotBlank()) {
                    viewModel.generateKdpBlurb(
                        bookTitle = titleInput,
                        protagonist = protagonistInput,
                        targetGenre = genreInput,
                        keyConflict = conflictInput,
                        selectionTone = selectedTone
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Icon(Icons.Default.Translate, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Generate Amazon Decription")
        }
    }
}

@Composable
fun CoverConceptForm(viewModel: KdpViewModel) {
    var titleInput by remember { mutableStateOf("") }
    var genreInput by remember { mutableStateOf("Space Opera") }
    var briefInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("Art Illustrator Coprocessor", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        Text("Suggests hex codes, geometric spacing setups, print bindings, and AI illustration prompts.", style = MaterialTheme.typography.bodySmall)

        OutlinedTextField(
            value = titleInput,
            onValueChange = { titleInput = it },
            label = { Text("Book Title") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = genreInput,
            onValueChange = { genreInput = it },
            label = { Text("Book Genre") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = briefInput,
            onValueChange = { briefInput = it },
            label = { Text("Briefly describe cover ideas (e.g. key object, dark mode)") },
            modifier = Modifier.fillMaxWidth().height(80.dp),
            maxLines = 3
        )

        Button(
            onClick = {
                if (titleInput.isNotBlank()) {
                    viewModel.brainstormCoverConcept(titleInput, genreInput, briefInput)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Icon(Icons.Default.Palette, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Brainstorm Visual Conception")
        }
    }
}

@Composable
fun ManuscriptPacingAuditForm(viewModel: KdpViewModel) {
    var chTitle by remember { mutableStateOf("Chapter One: The Awakening") }
    var txtAudit by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("Pacing & Typographical Auditor", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        Text("Paste draft text. Gemini evaluates sentence lengths, drop-cap placement, binding-crease safety, and pacing.", style = MaterialTheme.typography.bodySmall)

        OutlinedTextField(
            value = chTitle,
            onValueChange = { chTitle = it },
            label = { Text("Chapter Heading") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = txtAudit,
            onValueChange = { txtAudit = it },
            label = { Text("Paste chapter text sample...") },
            modifier = Modifier.fillMaxWidth().height(115.dp),
            maxLines = 5
        )

        Button(
            onClick = {
                if (txtAudit.isNotBlank()) {
                    viewModel.auditChapterFormatting(chTitle, txtAudit)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Icon(Icons.Default.Spellcheck, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Audit Formatting & Pacing")
        }
    }
}
