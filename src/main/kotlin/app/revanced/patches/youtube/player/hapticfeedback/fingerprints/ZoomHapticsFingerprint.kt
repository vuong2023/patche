package app.revanced.patches.youtube.player.hapticfeedback.fingerprints

import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint

object ZoomHapticsFingerprint : MethodFingerprint(
    returnType = "V",
    strings = listOf("Failed to haptics vibrate for video zoom")
)