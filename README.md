# OTP Mailtrap App

This application generates a 6-digit OTP and sends it by email through Mailtrap SMTP using JavaMail (`jakarta.mail`).

## How It Works

1. `OtpGen.genarate()` creates a random 6-digit OTP.
2. `SendEmailApp` reads Mailtrap SMTP settings from hardcoded values inside `src\SendEmailApp.java`.
3. JavaMail opens the SMTP connection, starts TLS, authenticates, and sends the message.
4. The app prints the OTP to the console after a successful send.

## Files

- `src\com\genesiis\core\security\auth\tfa\OtpGen.java` generates the OTP.
- `src\SendEmailApp.java` creates and sends the email with JavaMail.
- `pom.xml` declares the single JavaMail dependency and the run target.

## Update Mailtrap Creds

Open `src\SendEmailApp.java` and update the hardcoded values before running the app.

Required values:

- `DEFAULT_USERNAME` with your Mailtrap username
- `DEFAULT_PASSWORD` with your Mailtrap password
- `DEFAULT_FROM` with your sender email
- `DEFAULT_TO` with your receiver email

Optional values:

- `DEFAULT_HOST` defaults to `sandbox.smtp.mailtrap.io`
- `DEFAULT_PORT` defaults to `2525`
- `DEFAULT_SUBJECT` defaults to `Your OTP Code`
- `DEFAULT_BODY_PREFIX` defaults to `Your OTP is: `

## Build And Run

Build with Maven:

```powershell
mvn compile
```

Run with Maven:

```powershell
mvn exec:java
```

Expected output:

```text
Email sent to Mailtrap with OTP: 123456
```
