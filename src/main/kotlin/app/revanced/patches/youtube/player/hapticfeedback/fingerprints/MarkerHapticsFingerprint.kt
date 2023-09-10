package app.revanced.patches.youtube.player.hapticfeedback.fingerprints

import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint

object MarkerHapticsFingerprint : MethodFingerprint(
    returnType = "V",
    strings = listOf("Failed to execute markers haptics vibrate.")
)