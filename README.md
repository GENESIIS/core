# OTP Mailtrap App

This application generates a 6-digit OTP and sends it by email through Mailtrap SMTP.

## How It Works

1. `OtpGen.genarate()` creates a random 6-digit OTP.
2. `SendEmailApp` reads your Mailtrap and email settings from the values inside `src\SendEmailApp.java`.
3. The app connects to Mailtrap SMTP.
4. It starts TLS, logs in, sends the email, and prints the OTP in the console.

## Files

- `src\com\genesiis\core\security\auth\tfa\OtpGen.java` generates the OTP.
- `src\SendEmailApp.java` connects to Mailtrap and sends the email.

## Update Mailtrap Creds

Open `src\SendEmailApp.java` and replace the existing values with your own Mailtrap credentials and your email details.

Use your Mailtrap creds for getting the email into your email inbox.

Update these fields:

- `DEFAULT_USERNAME` with your Mailtrap username
- `DEFAULT_PASSWORD` with your Mailtrap password
- `DEFAULT_FROM` with your sender email
- `DEFAULT_TO` with your receiver email inbox

You can also change these if needed:

- `DEFAULT_HOST`
- `DEFAULT_PORT`
- `DEFAULT_SUBJECT`
- `DEFAULT_BODY_PREFIX`

## Compile And Run

Run these commands in PowerShell from `C:\genesiis-otp\core`:

```powershell
cd C:\genesiis-otp\core
```

```powershell
New-Item -ItemType Directory -Force out | Out-Null
```

```powershell
C:\JDKSE2135X64\bin\javac.exe -d out src\com\genesiis\core\security\auth\tfa\OtpGen.java src\SendEmailApp.java
```

```powershell
C:\JDKSE2135X64\bin\java.exe -cp out SendEmailApp
```

Expected output:

```text
Email sent to Mailtrap with OTP: 123456
```
