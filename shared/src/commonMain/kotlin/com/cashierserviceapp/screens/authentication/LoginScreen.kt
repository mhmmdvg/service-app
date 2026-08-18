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
import com.cashierserviceapp.localization.message
import com.cashierserviceapp.ui.components.Button
import com.cashierserviceapp.ui.components.Text
import com.cashierserviceapp.ui.components.TextField
import com.cashierserviceapp.ui.icons.ClosedEyeOutlined
import com.cashierserviceapp.ui.icons.OpenEyeOutlined
import com.cashierserviceapp.ui.theme.CashierServiceTheme
import com.cashierserviceapp.ui.theme.PreviewHelper
import com.cashierserviceapp.ui.utils.PreviewLightDark
import com.cashierserviceapp.utils.Resource
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
    val formState by viewModel.formState.collectAsStateWithLifecycle()

    LaunchedEffect(loginState) {
        if (loginState is Resource.Success) onLoginSuccess()
    }

    LoginContent(
        modifier = modifier,
        isLoading = loginState is Resource.Loading,
        errorMessage = (loginState as? Resource.Error)?.message,
        formState = formState,
        onSubmit = viewModel::onLogin,
        onInputChanged = viewModel::onLoginEvent,
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
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier,
    formState: LoginFormState,
    onInputChanged: (LoginFormEvent) -> Unit = {},
    onForgotPassword: () -> Unit = {},
    onSignUp: () -> Unit = {},
) {
    var passwordVisible by remember { mutableStateOf(false) }

    val passwordFocus = remember { FocusRequester() }
    val passwordMask = remember { PasswordVisualTransformation() }
    val canSubmit = formState.email.isNotBlank() && formState.password.isNotBlank() && !isLoading

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

        Column(
            Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            TextField(
                value = formState.email,
                onValueChange = { onInputChanged(LoginFormEvent.EmailChanged(it)) },
                modifier = Modifier.fillMaxWidth(),
                error = formState.emailError != null,
                label = "Email",
                enabled = !isLoading,
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(onNext = { passwordFocus.requestFocus() })
            )
            formState.emailError?.let {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = it.message(),
                    style = CashierServiceTheme.typography.text2,
                    color = CashierServiceTheme.colors.dangerText
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        Column(
            Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            TextField(
                value = formState.password,
                onValueChange = { onInputChanged(LoginFormEvent.PasswordChanged(it)) },
                error = formState.passwordError != null,
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
                keyboardActions = KeyboardActions(onDone = { onSubmit() }),
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
            formState.passwordError?.let {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = it.message(),
                    style = CashierServiceTheme.typography.text2,
                    color = CashierServiceTheme.colors.dangerText
                )
            }
        }

        if (errorMessage != null) {
            Spacer(Modifier.height(12.dp))

            Text(
                text = errorMessage,
                modifier = Modifier.fillMaxWidth(),
                style = CashierServiceTheme.typography.text2,
                color = CashierServiceTheme.colors.dangerText
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
            onClick = onSubmit,
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

private val formStatePreview = LoginFormState(
    email = "",
    password = "",
)

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


    LoginContent(isLoading = false, errorMessage = null, formState = formStatePreview, onSubmit = { })
}

@PreviewLightDark
@Composable
private fun LoginScreenErrorPreview() = PreviewHelper(paddingEnabled = false) {
    LoginContent(
        isLoading = false,
        errorMessage = "Email or password is incorrect.",
        formState = formStatePreview,
        onSubmit = { }
    )
}
