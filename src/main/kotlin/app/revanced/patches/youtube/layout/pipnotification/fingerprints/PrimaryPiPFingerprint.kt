package app.revanced.patches.youtube.layout.pipnotification.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode

object PrimaryPiPFingerprint : MethodFingerprint(
    returnType = "V",
    accessFlags = AccessFlags.PUBLIC or AccessFlags.FINAL,
    parameters = listOf("L"),
    opcodes = listOf(
        null,
        Opcode.CHECK_CAST,
        Opcode.INVOKE_VIRTUAL,
        Opcode.IGET_OBJECT,  // injection point
        Opcode.CHECK_CAST,
        Opcode.INVOKE_VIRTUAL,
        null,  // I used these null property to change the
        null,  // injection point. It is not recommended
        null,  // but I am too lazy to update PiPNotificationPatch .-.
    ),
    strings = listOf("honeycomb.Shell\$HomeActivity")
)
