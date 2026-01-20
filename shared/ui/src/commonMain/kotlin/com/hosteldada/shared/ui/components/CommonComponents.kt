package com.hosteldada.shared.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.hosteldada.shared.ui.theme.*

/**
 * Primary Button - Filled style
 */
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    icon: ImageVector? = null
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        enabled = enabled && !isLoading,
        shape = RoundedCornerShape(HostelDadaRadius.S.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = HostelDadaColors.Primary,
            contentColor = HostelDadaColors.OnPrimary,
            disabledContainerColor = HostelDadaColors.Primary.copy(alpha = 0.5f),
            disabledContentColor = HostelDadaColors.OnPrimary.copy(alpha = 0.5f)
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = HostelDadaColors.OnPrimary,
                strokeWidth = 2.dp
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        icon?.let {
            Icon(
                imageVector = it,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(text = text)
    }
}

/**
 * Secondary Button - Outlined style
 */
@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        enabled = enabled,
        shape = RoundedCornerShape(HostelDadaRadius.S.dp),
        border = BorderStroke(1.dp, HostelDadaColors.Primary),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = HostelDadaColors.Primary
        )
    ) {
        icon?.let {
            Icon(
                imageVector = it,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(text = text)
    }
}

/**
 * Text Button
 */
@Composable
fun TextLinkButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = HostelDadaColors.Primary
) {
    TextButton(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.textButtonColors(
            contentColor = color
        )
    ) {
        Text(text = text)
    }
}

/**
 * Custom TextField with validation
 */
@Composable
fun ValidatedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    error: String? = null,
    placeholder: String? = null,
    leadingIcon: ImageVector? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    onImeAction: () -> Unit = {},
    singleLine: Boolean = true,
    maxLines: Int = 1,
    enabled: Boolean = true
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            placeholder = placeholder?.let { { Text(it) } },
            leadingIcon = leadingIcon?.let {
                {
                    Icon(
                        imageVector = it,
                        contentDescription = null,
                        tint = if (error != null) HostelDadaColors.Error 
                               else HostelDadaColors.OnSurfaceVariant
                    )
                }
            },
            trailingIcon = trailingIcon,
            isError = error != null,
            singleLine = singleLine,
            maxLines = maxLines,
            enabled = enabled,
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType,
                imeAction = imeAction
            ),
            keyboardActions = KeyboardActions(
                onDone = { onImeAction() },
                onNext = { onImeAction() },
                onGo = { onImeAction() }
            ),
            shape = RoundedCornerShape(HostelDadaRadius.S.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = HostelDadaColors.Primary,
                unfocusedBorderColor = HostelDadaColors.Outline,
                errorBorderColor = HostelDadaColors.Error,
                focusedLabelColor = HostelDadaColors.Primary,
                unfocusedLabelColor = HostelDadaColors.OnSurfaceVariant,
                errorLabelColor = HostelDadaColors.Error
            ),
            modifier = Modifier.fillMaxWidth()
        )
        
        error?.let {
            Text(
                text = it,
                color = HostelDadaColors.Error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

/**
 * Password TextField with visibility toggle
 */
@Composable
fun PasswordTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    error: String? = null,
    isPasswordVisible: Boolean = false,
    onToggleVisibility: () -> Unit = {},
    leadingIcon: ImageVector? = null,
    imeAction: ImeAction = ImeAction.Done,
    onImeAction: () -> Unit = {}
) {
    ValidatedTextField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        modifier = modifier,
        error = error,
        leadingIcon = leadingIcon,
        trailingIcon = {
            IconButton(onClick = onToggleVisibility) {
                // TODO: Add visibility icons
                Text(if (isPasswordVisible) "Hide" else "Show")
            }
        },
        keyboardType = KeyboardType.Password,
        imeAction = imeAction,
        onImeAction = onImeAction
    )
}

/**
 * Loading indicator overlay
 */
@Composable
fun LoadingOverlay(
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(modifier = modifier) {
        content()
        
        if (isLoading) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color.Black.copy(alpha = 0.3f)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = HostelDadaColors.Primary,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
        }
    }
}

/**
 * Error message banner
 */
@Composable
fun ErrorBanner(
    message: String?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (message != null) {
        Surface(
            modifier = modifier.fillMaxWidth(),
            color = HostelDadaColors.ErrorContainer,
            shape = RoundedCornerShape(HostelDadaRadius.S.dp)
        ) {
            Row(
                modifier = Modifier.padding(HostelDadaSpacing.M.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = message,
                    color = HostelDadaColors.Error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
                TextButton(onClick = onDismiss) {
                    Text("Dismiss", color = HostelDadaColors.Error)
                }
            }
        }
    }
}

/**
 * Success message banner
 */
@Composable
fun SuccessBanner(
    message: String?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (message != null) {
        Surface(
            modifier = modifier.fillMaxWidth(),
            color = HostelDadaColors.SuccessContainer,
            shape = RoundedCornerShape(HostelDadaRadius.S.dp)
        ) {
            Row(
                modifier = Modifier.padding(HostelDadaSpacing.M.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = message,
                    color = HostelDadaColors.SuccessDark,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
                TextButton(onClick = onDismiss) {
                    Text("OK", color = HostelDadaColors.SuccessDark)
                }
            }
        }
    }
}

/**
 * Card component with standard styling
 */
@Composable
fun HostelDadaCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    if (onClick != null) {
        Card(
            onClick = onClick,
            modifier = modifier,
            shape = RoundedCornerShape(HostelDadaRadius.M.dp),
            colors = CardDefaults.cardColors(
                containerColor = HostelDadaColors.Surface
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = HostelDadaElevation.S.dp
            )
        ) {
            Column(
                modifier = Modifier.padding(HostelDadaSpacing.L.dp),
                content = content
            )
        }
    } else {
        Card(
            modifier = modifier,
            shape = RoundedCornerShape(HostelDadaRadius.M.dp),
            colors = CardDefaults.cardColors(
                containerColor = HostelDadaColors.Surface
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = HostelDadaElevation.S.dp
            )
        ) {
            Column(
                modifier = Modifier.padding(HostelDadaSpacing.L.dp),
                content = content
            )
        }
    }
}

/**
 * Status Badge component
 */
@Composable
fun StatusBadge(
    text: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(HostelDadaRadius.Full.dp),
        color = color.copy(alpha = 0.15f)
    ) {
        Text(
            text = text,
            color = color,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(
                horizontal = HostelDadaSpacing.S.dp,
                vertical = HostelDadaSpacing.XS.dp
            )
        )
    }
}

/**
 * Compatibility Score Badge
 */
@Composable
fun CompatibilityBadge(
    score: Int,
    modifier: Modifier = Modifier
) {
    val color = HostelDadaColors.getScoreColor(score)
    
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(HostelDadaRadius.Full.dp),
        color = color.copy(alpha = 0.15f)
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = HostelDadaSpacing.M.dp,
                vertical = HostelDadaSpacing.S.dp
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$score%",
                color = color,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

/**
 * Empty state component
 */
@Composable
fun EmptyState(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    action: @Composable (() -> Unit)? = null
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        icon?.let {
            Icon(
                imageVector = it,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = HostelDadaColors.OnSurfaceVariant
            )
            Spacer(modifier = Modifier.height(HostelDadaSpacing.L.dp))
        }
        
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = HostelDadaColors.OnSurface
        )
        
        Spacer(modifier = Modifier.height(HostelDadaSpacing.S.dp))
        
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = HostelDadaColors.OnSurfaceVariant
        )
        
        action?.let {
            Spacer(modifier = Modifier.height(HostelDadaSpacing.XL.dp))
            it()
        }
    }
}

/**
 * Section header
 */
@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    action: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = HostelDadaColors.OnSurface
            )
            subtitle?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = HostelDadaColors.OnSurfaceVariant
                )
            }
        }
        action?.invoke()
    }
}
