package tech.soc.soar.shared.domain.auth.model

data class TwoFactorVerificationResult(
    val status: String,
    val newAccessToken: String
)