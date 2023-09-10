package app.revanced.patches.youtube.misc.layoutswitch.fingerprints

import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import com.android.tools.smali.dexlib2.Opcode

object ClientFormFactorFingerprint : MethodFingerprint(
    returnType = "L",
    parameters = emptyList(),
    opcodes = listOf(
        Opcode.IF_EQZ,
        Opcode.SGET_OBJECT,
        Opcode.GOTO
    )
)