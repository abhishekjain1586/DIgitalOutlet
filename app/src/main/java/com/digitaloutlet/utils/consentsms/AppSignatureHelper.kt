package com.digitaloutlet.utils.consentsms

import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.content.pm.Signature
import android.util.Base64.encodeToString
import android.util.Log
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*
import kotlin.collections.ArrayList


class AppSignatureHelper(context: Context) : ContextWrapper(context) {

    val TAG = AppSignatureHelper::class.java.simpleName;

    private val HASH_TYPE = "SHA-256"
    val NUM_HASHED_BYTES = 9
    val NUM_BASE64_CHAR = 11

    /**
     * Get all the app signatures for the current package
     *
     * @return
     */
    fun getAppSignatures(): ArrayList<String>? {
        val appCodes: ArrayList<String> = ArrayList()
        try {
            // Get all package signatures for the current package
            val packageName = packageName
            val packageManager = packageManager
            val signatures: Array<Signature> = packageManager.getPackageInfo(
                packageName,
                PackageManager.GET_SIGNATURES
            ).signatures

            // For each signature create a compatible hash
            for (signature in signatures) {
                val hash: String? = hash(packageName, signature.toCharsString())
                if (hash != null) {
                    appCodes.add(String.format("%s", hash))
                }
            }
        } catch (e: PackageManager.NameNotFoundException) {
            Log.v(TAG, "Unable to find package to obtain hash.", e)
        }
        return appCodes
    }

    private fun hash(packageName: String, signature: String): String? {
        val appInfo = packageName + " " + signature
        try {
            val messageDigest: MessageDigest = MessageDigest.getInstance(HASH_TYPE)
            messageDigest.update(appInfo.toByteArray(Charsets.UTF_8))
            var hashSignature: ByteArray = messageDigest.digest()

            // truncated into NUM_HASHED_BYTES
            hashSignature = Arrays.copyOfRange(hashSignature, 0, NUM_HASHED_BYTES)
            // encode into Base64
            var base64Hash: String =
                Base64.getEncoder().encodeToString(hashSignature/*, Base64.NO_PADDING or Base64.NO_WRAP*/)
            base64Hash = base64Hash.substring(0, NUM_BASE64_CHAR)
            Log.v(
                TAG + "sms_sample_test",
                String.format("pkg: %s -- hash: %s", packageName, base64Hash)
            )
            return base64Hash
        } catch (e: NoSuchAlgorithmException) {
            Log.v(TAG + "sms_sample_test", "hash:NoSuchAlgorithm", e)
        }
        return null
    }
}