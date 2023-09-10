package app.revanced.patches.music.utils.returnyoutubedislike.bytecode.fingerprints

import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint

object RemoveLikeFingerprint : MethodFingerprint(
    returnType = "V",
    strings = listOf("like/removelike")
)