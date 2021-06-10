package net.accelf.itc_lms_unofficial.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import net.accelf.itc_lms_unofficial.R

@Composable
@Preview
fun PreviewExpandableCard() {
    Column {
        ExpandableCard(
            title = "Card Title",
            modifier = Modifier
                .padding(Values.Spacing.around)
                .fillMaxWidth(),
        ) {
            Button(
                onClick = {},
                modifier = Modifier
                    .padding(Values.Spacing.around)
                    .fillMaxWidth(),
            ) {
                Text(
                    text = "Card content",
                )
            }
        }

        ExpandableCard(
            title = {
                Text(
                    text = "Custom title component",
                    color = MaterialTheme.colors.primary,
                    style = MaterialTheme.typography.h4,
                )
            },
            modifier = Modifier
                .padding(Values.Spacing.around)
                .fillMaxWidth(),
        ) {
            Button(
                onClick = {},
                modifier = Modifier
                    .padding(Values.Spacing.around)
                    .fillMaxWidth(),
            ) {
                Text(
                    text = "Card content",
                )
            }
        }
    }
}

@Composable
fun ExpandableCard(
    title: String,
    modifier: Modifier = Modifier,
    defaultExpanded: Boolean = false,
    content: @Composable BoxScope.() -> Unit,
) {
    ExpandableCard(
        title = {
            NormalText(
                text = title,
                modifier = Modifier.padding(Values.Spacing.around),
                style = MaterialTheme.typography.h5,
            )
        },
        modifier = modifier,
        defaultExpanded = defaultExpanded,
        content = content,
    )
}

@Composable
fun ExpandableCard(
    title: @Composable BoxScope.() -> Unit,
    modifier: Modifier = Modifier,
    defaultExpanded: Boolean = false,
    content: @Composable BoxScope.() -> Unit,
) {
    val expanded = remember { mutableStateOf(defaultExpanded) }
    val expandIconRotation by animateFloatAsState(
        targetValue = if (expanded.value) 0f else 180f,
        animationSpec = OneDirectionFloatSpringSpec(),
    )

    TitledCard(
        title = {
            Row(
                modifier = Modifier.clickable { expanded.value = !expanded.value },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier.weight(1f),
                ) {
                    title()
                }

                Icon(
                    modifier = Modifier
                        .padding(Values.Spacing.around)
                        .rotate(expandIconRotation),
                    imageVector = Icons.Default.ExpandLess,
                    contentDescription = when (expanded.value) {
                        true -> stringResource(R.string.hint_icon_collapse)
                        false -> stringResource(R.string.hint_icon_expand)
                    },
                )
            }
        },
        modifier = modifier,
    ) {
        Box(
            modifier = Modifier.animateContentSize(),
        ) {
            if (expanded.value) {
                content()
            }
        }
    }
}
