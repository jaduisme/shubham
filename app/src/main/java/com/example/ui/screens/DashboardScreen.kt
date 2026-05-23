package com.example.ui.screens

import androidx.compose.animation.*
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BookCover
import com.example.data.Manuscript
import com.example.ui.KdpViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: KdpViewModel,
    onNavigateToCovers: () -> Unit,
    onNavigateToManuscripts: () -> Unit,
    onNavigateToAi: () -> Unit,
    modifier: Modifier = Modifier
) {
    val covers by viewModel.allCovers.collectAsState()
    val manuscripts by viewModel.allManuscripts.collectAsState()

    // Aggregate statistics
    val totalCovers = covers.size
    val totalManuscripts = manuscripts.size

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("KDP Book Builder", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Publishing Hero Banner Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primaryContainer,
                                        MaterialTheme.colorScheme.surfaceVariant
                                    )
                                )
                            )
                            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                            .padding(20.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                "Publishing Studio Command Central",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                "Format and design covers tailored perfectly to Amazon KDP template specifications. Utilize Gemini AI to explore niches, generate blurbs, or audit pacing.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.85f),
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            // High-Level Stat Counters Grid
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Covers Count
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable(onClick = onNavigateToCovers),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Covers", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                                Icon(Icons.Default.Palette, contentDescription = null, size = 16)
                            }
                            Text("$totalCovers", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                            Text("Click to design", fontSize = 10.sp, color = MaterialTheme.colorScheme.primary)
                        }
                    }

                    // Manuscripts Count
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable(onClick = onNavigateToManuscripts),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Manuscripts", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                                Icon(Icons.Default.Book, contentDescription = null, size = 16)
                            }
                            Text("$totalManuscripts", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                            Text("Click to format", fontSize = 10.sp, color = MaterialTheme.colorScheme.secondary)
                        }
                    }
                }
            }

            // Quick Shortcut Actions list
            item {
                Text("Coprocessor Tools", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onNavigateToAi,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("KDP Gemini AI", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = onNavigateToCovers,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.AspectRatio, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Spine Planner", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Recent Covers Horizontal display with dynamic drawings!
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Saved Front Covers", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    if (totalCovers > 0) {
                        Text(
                            "See All",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.clickable(onClick = onNavigateToCovers)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))

                if (covers.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("No cover projects yet.", style = MaterialTheme.typography.bodySmall)
                            TextButton(onClick = onNavigateToCovers) {
                                Text("New Design", fontSize = 10.sp)
                            }
                        }
                    }
                } else {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(covers.size) { index ->
                            val cov = covers[index]
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .width(85.dp)
                                    .clickable {
                                        viewModel.selectCover(cov.id)
                                        onNavigateToCovers()
                                    }
                            ) {
                                // Draw actual miniature canvas representing front cover
                                Box(
                                    modifier = Modifier
                                        .size(width = 80.dp, height = 110.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(android.graphics.Color.parseColor(cov.backgroundColorHex)))
                                        .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                                        .padding(4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        Text(
                                            cov.title.uppercase(),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(android.graphics.Color.parseColor(cov.textColorHex)),
                                            textAlign = TextAlign.Center,
                                            maxLines = 3,
                                            overflow = TextOverflow.Ellipsis
                                        )

                                        Box(
                                            modifier = Modifier
                                                .size(10.dp)
                                                .background(Color(android.graphics.Color.parseColor(cov.accentColorHex)))
                                        )

                                        Text(
                                            cov.author,
                                            fontSize = 7.sp,
                                            color = Color(android.graphics.Color.parseColor(cov.textColorHex)).copy(alpha = 0.8f),
                                            textAlign = TextAlign.Center,
                                            maxLines = 1
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    cov.title,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }

            // Recent books/manuscripts list
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Saved Manuscripts", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    if (totalManuscripts > 0) {
                        Text(
                            "See All",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.clickable(onClick = onNavigateToManuscripts)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))

                if (manuscripts.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("No manuscript projects yet.", style = MaterialTheme.typography.bodySmall)
                            TextButton(onClick = onNavigateToManuscripts) {
                                Text("New Manuscript", fontSize = 10.sp)
                            }
                        }
                    }
                } else {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        manuscripts.take(3).forEach { m ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.selectManuscript(m.id)
                                        onNavigateToManuscripts()
                                    },
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(m.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                        Text(
                                            "Trim: ${m.trimWidthInches}\"x${m.trimHeightInches}\" • Font: ${m.fontName}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                        )
                                    }
                                    Icon(
                                        Icons.Default.ArrowForwardIos,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

// Simple Icon Sizing Composable helper
@Composable
fun Icon(imageVector: androidx.compose.ui.graphics.vector.ImageVector, contentDescription: String?, size: Int) {
    Icon(
        imageVector = imageVector,
        contentDescription = contentDescription,
        tint = LocalContentColor.current,
        modifier = Modifier.size(size.dp)
    )
}
