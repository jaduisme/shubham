package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.data.KdpDatabase
import com.example.data.KdpRepository
import com.example.ui.KdpViewModel
import com.example.ui.KdpViewModelFactory
import com.example.ui.screens.AiPortalScreen
import com.example.ui.screens.CoverDesignerScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.ManuscriptFormatterScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 1. Initialize local SQLite Database & Repository Pattern
        val database = KdpDatabase.getDatabase(applicationContext)
        val repository = KdpRepository(database.kdpDao())
        
        // 2. Instantiate Main ViewModel using factory (No massive DI complexity required)
        val viewModel: KdpViewModel by viewModels {
            KdpViewModelFactory(repository)
        }

        enableEdgeToEdge()
        
        setContent {
            MyApplicationTheme {
                var currentRoute by remember { mutableStateOf("dashboard") }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        NavigationBar(
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.primary
                        ) {
                            // Dashboard Tab
                            NavigationBarItem(
                                selected = currentRoute == "dashboard",
                                onClick = { currentRoute = "dashboard" },
                                label = { Text("Hub", style = MaterialTheme.typography.labelSmall) },
                                icon = {
                                    Icon(
                                        imageVector = if (currentRoute == "dashboard") Icons.Filled.Home else Icons.Outlined.Home,
                                        contentDescription = "Hub Dashboard"
                                    )
                                }
                            )

                            // Cover Studio Tab
                            NavigationBarItem(
                                selected = currentRoute == "covers",
                                onClick = { currentRoute = "covers" },
                                label = { Text("Covers", style = MaterialTheme.typography.labelSmall) },
                                icon = {
                                    Icon(
                                        imageVector = if (currentRoute == "covers") Icons.Filled.Palette else Icons.Outlined.Palette,
                                        contentDescription = "Cover Designer"
                                    )
                                }
                            )

                            // Manuscripts Tab
                            NavigationBarItem(
                                selected = currentRoute == "manuscripts",
                                onClick = { currentRoute = "manuscripts" },
                                label = { Text("Books", style = MaterialTheme.typography.labelSmall) },
                                icon = {
                                    Icon(
                                        imageVector = if (currentRoute == "manuscripts") Icons.Filled.Book else Icons.Outlined.Book,
                                        contentDescription = "Manuscripts Builder"
                                    )
                                }
                            )

                            // Gemini AI assistant Tab
                            NavigationBarItem(
                                selected = currentRoute == "ai",
                                onClick = { currentRoute = "ai" },
                                label = { Text("Gemini", style = MaterialTheme.typography.labelSmall) },
                                icon = {
                                    Icon(
                                        imageVector = if (currentRoute == "ai") Icons.Filled.AutoAwesome else Icons.Outlined.AutoAwesome,
                                        contentDescription = "Gemini AI Coprocessor"
                                    )
                                }
                            )
                        }
                    }
                ) { innerPadding ->
                    // Viewport Switcher Router
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        when (currentRoute) {
                            "dashboard" -> {
                                DashboardScreen(
                                    viewModel = viewModel,
                                    onNavigateToCovers = { currentRoute = "covers" },
                                    onNavigateToManuscripts = { currentRoute = "manuscripts" },
                                    onNavigateToAi = { currentRoute = "ai" }
                                )
                            }
                            "covers" -> {
                                CoverDesignerScreen(viewModel = viewModel)
                            }
                            "manuscripts" -> {
                                ManuscriptFormatterScreen(viewModel = viewModel)
                            }
                            "ai" -> {
                                AiPortalScreen(viewModel = viewModel)
                            }
                        }
                    }
                }
            }
        }
    }
}
