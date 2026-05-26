package tech.soc.soar.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun SoarStyledTitle(
    modifier: Modifier = Modifier,
    animated: Boolean = true
) {
    val words = remember {
        listOf(
            SoarTitleWord(
                highlightedLetter = "S",
                rest = "ecurity"
            ),
            SoarTitleWord(
                highlightedLetter = "O",
                rest = "rchestration"
            ),
            SoarTitleWord(
                highlightedLetter = "A",
                rest = "utomation"
            ),
            SoarTitleWord(
                highlightedLetter = "R",
                rest = "esponse"
            )
        )
    }

    val visibleSquares = remember {
        mutableStateListOf(false, false, false, false)
    }

    val typedCharacterCounts = remember {
        mutableStateListOf(0, 0, 0, 0)
    }

    LaunchedEffect(animated) {
        if (!animated) {
            words.forEachIndexed { index, word ->
                visibleSquares[index] = true
                typedCharacterCounts[index] = word.rest.length
            }

            return@LaunchedEffect
        }

        words.indices.forEach { index ->
            visibleSquares[index] = false
            typedCharacterCounts[index] = 0
        }

        delay(180)

        words.forEachIndexed { index, word ->
            visibleSquares[index] = true

            delay(180)

            for (count in 1..word.rest.length) {
                typedCharacterCounts[index] = count
                delay(TYPING_DELAY_MS)
            }

            delay(ROW_DELAY_MS)
        }
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(6.dp),
        horizontalAlignment = Alignment.Start
    ) {
        words.forEachIndexed { index, word ->
            StyledWord(
                highlightedLetter = word.highlightedLetter,
                rest = word.rest.take(typedCharacterCounts[index]),
                squareVisible = visibleSquares[index]
            )
        }
    }
}

@Composable
private fun StyledWord(
    highlightedLetter: String,
    rest: String,
    squareVisible: Boolean
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        AnimatedVisibility(
            visible = squareVisible,
            enter = fadeIn() + scaleIn(
                initialScale = 0.72f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMediumLow
                )
            )
        ) {
            Box(
                modifier = Modifier
                    .shadow(
                        elevation = 4.dp,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = highlightedLetter,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Spacer(modifier = Modifier.width(6.dp))

        Text(
            text = rest,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontSize = 30.sp,
                fontWeight = FontWeight.SemiBold
            ),
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

private data class SoarTitleWord(
    val highlightedLetter: String,
    val rest: String
)

private const val TYPING_DELAY_MS = 35L
private const val ROW_DELAY_MS = 120L