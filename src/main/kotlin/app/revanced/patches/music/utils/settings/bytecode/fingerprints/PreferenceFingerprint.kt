package app.revanced.patches.music.utils.settings.bytecode.fingerprints

import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import com.android.tools.smali.dexlib2.Opcode

object PreferenceFingerprint : MethodFingerprint(
    returnType = "V",
    parameters = listOf("Z"),
    opcodes = listOf(
        Opcode.RETURN_VOID,
        Opcode.XOR_INT_LIT8,
        Opcode.INVOKE_VIRTUAL,
        Opcode.MOVE_RESULT,
        Opcode.IF_NE,
        Opcode.RETURN_VOID,
        Opcode.IGET_OBJECT,
        Opcode.INVOKE_VIRTUAL,
        Opcode.MOVE_RESULT_OBJECT,
        Opcode.IGET_OBJECT,
        Opcode.INVOKE_INTERFACE
    ),
    customFingerprint = { methodDef, _ -> methodDef.definingClass == "Landroidx/preference/Preference;" }
)