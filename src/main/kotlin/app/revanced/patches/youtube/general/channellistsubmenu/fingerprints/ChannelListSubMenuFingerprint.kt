package app.revanced.patches.youtube.general.channellistsubmenu.fingerprints

import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import app.revanced.patches.youtube.utils.resourceid.patch.SharedResourceIdPatch.Companion.ChannelListSubMenu
import app.revanced.util.bytecode.isWideLiteralExists
import com.android.tools.smali.dexlib2.Opcode

object ChannelListSubMenuFingerprint : MethodFingerprint(
    opcodes = listOf(
        Opcode.CONST,
        Opcode.CONST_4,
        Opcode.INVOKE_VIRTUAL,
        Opcode.MOVE_RESULT_OBJECT
    ),
    customFingerprint = { methodDef, _ -> methodDef.isWideLiteralExists(ChannelListSubMenu) }
)