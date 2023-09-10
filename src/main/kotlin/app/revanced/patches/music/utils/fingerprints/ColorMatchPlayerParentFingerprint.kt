package app.revanced.patches.music.utils.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode

object ColorMatchPlayerParentFingerprint : MethodFingerprint(
    returnType = "V",
    accessFlags = AccessFlags.PRIVATE or AccessFlags.FINAL,
    opcodes = listOf(
        Opcode.IGET,
        Opcode.IGET,
        Opcode.CONST_WIDE_16,
        Opcode.IF_EQ,
        Opcode.IPUT,
        Opcode.IGET_OBJECT,
        Opcode.INVOKE_VIRTUAL,
        Opcode.IGET,
        Opcode.IGET,
        Opcode.IF_EQ,
        Opcode.IPUT,
        Opcode.IGET_OBJECT,
        Opcode.INVOKE_VIRTUAL
    )
)