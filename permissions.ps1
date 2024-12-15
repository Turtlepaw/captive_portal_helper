Write-Output "Setting required permissions via ADB"

try {
    & adb shell pm grant com.turtlepaw.wifihelper android.permission.WRITE_SECURE_SETTINGS
    if ($LASTEXITCODE -ne 0) {
        throw "ADB command failed"
    }
} catch {
    Write-Output "Failed to set permissions, run the following command manually:"
    Write-Output "adb shell pm grant com.turtlepaw.wifihelper android.permission.WRITE_SECURE_SETTINGS"
}
