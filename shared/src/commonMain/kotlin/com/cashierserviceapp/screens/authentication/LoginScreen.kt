package com.cashierserviceapp.screens.authentication

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cashierserviceapp.utils.Resource
import com.cashierserviceapp.ui.components.Button
import com.cashierserviceapp.ui.components.Text
import com.cashierserviceapp.ui.components.TextField
import com.cashierserviceapp.ui.theme.CashierServiceTheme
import com.cashierserviceapp.ui.theme.PreviewHelper
import com.cashierserviceapp.ui.utils.PreviewLightDark
import com.cashierserviceapp.ui.icons.ClosedEyeOutlined
import com.cashierserviceapp.ui.icons.OpenEyeOutlined
import dev.zacsweers.metrox.viewmodel.metroViewModel

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    modifier: Modifier = Modifier,
    onForgotPassword: () -> Unit = {},
    onSignUp: () -> Unit = {},
    viewModel: LoginViewModel = metroViewModel(),
) {
    val loginState by viewModel.loginState.collectAsStateWithLifecycle()

    LaunchedEffect(loginState) {
        if (loginState is Resource.Success) onLoginSuccess()
    }

    LoginContent(
        modifier = modifier,
        isLoading = loginState is Resource.Loading,
        errorMessage = (loginState as? Resource.Error)?.message,
        onSubmit = viewModel::login,
        onInputChanged = viewModel::clearError,
        onForgotPassword = onForgotPassword,
        onSignUp = onSignUp,
    )
}

/**
 * Stateless half of the screen, so previews render without needing the DI graph to stand up a
 * [LoginViewModel].
 */
@Composable
private fun LoginContent(
    isLoading: Boolean,
    errorMessage: String?,
    onSubmit: (email: String, password: String) -> Unit,
    modifier: Modifier = Modifier,
    onInputChanged: () -> Unit = {},
    onForgotPassword: () -> Unit = {},
    onSignUp: () -> Unit = {},
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val passwordFocus = remember { FocusRequester() }
    val passwordMask = remember { PasswordVisualTransformation() }
    val canSubmit = email.isNotBlank() && password.isNotBlank() && !isLoading
    val submit = { if (canSubmit) onSubmit(email, password) }

    Column(
        modifier = modifier
            .fillMaxSize()
            // Opaque for the same reason as SearchScreen: signing out transitions Settings to this
            // screen, and both are drawn while that runs.
            .background(CashierServiceTheme.colors.mainBackground)
            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Vertical))
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
    ) {
        Spacer(Modifier.height(56.dp))

        Text(
            text = "Welcome back",
            style = CashierServiceTheme.typography.h1,
            color = CashierServiceTheme.colors.primaryText
        )

        Spacer(Modifier.height(10.dp))

        Text(
            text = "Sign in to your cashier account to keep taking orders.",
            style = CashierServiceTheme.typography.text1,
            color = CashierServiceTheme.colors.secondaryText
        )

        Spacer(Modifier.height(40.dp))

        TextField(
            value = email,
            onValueChange = { email = it; onInputChanged() },
            modifier = Modifier.fillMaxWidth(),
            label = "Email",
            enabled = !isLoading,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onNext = { passwordFocus.requestFocus() })
        )

        Spacer(Modifier.height(12.dp))

        TextField(
            value = password,
            onValueChange = { password = it; onInputChanged() },
            modifier = Modifier.fillMaxWidth(),
            label = "Password",
            focusRequester = passwordFocus,
            enabled = !isLoading,
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else passwordMask,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = { submit() }),
            trailing = {
                Icon(
                    imageVector = if (passwordVisible) ClosedEyeOutlined else OpenEyeOutlined,
                    contentDescription = "showPassword",
                    tint = CashierServiceTheme.colors.secondaryText,
                    modifier = Modifier.clickable(
                        interactionSource = null,
                        indication = null,
                    ) { passwordVisible = !passwordVisible }
                )
            }
        )

        if (errorMessage != null) {
            Spacer(Modifier.height(12.dp))

            Text(
                text = errorMessage,
                modifier = Modifier.fillMaxWidth(),
                style = CashierServiceTheme.typography.text2,
                color = CashierServiceTheme.colors.accentText
            )
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = "Forgot password?",
            modifier = Modifier
                .align(Alignment.End)
                .clickable(onClick = onForgotPassword),
            style = CashierServiceTheme.typography.h4
        )

        Spacer(Modifier.height(32.dp))

        Button(
            label = if (isLoading) "Signing in…" else "Sign in",
            onClick = submit,
            modifier = Modifier.fillMaxWidth(),
            primary = true,
            enabled = canSubmit,
        )

        Spacer(Modifier.height(24.dp))

        LabelledDivider()

        Spacer(Modifier.height(24.dp))

        Button(
            label = "Create an account",
            onClick = onSignUp,
            modifier = Modifier.fillMaxWidth(),
            primary = false
        )

        Spacer(Modifier.height(40.dp))

        Text(
            text = "By continuing you agree to our Terms of Service and Privacy Policy.",
            modifier = Modifier.fillMaxWidth(),
            style = CashierServiceTheme.typography.text2.copy(textAlign = TextAlign.Center),
            color = CashierServiceTheme.colors.noteText
        )

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun LabelledDivider() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Rule(Modifier.weight(1f))
        Text(
            text = "or",
            modifier = Modifier.padding(horizontal = 16.dp),
            style = CashierServiceTheme.typography.text2,
            color = CashierServiceTheme.colors.noteText
        )
        Rule(Modifier.weight(1f))
    }
}

@Composable
private fun Rule(modifier: Modifier = Modifier) {
    Box(
        modifier
            .height(1.dp)
            .background(CashierServiceTheme.colors.strokePale)
    )
}

@PreviewLightDark
@Composable
private fun LoginScreenPreview() = PreviewHelper(paddingEnabled = false) {
    LoginContent(isLoading = false, errorMessage = null, onSubmit = { _, _ -> })
}

@PreviewLightDark
@Composable
private fun LoginScreenErrorPreview() = PreviewHelper(paddingEnabled = false) {
    LoginContent(
        isLoading = false,
        errorMessage = "Email or password is incorrect.",
        onSubmit = { _, _ -> }
    )
}
