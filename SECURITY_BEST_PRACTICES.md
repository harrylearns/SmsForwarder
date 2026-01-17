# Security Best Practices Guide

## For Users of SmsForwarder

This guide provides security recommendations for users of the SmsForwarder application to ensure safe and secure SMS and notification forwarding.

---

## 🔒 Essential Security Practices

### 1. Server Authentication

**⚠️ CRITICAL: Always enable server authentication when exposing the HTTP server**

If you're using the built-in HTTP server feature:

1. **Set a Server Sign Key:**
   - Go to Settings → Server Settings
   - Enable "Server Sign Key" 
   - Set a strong, unique key (minimum 16 characters)
   - Use a password manager to generate and store the key

2. **Use Strong Keys:**
   - ✅ Good: `xK9mP2qR8sT4vW7yZ3aB5cD6eF1gH0jL`
   - ❌ Bad: `123456`, `password`, `88888888`

3. **Network Exposure:**
   - Only expose the server on trusted networks
   - Use Frpc/VPN for remote access instead of direct internet exposure
   - Configure firewall rules to restrict access

---

### 2. HTTPS for Forwarding Endpoints

**Always use HTTPS URLs for forwarding targets**

```
✅ Correct:   https://api.example.com/webhook
❌ Incorrect: http://api.example.com/webhook
```

**Why?**
- HTTP transmits SMS content in plaintext
- Anyone on the network can intercept messages
- HTTPS encrypts data in transit

**Exception:** Localhost/local network testing only

---

### 3. API Key Security

**Protect your forwarding service API keys:**

1. **Never share API keys or tokens**
   - Each user should have their own keys
   - Don't post keys in public forums or screenshots

2. **Use limited-scope tokens when available**
   - Create read-only tokens where possible
   - Use tokens specific to SMS forwarding (not admin tokens)

3. **Rotate keys regularly**
   - Change API keys every 3-6 months
   - Immediately rotate if you suspect compromise

4. **Revoke unused keys**
   - Remove old configurations
   - Delete API keys for services you no longer use

---

### 4. Permission Management

**Grant only necessary permissions:**

The app requests many permissions. Here's what they're used for:

| Permission | Purpose | When to Grant |
|-----------|---------|---------------|
| SMS (Read/Receive) | Core functionality - forward SMS | Always (required) |
| Contacts | Match sender names | Optional - only if needed |
| Phone State | Detect SIM card info | Recommended |
| Location | Include location in forwarded messages | Optional - only if needed |
| Camera | Scan QR codes for configuration | Optional - only if needed |
| Storage | Backup/restore settings | Optional |
| Notification Access | Forward app notifications | Only if using this feature |

**To review permissions:**
1. Android Settings → Apps → SmsForwarder → Permissions
2. Disable any permissions you don't need

---

### 5. Device Security

**Secure your Android device:**

1. **Enable Device Encryption**
   - Settings → Security → Encryption
   - Protects app data if device is stolen

2. **Use Strong Screen Lock**
   - PIN/Pattern minimum 6 digits
   - Fingerprint/Face unlock + PIN backup

3. **Keep Android Updated**
   - Install security patches promptly
   - Enable automatic updates when possible

4. **Enable Google Play Protect**
   - Scans apps for malware
   - Settings → Security → Google Play Protect

---

### 6. Application Updates

**Keep SmsForwarder updated:**

- ✅ Enable auto-updates in app settings
- ✅ Review changelogs for security fixes
- ✅ Update within 1 week of new release
- ⚠️ Avoid unofficial/modified versions

---

### 7. Backup Security

**Secure your configuration backups:**

1. **Encrypt backups:**
   - Use app's built-in encryption feature
   - Set a strong backup password

2. **Store backups securely:**
   - Use cloud storage with encryption (Google Drive with 2FA)
   - Or keep offline backups on encrypted USB

3. **Delete old backups:**
   - Remove backups from public/shared folders
   - Don't leave backups on unencrypted SD cards

---

### 8. Network Configuration

**When using Frpc for remote access:**

1. **Set strong token:**
   ```ini
   [common]
   token = [USE_STRONG_RANDOM_TOKEN_HERE]
   ```
   - Generate with password manager
   - Minimum 16 characters
   - Mix of letters, numbers, symbols

2. **Use dedicated Frps server:**
   - Don't share server with untrusted users
   - Keep server software updated
   - Monitor server logs

3. **Restrict remote_port:**
   - Use non-standard port (not 5000)
   - Configure firewall rules on Frps server

---

### 9. Forwarding Rule Security

**Create secure forwarding rules:**

1. **Filter sensitive senders:**
   - Don't forward bank OTPs to insecure services
   - Create blacklist rules for 2FA messages
   - Use separate rules for important vs. spam

2. **Sanitize message content:**
   - Use regex replace to remove sensitive data
   - Example: Hide full card numbers, keep last 4 digits

3. **Limit forwarding targets:**
   - Only forward to services you trust
   - Review terms of service for privacy policies

---

### 10. Monitoring & Auditing

**Regularly review your setup:**

- ✅ **Weekly:** Check forwarding logs for anomalies
- ✅ **Monthly:** Review enabled forwarding rules
- ✅ **Monthly:** Audit API keys and tokens
- ✅ **Quarterly:** Review app permissions
- ✅ **Quarterly:** Rotate sensitive credentials

**Signs of compromise:**
- Unexpected forwarding failures
- Unknown forwarding rules
- Unusual battery drain
- Unfamiliar API configurations

---

## 🚨 Security Incident Response

**If you suspect your configuration is compromised:**

1. **Immediate Actions:**
   - Disable all forwarding rules
   - Stop HTTP server service
   - Revoke all API keys/tokens
   - Change device lock screen password

2. **Investigation:**
   - Review app logs for unauthorized access
   - Check forwarding history for unusual activity
   - Review device permissions

3. **Recovery:**
   - Reset app configuration
   - Generate new API keys
   - Update all passwords
   - Re-enable forwarding with new credentials

4. **Prevention:**
   - Enable server authentication
   - Use HTTPS only
   - Review this security guide

---

## 📋 Security Checklist

Use this checklist to verify your setup:

### Initial Setup
- [ ] Device has screen lock enabled
- [ ] Device encryption is enabled
- [ ] Strong app backup password set
- [ ] Only necessary permissions granted

### Server Configuration
- [ ] Server sign key enabled (if using HTTP server)
- [ ] Strong sign key configured (16+ characters)
- [ ] Server not exposed to public internet
- [ ] Using Frpc with strong token (if remote access needed)

### Forwarding Configuration
- [ ] All forwarding URLs use HTTPS
- [ ] API keys are unique and strong
- [ ] Sensitive senders filtered appropriately
- [ ] No API keys shared or posted publicly

### Maintenance
- [ ] App is up to date
- [ ] Android security patches installed
- [ ] API keys reviewed in last 90 days
- [ ] Backup stored securely
- [ ] Logs reviewed for anomalies

---

## 🔗 Additional Resources

- **OWASP Mobile Security:** https://owasp.org/www-project-mobile-security-testing-guide/
- **Android Security Guide:** https://developer.android.com/training/best-security-practices
- **Password Manager:** Use 1Password, Bitwarden, or KeePassXC for credential management

---

## 📞 Reporting Security Issues

If you discover a security vulnerability in SmsForwarder:

1. **Do NOT post in public issues**
2. Contact the maintainers privately via GitHub Security Advisory
3. Include:
   - Description of vulnerability
   - Steps to reproduce
   - Potential impact
   - Suggested fix (if known)

---

**Last Updated:** 2026-01-17  
**Version:** 1.0

---

## FAQ

### Q: Is it safe to forward SMS to third-party services?
**A:** Only if you trust the service provider and they use HTTPS. Review their privacy policy. Never forward sensitive messages (bank OTPs, 2FA codes) to untrusted services.

### Q: Can someone intercept my SMS messages?
**A:** If you use HTTPS for all forwarding URLs and enable server authentication, interception risk is minimal. HTTP connections are vulnerable to interception.

### Q: What happens if my API key is leaked?
**A:** Immediately revoke the key in the service provider's dashboard. Generate a new key and update SmsForwarder configuration. Review forwarding logs for unauthorized access.

### Q: Should I use the HTTP server feature?
**A:** Only if you need it. If using it:
- Enable server sign key authentication
- Use Frpc/VPN for remote access (don't expose directly to internet)
- Monitor access logs regularly

### Q: How do I backup my configuration securely?
**A:** Use the app's built-in backup feature with encryption enabled. Store the encrypted backup in a secure location (encrypted cloud storage or offline encrypted USB). Never store unencrypted backups in public folders.

### Q: Is my SMS data stored encrypted?
**A:** The app database uses Android's default protection (app sandboxing). For additional security, enable Android device encryption in your device settings. This encrypts all app data including SMS messages.

---

**Remember: Security is a process, not a one-time setup. Regularly review and update your configuration.**
