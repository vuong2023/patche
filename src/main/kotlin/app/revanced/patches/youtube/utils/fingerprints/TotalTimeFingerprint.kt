package app.revanced.patches.youtube.utils.fingerprints

import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import app.revanced.patches.youtube.utils.resourceid.patch.SharedResourceIdPatch.Companion.TotalTime
import app.revanced.util.bytecode.isWideLiteralExists

object TotalTimeFingerprint : MethodFingerprint(
    returnType = "V",
    customFingerprint = { methodDef, _ -> methodDef.isWideLiteralExists(TotalTime) }
)