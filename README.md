# OTP Mailtrap App

This application generates a 6-digit OTP and sends it by email.

Email sending is implemented with the Factory pattern and supports 2 methods:

- `raw-smtp`: custom SMTP client (no library needed)
- `javamail`: Jakarta Mail (dependencies handled by Maven)

## How It Works

1. `OtpGen.genarate()` creates a random 6-digit OTP.
2. `SendEmailApp` builds the email content + SMTP config from the constants in `src\SendEmailApp.java`.
3. `EmailSenderFactory` selects the sender implementation based on `EMAIL_SENDER_METHOD`.
4. The chosen sender sends the email and the app prints the OTP.

## Switch Email Method

Open `src\SendEmailApp.java` and change:

`EMAIL_SENDER_METHOD = "raw-smtp"`  (or `"javamail"`)

Aliases supported:

- `rawemail` -> `raw-smtp`
- `javaemail` -> `javamail`

## Update SMTP Creds

Open `src\SendEmailApp.java` and update:

- `DEFAULT_USERNAME`, `DEFAULT_PASSWORD`
- `DEFAULT_FROM`, `DEFAULT_TO`
- optional: `DEFAULT_HOST`, `DEFAULT_PORT`, `DEFAULT_SUBJECT`, `DEFAULT_BODY_PREFIX`

## Run (Maven Only)

From PowerShell in `C:\genesiis-otp\core`:

```powershell
cd C:\genesiis-otp\core
mvn "-Dmaven.repo.local=$((Resolve-Path .).Path)\\.m2\\repository" -q compile exec:java
```

First run will download dependencies/plugins from Maven Central (make sure you have internet/proxy configured).
