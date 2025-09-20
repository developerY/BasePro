package com.ylabz.basepro.feature.material3.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.SplitButtonDefaults
import androidx.compose.material3.SplitButtonLayout
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SplitButtonSimple() {
    var checked by remember { mutableStateOf(false) }

    SplitButtonLayout(
        leadingButton = {
            SplitButtonDefaults.ElevatedLeadingButton(
                onClick = { /* Primary action */ }
            ) {
                Icon(
                    Icons.Filled.Edit,
                    contentDescription = "Edit",
                    modifier = Modifier.size(SplitButtonDefaults.LeadingIconSize)
                )
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text("Edit Document")
            }
        },
        trailingButton = {
            SplitButtonDefaults.ElevatedTrailingButton(
                checked = checked,
                onCheckedChange = { checked = it }
            ) {
                // Animate the rotation of the dropdown arrow
                val rotation by animateFloatAsState(
                    targetValue = if (checked) 180f else 0f,
                    label = "TrailingIconRotation"
                )
                Icon(
                    Icons.Filled.KeyboardArrowDown,
                    contentDescription = "Expand for more options",
                    modifier = Modifier.graphicsLayer { rotationZ = rotation }
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SplitButtonExample(modifier: Modifier = Modifier) {

    var checked by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize().wrapContentSize()) {
        SplitButtonLayout(
            leadingButton = {
                SplitButtonDefaults.LeadingButton(onClick = { /* Do Nothing */ }) {
                    Icon(
                        Icons.Filled.Edit,
                        modifier = Modifier.size(SplitButtonDefaults.LeadingIconSize),
                        contentDescription = "Localized description",
                    )
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                    Text("My Button")
                }
            },
            trailingButton = {
                val description = "Toggle Button"
                // Icon-only trailing button should have a tooltip for a11y.
                TooltipBox(
                    positionProvider =
                        TooltipDefaults.rememberTooltipPositionProvider(
                            TooltipAnchorPosition.Above
                        ),
                    tooltip = { PlainTooltip { Text(description) } },
                    state = rememberTooltipState(),
                ) {
                    SplitButtonDefaults.TrailingButton(
                        checked = checked,
                        onCheckedChange = { checked = it },
                        modifier =
                            Modifier.semantics {
                                stateDescription = if (checked) "Expanded" else "Collapsed"
                                // contentDescription = description
                            },
                    ) {
                        val rotation: Float by
                        animateFloatAsState(
                            targetValue = if (checked) 180f else 0f,
                            label = "Trailing Icon Rotation",
                        )
                        Icon(
                            Icons.Filled.KeyboardArrowDown,
                            modifier =
                                Modifier.size(SplitButtonDefaults.TrailingIconSize).graphicsLayer {
                                    this.rotationZ = rotation
                                },
                            contentDescription = "Localized description",
                        )
                    }
                }
            },
        )

        DropdownMenu(expanded = checked, onDismissRequest = { checked = false }) {
            DropdownMenuItem(
                text = { Text("Edit") },
                onClick = { /* Handle edit! */ },
                leadingIcon = { Icon(Icons.Outlined.Edit, contentDescription = null) },
            )
            DropdownMenuItem(
                text = { Text("Settings") },
                onClick = { /* Handle settings! */ },
                leadingIcon = { Icon(Icons.Outlined.Settings, contentDescription = null) },
            )
            HorizontalDivider()
            DropdownMenuItem(
                text = { Text("Send Feedback") },
                onClick = { /* Handle send feedback! */ },
                leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null) },
                trailingIcon = { Text("F11", textAlign = TextAlign.Center) },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview
@Composable
fun SplitButtonExamplePreview() {
    SplitButtonExample()
}