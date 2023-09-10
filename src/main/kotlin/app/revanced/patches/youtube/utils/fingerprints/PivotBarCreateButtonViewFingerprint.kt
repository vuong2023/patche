package app.revanced.patches.youtube.utils.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import app.revanced.patches.youtube.utils.resourceid.patch.SharedResourceIdPatch.Companion.ImageOnlyTab
import app.revanced.util.bytecode.isWideLiteralExists
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode

object PivotBarCreateButtonViewFingerprint : MethodFingerprint(
    returnType = "V",
    accessFlags = AccessFlags.PUBLIC or AccessFlags.FINAL,
    opcodes = listOf(
        Opcode.MOVE_OBJECT,
        Opcode.INVOKE_DIRECT_RANGE, // unique instruction anchor
    ),
    customFingerprint = { methodDef, _ -> methodDef.isWideLiteralExists(ImageOnlyTab) }
)