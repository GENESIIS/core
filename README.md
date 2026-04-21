# Commands

Run these commands in PowerShell from `C:\genesiis-otp`.

```powershell
cd C:\genesiis-otp
```

```powershell
New-Item -ItemType Directory -Force out\OtpGen
```

```powershell
New-Item -ItemType Directory -Force out\app
```

```powershell
C:\JDKSE2135X64\bin\javac.exe -d out\OtpGen src\com\genesiis\core\security\auth\tfa\OtpGen.java
```

```powershell
C:\JDKSE2135X64\bin\jar.exe --create --file OtpGen.jar -C out\OtpGen .
```

```powershell
C:\JDKSE2135X64\bin\javac.exe --module-path OtpGen.jar --add-modules OtpGen -d out\app src\SendEmailApp.java
```

```powershell
C:\JDKSE2135X64\bin\jar.exe --create --file mailtrap-email.jar --main-class SendEmailApp -C out\app .
```

```powershell
C:\JDKSE2135X64\bin\java.exe --module-path OtpGen.jar --add-modules OtpGen -jar mailtrap-email.jar
```

Expected output:

```text
Email sent to Mailtrap with OTP: 123456
```
