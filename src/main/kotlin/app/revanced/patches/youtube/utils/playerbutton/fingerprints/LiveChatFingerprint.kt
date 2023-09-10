package app.revanced.patches.youtube.utils.playerbutton.fingerprints

import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import app.revanced.patches.youtube.utils.resourceid.patch.SharedResourceIdPatch.Companion.LiveChatButton
import app.revanced.util.bytecode.isWideLiteralExists
import com.android.tools.smali.dexlib2.Opcode

object LiveChatFingerprint : MethodFingerprint(
    opcodes = listOf(Opcode.NEW_INSTANCE),
    customFingerprint = { methodDef, _ -> methodDef.isWideLiteralExists(LiveChatButton) }
)