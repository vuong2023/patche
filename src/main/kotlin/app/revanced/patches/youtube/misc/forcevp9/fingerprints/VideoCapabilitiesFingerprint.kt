package app.revanced.patches.youtube.misc.forcevp9.fingerprints

import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import com.android.tools.smali.dexlib2.Opcode

object VideoCapabilitiesFingerprint : MethodFingerprint(
    returnType = "V",
    opcodes = listOf(
        Opcode.IPUT,
        Opcode.IPUT,
        Opcode.IPUT,
        Opcode.IPUT
    ),
    customFingerprint = { methodDef, _ -> methodDef.name == "<init>" }
)
