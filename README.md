# Commands

Run these commands in PowerShell from `C:\genesiis-otp`.

```powershell
cd C:\genesiis-otp
```

```powershell
New-Item -ItemType Directory -Force out\otpgen
```

```powershell
New-Item -ItemType Directory -Force out\app
```

```powershell
C:\JDKSE2135X64\bin\javac.exe -d out\otpgen src\com\genesiis\core\security\auth\tfa\otpgen.java
```

```powershell
C:\JDKSE2135X64\bin\jar.exe --create --file otpgen.jar -C out\otpgen .
```

```powershell
C:\JDKSE2135X64\bin\javac.exe --module-path otpgen.jar --add-modules otpgen -d out\app src\SimpleMailtrapEmail.java
```

```powershell
C:\JDKSE2135X64\bin\jar.exe --create --file mailtrap-email.jar --main-class SimpleMailtrapEmail -C out\app .
```

```powershell
C:\JDKSE2135X64\bin\java.exe --module-path otpgen.jar --add-modules otpgen -jar mailtrap-email.jar
```

Expected output:

```text
Email sent to Mailtrap with OTP: 123456
```
