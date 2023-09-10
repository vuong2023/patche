package app.revanced.patches.music.misc.sharebuttonhook.fingerprints

import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import com.android.tools.smali.dexlib2.Opcode

object SharePanelFingerprint : MethodFingerprint(
    returnType = "V",
    opcodes = listOf(Opcode.INVOKE_VIRTUAL),
    strings = listOf("share/get_share_panel")
)
