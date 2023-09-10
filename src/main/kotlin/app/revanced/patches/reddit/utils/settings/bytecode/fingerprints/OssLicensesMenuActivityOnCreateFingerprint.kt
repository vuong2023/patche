package app.revanced.patches.reddit.utils.settings.bytecode.fingerprints

import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import com.android.tools.smali.dexlib2.Opcode

object OssLicensesMenuActivityOnCreateFingerprint : MethodFingerprint(
    returnType = "V",
    opcodes = listOf(
        Opcode.IGET_BOOLEAN,
        Opcode.IF_EQZ,
        Opcode.INVOKE_STATIC
    ),
    customFingerprint = { methodDef, _ ->
        methodDef.definingClass.endsWith("/OssLicensesMenuActivity;") &&
                methodDef.name == "onCreate"
    }
)