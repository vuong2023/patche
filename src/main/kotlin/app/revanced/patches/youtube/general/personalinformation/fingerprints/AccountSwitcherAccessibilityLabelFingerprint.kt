package app.revanced.patches.youtube.general.personalinformation.fingerprints

import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import app.revanced.patches.youtube.utils.resourceid.patch.SharedResourceIdPatch.Companion.AccountSwitcherAccessibility
import app.revanced.util.bytecode.isWideLiteralExists
import com.android.tools.smali.dexlib2.Opcode

object AccountSwitcherAccessibilityLabelFingerprint : MethodFingerprint(
    returnType = "V",
    parameters = listOf("L", "Ljava/lang/Object;"),
    opcodes = listOf(
        Opcode.INVOKE_VIRTUAL,
        Opcode.IGET_OBJECT,
        Opcode.IGET_OBJECT,
        Opcode.NEW_ARRAY,
        Opcode.CONST_4,
        Opcode.APUT_OBJECT,
        Opcode.CONST
    ),
    customFingerprint = { methodDef, _ -> methodDef.isWideLiteralExists(AccountSwitcherAccessibility) }
)