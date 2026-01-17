package com.idormy.sms.forwarder.utils

import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * SM4分组密码算法是我国自主设计的分组对称密码算法
 *
 * SECURITY WARNING:
 * - The default IV (SM4_CBC_IV) is hardcoded and should NOT be used in production
 * - Always generate a random IV for each encryption operation using SecureRandom
 * - Store or transmit the IV alongside the ciphertext (IV does not need to be secret)
 * - Using the same IV with the same key for multiple encryptions weakens security
 * - For maximum security, always provide a unique IV parameter when calling encrypt()
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
object SM4Crypt {

    const val SM4_CBC_NOPADDING = "SM4/CBC/NoPadding"
    const val SM4_CBC_PKCS5 = "SM4/CBC/PKCS5Padding"
    const val SM4_CBC_PKCS7 = "SM4/CBC/PKCS7Padding"
    const val SM4_ECB_NOPADDING = "SM4/ECB/NoPadding"
    const val SM4_ECB_PKCS5 = "SM4/ECB/PKCS5Padding"
    const val SM4_ECB_PKCS7 = "SM4/ECB/PKCS7Padding"
    private val BC_PROVIDER = BouncyCastleProvider()
    
    /**
     * DEPRECATED: Default IV - DO NOT USE IN PRODUCTION
     * This hardcoded IV reduces security. Generate a random IV instead.
     */
    @Deprecated("Use a randomly generated IV for each encryption operation", ReplaceWith("createRandomIV()"))
    private val SM4_CBC_IV = byteArrayOf(3, 5, 6, 9, 6, 9, 5, 9, 3, 5, 6, 9, 6, 9, 5, 9)

    /**
     * Generate a random 16-byte IV for SM4 CBC mode
     * SECURITY: Use this to generate a unique IV for each encryption operation
     */
    fun createRandomIV(): ByteArray {
        val iv = ByteArray(16)
        val random = SecureRandom()
        random.nextBytes(iv)
        return iv
    }

    /**
     * 获取随机密钥
     */
    fun createSM4Key(): ByteArray {
        val seed = ByteArray(16)
        val random = SecureRandom()
        random.nextBytes(seed)
        return seed
    }

    /**
     * DEPRECATED: encrypt() with default IV parameter
     * Use encryptSecure() instead for automatic random IV generation
     */
    @Deprecated("Use encryptSecure() for automatic random IV", ReplaceWith("encryptSecure(source, key, mode)"))
    @JvmOverloads
    fun encrypt(source: ByteArray, key: ByteArray, mode: String = SM4_CBC_PKCS7, iv: ByteArray? = SM4_CBC_IV): ByteArray {
        return doSM4(true, source, key, mode, iv)
    }

    /**
     * Secure encryption with automatically generated random IV
     * Returns: Pair of (encrypted data, IV used)
     * SECURITY: Caller must store/transmit the IV alongside ciphertext
     */
    fun encryptSecure(source: ByteArray, key: ByteArray, mode: String = SM4_CBC_PKCS7): Pair<ByteArray, ByteArray> {
        val iv = createRandomIV()
        val encrypted = doSM4(true, source, key, mode, iv)
        return Pair(encrypted, iv)
    }

    /**
     * DEPRECATED: decrypt() with default IV parameter
     * Use decryptSecure() instead with the IV from encryption
     */
    @Deprecated("Use decryptSecure() with explicit IV from encryption", ReplaceWith("decryptSecure(source, key, iv, mode)"))
    @JvmOverloads
    fun decrypt(source: ByteArray, key: ByteArray, mode: String = SM4_CBC_PKCS7, iv: ByteArray? = SM4_CBC_IV): ByteArray {
        return doSM4(false, source, key, mode, iv)
    }

    /**
     * Secure decryption with explicit IV (obtained from encryption)
     * SECURITY: IV must be the same one used during encryption
     */
    fun decryptSecure(source: ByteArray, key: ByteArray, iv: ByteArray, mode: String = SM4_CBC_PKCS7): ByteArray {
        return doSM4(false, source, key, mode, iv)
    }

    private fun doSM4(forEncryption: Boolean, source: ByteArray, key: ByteArray, mode: String, iv: ByteArray?): ByteArray {
        return try {
            val cryptMode = if (forEncryption) 1 else 2
            val sm4Key = SecretKeySpec(key, "SM4")
            val cipher = Cipher.getInstance(mode, BC_PROVIDER)
            if (iv == null) {
                cipher.init(cryptMode, sm4Key)
            } else {
                val ivParameterSpec = IvParameterSpec(iv)
                cipher.init(cryptMode, sm4Key, ivParameterSpec)
            }
            cipher.doFinal(source)
        } catch (var9: Exception) {
            var9.printStackTrace()
            ByteArray(0)
        }
    }

}