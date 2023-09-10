package app.revanced.patches.youtube.video.customspeed.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode

object SpeedArrayGeneratorFingerprint : MethodFingerprint(
    returnType = "[L",
    accessFlags = AccessFlags.PUBLIC or AccessFlags.STATIC,
    opcodes = listOf(
        Opcode.CONST_4,
        Opcode.NEW_ARRAY
    ),
    strings = listOf("0.0#")
)
