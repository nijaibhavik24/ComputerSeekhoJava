# Email Troubleshooting Guide - ComputerSeekho

## Common Email Issues and Solutions

### 1. **Gmail Security Settings (Most Common Issue)**

**Problem**: Gmail blocks login attempts from "less secure apps"

**Solution**: Use App Passwords instead of your regular password

#### Steps to Create Gmail App Password:

1. **Enable 2-Factor Authentication**:
   - Go to [Google Account Settings](https://myaccount.google.com/)
   - Click on "Security"
   - Enable "2-Step Verification"

2. **Generate App Password**:
   - Go to [App Passwords](https://myaccount.google.com/apppasswords)
   - Select "Mail" and "Other (Custom name)"
   - Enter "ComputerSeekho" as the name
   - Click "Generate"
   - Copy the 16-character password

3. **Update your application.properties**:
   ```properties
   spring.mail.password=YOUR_16_CHAR_APP_PASSWORD
   ```

### 2. **Check Application Logs**

Look for these error messages in your console:

- `535-5.7.8 Username and Password not accepted`
- `Authentication failed`
- `Connection timeout`
- `SSL/TLS required`

### 3. **Test Email Configuration**

Use the provided test endpoints:

#### Simple Test:
```bash
POST /api/email/simple-test
```

#### Custom Test:
```bash
POST /api/email/test
Content-Type: application/json

{
  "to": "your-email@gmail.com",
  "subject": "Test Email",
  "message": "This is a test"
}
```

### 4. **Verify Configuration**

Ensure your `application.properties` has:

```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=gwaliorkt09@gmail.com
spring.mail.password=YOUR_APP_PASSWORD
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true
spring.mail.properties.mail.smtp.ssl.trust=smtp.gmail.com
```

### 5. **Network/Firewall Issues**

- Check if port 587 is blocked by your firewall
- Try using port 465 with SSL instead:
  ```properties
  spring.mail.port=465
  spring.mail.properties.mail.smtp.ssl.enable=true
  ```

### 6. **Gmail Account Issues**

- Ensure your Gmail account is not suspended
- Check if you've exceeded Gmail sending limits (500/day for regular accounts)
- Verify your account doesn't have any security holds

### 7. **Alternative Email Providers**

If Gmail continues to have issues, consider:

#### Outlook/Hotmail:
```properties
spring.mail.host=smtp-mail.outlook.com
spring.mail.port=587
spring.mail.username=your-email@outlook.com
spring.mail.password=your-password
```

#### Yahoo:
```properties
spring.mail.host=smtp.mail.yahoo.com
spring.mail.port=587
spring.mail.username=your-email@yahoo.com
spring.mail.password=your-app-password
```

### 8. **Testing Steps**

1. **Start your application**
2. **Open `email-test.html` in your browser**
3. **Try the "Send Simple Test" button first**
4. **Check console logs for detailed error messages**
5. **Verify the email arrives in your inbox**

### 9. **Debug Mode**

The configuration includes debug mode. Check your console for detailed SMTP communication logs.

### 10. **Common Error Messages and Solutions**

| Error | Solution |
|-------|----------|
| `535-5.7.8 Username and Password not accepted` | Use App Password, not regular password |
| `Connection timeout` | Check firewall/network settings |
| `SSL/TLS required` | Ensure `starttls.enable=true` |
| `Authentication failed` | Verify username and App Password |
| `Connection refused` | Check if port 587 is open |

### 11. **Quick Fix Checklist**

- [ ] 2-Factor Authentication enabled on Gmail
- [ ] App Password generated and used
- [ ] Port 587 not blocked by firewall
- [ ] Application restarted after config changes
- [ ] Console logs checked for errors
- [ ] Test email sent to yourself first

### 12. **Still Having Issues?**

1. Check the console output when sending emails
2. Verify your Gmail account settings
3. Try sending from a different email provider
4. Check if your hosting provider blocks SMTP traffic

## Contact Support

If you continue to have issues, please provide:
- Console error messages
- Gmail account status
- Network/firewall configuration
- Application logs
