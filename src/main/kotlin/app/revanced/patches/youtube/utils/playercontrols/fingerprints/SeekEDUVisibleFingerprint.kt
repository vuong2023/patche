package app.revanced.patches.youtube.utils.playercontrols.fingerprints

import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.instruction.NarrowLiteralInstruction

object SeekEDUVisibleFingerprint : MethodFingerprint(
    returnType = "V",
    parameters = listOf("Z"),
    opcodes = listOf(Opcode.OR_INT_LIT8),
    customFingerprint = { methodDef, _ ->
        methodDef.implementation!!.instructions.any {
            ((it as? NarrowLiteralInstruction)?.narrowLiteral == 32)
        }
    }
)