package ui.components

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import ui.theme.AppColors
import ui.theme.AppSpacing

/**
 * ⏳ Loading Spinner Component
 * 
 * Reusable loading indicator with customizable size and color
 * Perfect for authentication screens and async operations
 */
@Composable
fun LoadingSpinner(
    size: CSSLengthValue = 40.px,
    color: Color = AppColors.primary,
    message: String? = null
) {
    Div({
        style {
            display(DisplayStyle.Flex)
            flexDirection(FlexDirection.Column)
            alignItems(AlignItems.Center)
            justifyContent(JustifyContent.Center)
            padding(AppSpacing.large.px)
        }
    }) {
        // Spinner Animation
        Div({
            style {
                width(size)
                height(size)
                border(3.px, LineStyle.Solid, Color.transparent)
                borderTop(3.px, LineStyle.Solid, color)
                borderRadius(50.percent)
                animation("spin 1s linear infinite")
            }
        })
        
        // Optional Loading Message
        message?.let { text ->
            P({
                style {
                    marginTop(AppSpacing.medium.px)
                    color(AppColors.textSecondary)
                    fontSize(14.px)
                    textAlign("center")
                }
            }) {
                Text(text)
            }
        }
    }
}

/**
 * 🎯 CSS Animation for Spinner
 * Note: This would typically be defined in a CSS file or global styles
 */
private val spinAnimation = """
    @keyframes spin {
        0% { transform: rotate(0deg); }
        100% { transform: rotate(360deg); }
    }
""".trimIndent()
