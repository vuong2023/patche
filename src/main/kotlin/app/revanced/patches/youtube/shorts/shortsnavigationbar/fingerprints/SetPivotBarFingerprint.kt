package app.revanced.patches.youtube.shorts.shortsnavigationbar.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode

object SetPivotBarFingerprint : MethodFingerprint(
    returnType = "V",
    accessFlags = AccessFlags.PRIVATE or AccessFlags.FINAL,
    parameters = listOf("Z"),
    opcodes = listOf(
        Opcode.CHECK_CAST,
        Opcode.IF_EQZ
    )
)