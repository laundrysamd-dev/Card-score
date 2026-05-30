package com.example.ui.screens

import android.content.Intent
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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
fun CardGameScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activeGame by viewModel.activeCardGame.collectAsState()
    val rounds by viewModel.activeGameRounds.collectAsState()

    // Dialog state for score editing
    var editingRoundNum by remember { mutableStateOf<Int?>(null) }
    var editingTeam1Score by remember { mutableStateOf("") }
    var editingTeam2Score by remember { mutableStateOf("") }

    // Dialog state for team/player renaming
    var isRenamingActive by remember { mutableStateOf(false) }
    var team1NameInput by remember { mutableStateOf("") }
    var team1P1Input by remember { mutableStateOf("") }
    var team1P2Input by remember { mutableStateOf("") }
    var team2NameInput by remember { mutableStateOf("") }
    var team2P1Input by remember { mutableStateOf("") }
    var team2P2Input by remember { mutableStateOf("") }

    // Dialog state for starting a new card game confirmation
    var showNewGameConfirm by remember { mutableStateOf(false) }

    // Compute totals
    val t1Total = rounds.sumOf { it.team1Score }
    val t2Total = rounds.sumOf { it.team2Score }

    val winnerText = when {
        t1Total > t2Total -> "${activeGame?.team1Name ?: "Team 1"} Wins! 🏆"
        t2Total > t1Total -> "${activeGame?.team2Name ?: "Team 2"} Wins! 🏆"
        rounds.isEmpty() -> "Get ready to play!"
        else -> "It's a Tie! 🤝"
    }

    val leadingText = when {
        t1Total > t2Total -> "${activeGame?.team1Name ?: "Team A"} is leading by ${t1Total - t2Total}!"
        t2Total > t1Total -> "${activeGame?.team2Name ?: "Team B"} is leading by ${t2Total - t1Total}!"
        rounds.isEmpty() -> "No scores entered yet"
        else -> "Scores are currently even!"
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Card Game Scorecard",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        val sdf = SimpleDateFormat("MMM d, yyyy - h:mm a", Locale.getDefault())
                        Text(
                            text = sdf.format(Date(activeGame?.dateMillis ?: System.currentTimeMillis())),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateTo(AppScreen.Home) },
                        modifier = Modifier.testTag("back_button")
                    ) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back home")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.resetCurrentScores() },
                        modifier = Modifier.testTag("reset_game_button")
                    ) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "Reset round scores to 0")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                )
            )
        },
        modifier = modifier
    ) { innerPadding ->
        activeGame?.let { game ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                // Interactive dynamic scoreboard header with leading player
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = leadingText,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }

                        // Edit team details pencil button
                        Button(
                            onClick = {
                                team1NameInput = game.team1Name
                                team1P1Input = game.team1Player1
                                team1P2Input = game.team1Player2
                                team2NameInput = game.team2Name
                                team2P1Input = game.team2Player1
                                team2P2Input = game.team2Player2
                                isRenamingActive = true
                            },
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary,
                                contentColor = MaterialTheme.colorScheme.onSecondary
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .height(32.dp)
                                .testTag("rename_players_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Edit Players", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Layout consisting of two team cards with details side by side
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Team 1 Card
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .testTag("team1_card"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = game.team1Name,
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = MaterialTheme.colorScheme.primary,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${game.team1Player1} • ${game.team1Player2}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    // Team 2 Card
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .testTag("team2_card"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f))
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = game.team2Name,
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = MaterialTheme.colorScheme.tertiary,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${game.team2Player1} • ${game.team2Player2}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                // Table Header row representing Round and scores aligned with the side-by-side Vibrant Columns
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = game.team1Name.uppercase(),
                        fontWeight = FontWeight.Black,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "ROUND",
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.width(52.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = game.team2Name.uppercase(),
                        fontWeight = FontWeight.Black,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.tertiary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // List of rounds (scoresheet look)
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp),
                    contentPadding = PaddingValues(top = 4.dp, bottom = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(rounds, key = { it.roundNumber }) { round ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .clickable {
                                    editingRoundNum = round.roundNumber
                                    editingTeam1Score = round.team1Score.toString()
                                    editingTeam2Score = round.team2Score.toString()
                                },
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Left Cell: Team A Score in PrimaryContainer (violet)
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .background(
                                        MaterialTheme.colorScheme.primaryContainer,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .border(
                                        1.dp,
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(12.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = round.team1Score.toString(),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 18.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }

                            // Center Cell: Round Indicator
                            Box(
                                modifier = Modifier
                                    .width(52.dp)
                                    .fillMaxHeight()
                                    .background(
                                        MaterialTheme.colorScheme.surfaceVariant,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .border(
                                        1.dp,
                                        MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(12.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "R${round.roundNumber}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            // Right Cell: Team B Score in TertiaryContainer (blue)
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .background(
                                        MaterialTheme.colorScheme.tertiaryContainer,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .border(
                                        1.dp,
                                        MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(12.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = round.team2Score.toString(),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 18.sp,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                            }
                        }
                    }

                    item {
                        // Quick Add Round dynamic button at bottom of list
                        Button(
                            onClick = { viewModel.addMoreRounds() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp)
                                .testTag("add_round_button"),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Add Round (Round ${rounds.size + 1})", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Sticky grand total summary footer card at bottom (Vibrant Paper Sheets style)
                Surface(
                    tonalElevation = 6.dp,
                    shadowElevation = 8.dp,
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        // Winner / Status display bar
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = winnerText,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        // Combined Total Score values configured as solid design header blocks with overlapping banner support
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Team 1 Bold Total Block
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(100.dp)
                                    .background(
                                        MaterialTheme.colorScheme.primary,
                                        shape = RoundedCornerShape(18.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.padding(6.dp)
                                ) {
                                    Text(
                                        text = "TOTAL",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                                        letterSpacing = 1.sp
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = t1Total.toString(),
                                        fontWeight = FontWeight.Black,
                                        fontSize = 32.sp,
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                    Text(
                                        text = game.team1Name.uppercase(),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                if (t1Total > t2Total) {
                                    // Yellow "🏆 Leading" badge overlay
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.TopCenter)
                                            .offset(y = (-10).dp)
                                            .background(Color(0xFFFFD600), shape = RoundedCornerShape(6.dp))
                                            .border(0.5.dp, Color.Black.copy(alpha = 0.15f), shape = RoundedCornerShape(6.dp))
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text("🏆", fontSize = 9.sp)
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Text(
                                                text = "LEADING",
                                                fontWeight = FontWeight.Black,
                                                fontSize = 9.sp,
                                                color = Color.Black
                                            )
                                        }
                                    }
                                }
                            }

                            // Team 2 Bold Total Block
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(100.dp)
                                    .background(
                                        MaterialTheme.colorScheme.tertiary,
                                        shape = RoundedCornerShape(18.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.padding(6.dp)
                                ) {
                                    Text(
                                        text = "TOTAL",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.7f),
                                        letterSpacing = 1.sp
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = t2Total.toString(),
                                        fontWeight = FontWeight.Black,
                                        fontSize = 32.sp,
                                        color = MaterialTheme.colorScheme.onTertiary
                                    )
                                    Text(
                                        text = game.team2Name.uppercase(),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onTertiary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                if (t2Total > t1Total) {
                                    // Yellow "🏆 Leading" badge overlay
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.TopCenter)
                                            .offset(y = (-10).dp)
                                            .background(Color(0xFFFFD600), shape = RoundedCornerShape(6.dp))
                                            .border(0.5.dp, Color.Black.copy(alpha = 0.15f), shape = RoundedCornerShape(6.dp))
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text("🏆", fontSize = 9.sp)
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Text(
                                                text = "LEADING",
                                                fontWeight = FontWeight.Black,
                                                fontSize = 9.sp,
                                                color = Color.Black
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Controls: Save game flow and direct sharing
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // New Game Trigger
                            OutlinedButton(
                                onClick = { showNewGameConfirm = true },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("init_new_game"),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("New Game")
                            }

                            // Share scorecard
                            IconButton(
                                onClick = {
                                    val shareText = buildString {
                                        appendLine("🏆 GAME NIGHT SCOREBOARD 🏆")
                                        appendLine("Card Game: ${game.team1Name} vs ${game.team2Name}")
                                        appendLine("-----------------------------")
                                        rounds.forEach { r ->
                                            appendLine("Round ${r.roundNumber}:  ${r.team1Score}  |  ${r.team2Score}")
                                        }
                                        appendLine("-----------------------------")
                                        appendLine("GRAND TOTALS:  $t1Total  |  $t2Total")
                                        appendLine("Winner highlight: $winnerText")
                                        appendLine("\nShared from family Game Night Score App! 🎲")
                                    }
                                    val sendIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_TEXT, shareText)
                                        type = "text/plain"
                                    }
                                    val shareIntent = Intent.createChooser(sendIntent, "Share Scores")
                                    context.startActivity(shareIntent)
                                },
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(14.dp))
                                    .size(48.dp)
                                    .testTag("share_scores_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Share match outcomes"
                                )
                            }

                            // Complete/Save button
                            Button(
                                onClick = { viewModel.finishAndSaveGame() },
                                modifier = Modifier
                                    .weight(1.5f)
                                    .height(48.dp)
                                    .testTag("save_history_button"),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Save Game", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        } ?: run {
            // Null state fallback (initialize fresh)
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }

    // --- QUICK EASY SCORE ENTRY KEYPAD DIALOG ---
    editingRoundNum?.let { roundNum ->
        AlertDialog(
            onDismissRequest = { editingRoundNum = null },
            title = {
                Text(
                    text = "Enter Scores • Round $roundNum",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                val team1Name = activeGame?.team1Name ?: "Team 1"
                val team2Name = activeGame?.team2Name ?: "Team 2"

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Team 1 Score entry block
                    Column {
                        Text(
                            text = team1Name,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        OutlinedTextField(
                            value = editingTeam1Score,
                            onValueChange = { editingTeam1Score = it },
                            placeholder = { Text("0") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, fontSize = 20.sp),
                            modifier = Modifier.fillMaxWidth().testTag("t1_score_input")
                        )
                        // Quick increment buttons (+5, +10, +50)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(top = 6.dp)
                        ) {
                            ButtonDefaults.filledTonalButtonColors()
                            listOf(5, 10, 50).forEach { amt ->
                                FilledTonalButton(
                                    onClick = {
                                        val currentVal = editingTeam1Score.toIntOrNull() ?: 0
                                        editingTeam1Score = (currentVal + amt).toString()
                                    },
                                    modifier = Modifier.weight(1f).height(36.dp),
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text("+$amt", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            FilledTonalButton(
                                onClick = { editingTeam1Score = "" },
                                modifier = Modifier.weight(0.8f).height(36.dp),
                                contentPadding = PaddingValues(0.dp),
                                colors = ButtonDefaults.filledTonalButtonColors(containerColor = MaterialTheme.colorScheme.errorContainer, contentColor = MaterialTheme.colorScheme.onErrorContainer)
                            ) {
                                Text("Clear", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))

                    // Team 2 Score entry block
                    Column {
                        Text(
                            text = team2Name,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        OutlinedTextField(
                            value = editingTeam2Score,
                            onValueChange = { editingTeam2Score = it },
                            placeholder = { Text("0") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, fontSize = 20.sp),
                            modifier = Modifier.fillMaxWidth().testTag("t2_score_input")
                        )
                        // Quick increment buttons (+5, +10, +50)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(top = 6.dp)
                        ) {
                            listOf(5, 10, 50).forEach { amt ->
                                FilledTonalButton(
                                    onClick = {
                                        val currentVal = editingTeam2Score.toIntOrNull() ?: 0
                                        editingTeam2Score = (currentVal + amt).toString()
                                    },
                                    modifier = Modifier.weight(1f).height(36.dp),
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text("+$amt", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            FilledTonalButton(
                                onClick = { editingTeam2Score = "" },
                                modifier = Modifier.weight(0.8f).height(36.dp),
                                contentPadding = PaddingValues(0.dp),
                                colors = ButtonDefaults.filledTonalButtonColors(containerColor = MaterialTheme.colorScheme.errorContainer, contentColor = MaterialTheme.colorScheme.onErrorContainer)
                            ) {
                                Text("Clear", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val s1 = editingTeam1Score.toIntOrNull() ?: 0
                        val s2 = editingTeam2Score.toIntOrNull() ?: 0
                        viewModel.updateRoundScore(roundNum, s1, s2)
                        editingRoundNum = null
                    },
                    modifier = Modifier.testTag("confirm_round_scores")
                ) {
                    Text("OK", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { editingRoundNum = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // --- RENAME TEAMS & PLAYERS DIALOG ---
    if (isRenamingActive) {
        AlertDialog(
            onDismissRequest = { isRenamingActive = false },
            title = {
                Text(
                    text = "Edit Teams & Players",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Team 1 Section
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Team A (Primary Lineup)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        OutlinedTextField(
                            value = team1NameInput,
                            onValueChange = { team1NameInput = it },
                            label = { Text("Team A Name") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("rename_team1_input")
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = team1P1Input,
                                onValueChange = { team1P1Input = it },
                                label = { Text("Player 1") },
                                singleLine = true,
                                modifier = Modifier.weight(1f).testTag("t1_p1_input")
                            )
                            OutlinedTextField(
                                value = team1P2Input,
                                onValueChange = { team1P2Input = it },
                                label = { Text("Player 2") },
                                singleLine = true,
                                modifier = Modifier.weight(1f).testTag("t1_p2_input")
                            )
                        }
                    }

                    Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))

                    // Team 2 Section
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Team B (Challenger Lineup)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                        OutlinedTextField(
                            value = team2NameInput,
                            onValueChange = { team2NameInput = it },
                            label = { Text("Team B Name") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("rename_team2_input")
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = team2P1Input,
                                onValueChange = { team2P1Input = it },
                                label = { Text("Player 1") },
                                singleLine = true,
                                modifier = Modifier.weight(1f).testTag("t2_p1_input")
                            )
                            OutlinedTextField(
                                value = team2P2Input,
                                onValueChange = { team2P2Input = it },
                                label = { Text("Player 2") },
                                singleLine = true,
                                modifier = Modifier.weight(1f).testTag("t2_p2_input")
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val t1N = team1NameInput.ifBlank { "Team A" }
                        val t1P1 = team1P1Input.ifBlank { "Player 1" }
                        val t1P2 = team1P2Input.ifBlank { "Player 2" }
                        val t2N = team2NameInput.ifBlank { "Team B" }
                        val t2P1 = team2P1Input.ifBlank { "Player 3" }
                        val t2P2 = team2P2Input.ifBlank { "Player 4" }

                        viewModel.renameTeamsAndPlayers(
                            team1Name = t1N, team1P1 = t1P1, team1P2 = t1P2,
                            team2Name = t2N, team2P1 = t2P1, team2P2 = t2P2
                        )
                        isRenamingActive = false
                    },
                    modifier = Modifier.testTag("save_renames_button")
                ) {
                    Text("Apply & Save", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { isRenamingActive = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // --- CONFIRM STARTING FRESH CARD GAME ---
    if (showNewGameConfirm) {
        var teamAVal by remember { mutableStateOf("Team A") }
        var teamAp1 by remember { mutableStateOf("Player 1") }
        var teamAp2 by remember { mutableStateOf("Player 2") }
        var teamBVal by remember { mutableStateOf("Team B") }
        var teamBp1 by remember { mutableStateOf("Player 3") }
        var teamBp2 by remember { mutableStateOf("Player 4") }

        AlertDialog(
            onDismissRequest = { showNewGameConfirm = false },
            title = { Text("Start Brand New Game?", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "This will archive current round scores to history and start a fresh scorecard setup.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = teamAVal,
                        onValueChange = { teamAVal = it },
                        label = { Text("Team A Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = teamAp1,
                            onValueChange = { teamAp1 = it },
                            label = { Text("P1 Name") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = teamAp2,
                            onValueChange = { teamAp2 = it },
                            label = { Text("P2 Name") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = teamBVal,
                        onValueChange = { teamBVal = it },
                        label = { Text("Team B Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = teamBp1,
                            onValueChange = { teamBp1 = it },
                            label = { Text("P1 Name") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = teamBp2,
                            onValueChange = { teamBp2 = it },
                            label = { Text("P2 Name") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.startNewCardGame(
                            team1Name = teamAVal.ifBlank { "Team A" },
                            team1P1 = teamAp1.ifBlank { "Player 1" },
                            team1P2 = teamAp2.ifBlank { "Player 2" },
                            team2Name = teamBVal.ifBlank { "Team B" },
                            team2P1 = teamBp1.ifBlank { "Player 3" },
                            team2P2 = teamBp2.ifBlank { "Player 4" }
                        )
                        showNewGameConfirm = false
                    },
                    modifier = Modifier.testTag("submit_new_game_confirm")
                ) {
                    Text("Start Game", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showNewGameConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
