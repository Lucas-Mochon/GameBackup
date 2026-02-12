package fr.sdv.gamebacklog.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.sdv.gamebacklog.data.model.GameStatus
import fr.sdv.gamebacklog.viewmodel.StatisticsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    viewModel: StatisticsViewModel,
    onNavigateBack: () -> Unit,
    fontScaleFactor: Float = 1f
) {
    val todoCount by viewModel.todoCount.collectAsState(initial = 0)
    val inProgressCount by viewModel.inProgressCount.collectAsState(initial = 0)
    val doneCount by viewModel.doneCount.collectAsState(initial = 0)

    val total = todoCount + inProgressCount + doneCount

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Statistiques",
                        fontSize = (18.sp * fontScaleFactor)
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.semantics {
                            contentDescription = "Retour"
                        }
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Total de jeux: $total",
                fontSize = (18.sp * fontScaleFactor),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Statistics cards
            StatisticCard(
                title = GameStatus.TO_DO.getLabel(),
                count = todoCount,
                total = total,
                fontScaleFactor = fontScaleFactor
            )

            StatisticCard(
                title = GameStatus.IN_PROGRESS.getLabel(),
                count = inProgressCount,
                total = total,
                fontScaleFactor = fontScaleFactor
            )

            StatisticCard(
                title = GameStatus.DONE.getLabel(),
                count = doneCount,
                total = total,
                fontScaleFactor = fontScaleFactor
            )
        }
    }
}

@Composable
fun StatisticCard(
    title: String,
    count: Int,
    total: Int,
    fontScaleFactor: Float = 1f
) {
    val percentage = if (total > 0) (count * 100) / total else 0

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
            .semantics {
                contentDescription = "$title: $count jeux ($percentage%)"
            },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                fontSize = (16.sp * fontScaleFactor),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Nombre: $count",
                    fontSize = (14.sp * fontScaleFactor)
                )
                Text(
                    text = "$percentage%",
                    fontSize = (14.sp * fontScaleFactor),
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Simple bar visualization
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp)
                    .padding(top = 8.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(4.dp)
                    )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(percentage / 100f)
                        .height(20.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(4.dp)
                        )
                )
            }
        }
    }
}

