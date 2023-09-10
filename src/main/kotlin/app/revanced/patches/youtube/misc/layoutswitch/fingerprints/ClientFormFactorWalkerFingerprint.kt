package app.revanced.patches.youtube.misc.layoutswitch.fingerprints

import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import com.android.tools.smali.dexlib2.Opcode

object ClientFormFactorWalkerFingerprint : MethodFingerprint(
    returnType = "L",
    parameters = emptyList(),
    opcodes = listOf(
        Opcode.INVOKE_STATIC,
        Opcode.MOVE_RESULT_OBJECT,
        Opcode.RETURN_OBJECT
    )
)