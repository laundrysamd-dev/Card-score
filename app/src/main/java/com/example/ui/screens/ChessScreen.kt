package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AppScreen
import com.example.ui.GameViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChessScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val chessHistory by viewModel.chessHistory.collectAsState()

    // Screen states
    var player1Input by remember { mutableStateOf("Grandpa") }
    var player2Input by remember { mutableStateOf("Dad") }
    var isMatchActive by remember { mutableStateOf(false) }

    // Dynamic stats computation of all Chess history
    val totalMatches = chessHistory.size
    val p1WinsGlobal = chessHistory.count { it.outcome == 1 }
    val p2WinsGlobal = chessHistory.count { it.outcome == 2 }
    val drawsGlobal = chessHistory.count { it.outcome == 0 }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = "Chess Match Tracker", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateTo(AppScreen.Home) },
                        modifier = Modifier.testTag("back_button_chess")
                    ) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back home")
                    }
                },
                actions = {
                    if (chessHistory.isNotEmpty()) {
                        IconButton(
                            onClick = { viewModel.clearAllChessMatches() },
                            modifier = Modifier.testTag("clear_chess_history")
                        ) {
                            Icon(imageVector = Icons.Default.DeleteSweep, contentDescription = "Clear all chess records")
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
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
            // Configuration / Controller card
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = if (isMatchActive) "⚔️ Match In Progress" else "♟️ Configure Match players",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        color = if (isMatchActive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                    )

                    // Players config text fields
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = player1Input,
                            onValueChange = { if (!isMatchActive) player1Input = it },
                            label = { Text("White (Player 1)") },
                            singleLine = true,
                            enabled = !isMatchActive,
                            leadingIcon = { Icon(Icons.Default.Person, null) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("chess_p1_input")
                        )

                        OutlinedTextField(
                            value = player2Input,
                            onValueChange = { if (!isMatchActive) player2Input = it },
                            label = { Text("Black (Player 2)") },
                            singleLine = true,
                            enabled = !isMatchActive,
                            leadingIcon = { Icon(Icons.Default.Person, null) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("chess_p2_input")
                        )
                    }

                    // Start/Stop controller button
                    AnimatedContent(targetState = isMatchActive, label = "MatchButtonAnim") { active ->
                        if (!active) {
                            Button(
                                onClick = { isMatchActive = true },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("start_chess_match_btn"),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Start Chess Match", fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Select Match Outcome:",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    // Player 1 Wins
                                    Button(
                                        onClick = {
                                            viewModel.saveChessMatch(player1Input, player2Input, 1)
                                            isMatchActive = false
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer, contentColor = MaterialTheme.colorScheme.onPrimaryContainer),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(44.dp)
                                            .testTag("p1_wins_btn")
                                    ) {
                                        Text("${player1Input.take(8)} Win 👑", fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    }

                                    // Tie / Draw
                                    Button(
                                        onClick = {
                                            viewModel.saveChessMatch(player1Input, player2Input, 0)
                                            isMatchActive = false
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer, contentColor = MaterialTheme.colorScheme.onSecondaryContainer),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier
                                            .weight(0.8f)
                                            .height(44.dp)
                                            .testTag("chess_draw_btn")
                                    ) {
                                        Text("Draw 🤝", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }

                                    // Player 2 Wins
                                    Button(
                                        onClick = {
                                            viewModel.saveChessMatch(player1Input, player2Input, 2)
                                            isMatchActive = false
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer, contentColor = MaterialTheme.colorScheme.onTertiaryContainer),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(44.dp)
                                            .testTag("p2_wins_btn")
                                    ) {
                                        Text("${player2Input.take(8)} Win 👑", fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    }
                                }

                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                                    TextButton(onClick = { isMatchActive = false }) {
                                        Icon(Icons.Default.Close, null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Abort Match", color = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Global Statistics stats banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatWidget(label = "Total Matches", value = totalMatches.toString(), color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
                StatWidget(label = "P1 Wins", value = p1WinsGlobal.toString(), color = MaterialTheme.colorScheme.primary, modifier = Modifier.weight(1f))
                StatWidget(label = "Draws", value = drawsGlobal.toString(), color = MaterialTheme.colorScheme.secondary, modifier = Modifier.weight(1f))
                StatWidget(label = "P2 Wins", value = p2WinsGlobal.toString(), color = MaterialTheme.colorScheme.tertiary, modifier = Modifier.weight(1f))
            }

            Text(
                text = "Match History list",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 20.dp, top = 20.dp, bottom = 8.dp)
            )

            // Matches historical Log
            if (chessHistory.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Casino,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                            modifier = Modifier.size(72.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "No chess matches saved yet.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(chessHistory) { match ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(16.dp))
                                .border(
                                    0.5.dp,
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    val formattedTime = SimpleDateFormat("h:mm a, d MMM", Locale.getDefault()).format(Date(match.dateMillis))
                                    Text(
                                        text = formattedTime,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Player 1 details
                                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1.2f)) {
                                        Text(
                                            text = match.player1Name,
                                            fontWeight = if (match.outcome == 1) FontWeight.Black else FontWeight.SemiBold,
                                            fontSize = 14.sp,
                                            color = if (match.outcome == 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        if (match.outcome == 1) {
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Icon(Icons.Default.EmojiEvents, "Winner", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(14.dp))
                                        }
                                    }

                                    // Outcome banner
                                    Text(
                                        text = "VS",
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(horizontal = 6.dp)
                                    )

                                    // Player 2 details
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.End,
                                        modifier = Modifier.weight(1.2f)
                                    ) {
                                        if (match.outcome == 2) {
                                            Icon(Icons.Default.EmojiEvents, "Winner", tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                        }
                                        Text(
                                            text = match.player2Name,
                                            fontWeight = if (match.outcome == 2) FontWeight.Black else FontWeight.SemiBold,
                                            fontSize = 14.sp,
                                            color = if (match.outcome == 2) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurface,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            textAlign = TextAlign.End
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatWidget(
    label: String,
    value: String,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier.border(0.5.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f), shape = RoundedCornerShape(16.dp))
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = color
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }
    }
}
