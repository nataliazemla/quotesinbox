@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.quotesinbox.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun App() {
    var architecture by rememberSaveable { mutableStateOf(Architecture.MVVM) }

    MaterialTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("MVVM vs MVI - Quotes") },
                    actions = {
                        ArchitectureToggle(
                            architecture = architecture,
                            onArchitectureChange = { architecture = it }
                        )
                    }
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier
                .padding(padding)
                .fillMaxSize()
            ) {
                Text(
                    text = "Architecture: $architecture",
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

@Composable
private fun ArchitectureToggle(
    architecture: Architecture,
    onArchitectureChange: (Architecture) -> Unit
) {
    Row(
        modifier = Modifier.padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AssistChip(
            onClick = { onArchitectureChange(Architecture.MVVM) },
            label = { Text("MVVM") },
            colors = AssistChipDefaults.assistChipColors()
        )
        AssistChip(
            onClick = { onArchitectureChange(Architecture.MVI) },
            label = { Text("MVI") },
            colors = AssistChipDefaults.assistChipColors()
        )
    }
}