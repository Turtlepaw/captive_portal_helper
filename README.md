# 🛜 WiFI Helper

WiFi helper makes it easier to connect to [Captive Portals](https://en.wikipedia.org/wiki/Captive_portal), you can try [3 system behaviors](#behaviors) and quickly launch a DNS server (_to redirect to the captive portal login_) using your default internet app.

## Behaviors

1. **No Detection (recommended):** The system doesn't check if it's a captive portal and won't automatically disconnect
2. **Captive Portal Mode:** The system treats the network as a captive portal and will redirect users to the captive portal page when they attempt to access the internet. (I haven't tested this yet)
3. **Notification Mode:**: In this mode, users will receive a notification about the captive portal, but the system will not automatically redirect them. Instead, they can manually navigate to the portal page if they choose.
4. **`null` (default):** I _assume_ this is **Notification Mode**, which sends a notification, automatically disconnects, so you can't login.

### ⚠️ READ FIRST

Setting a behavior will _by default_ fail, you can enable it by running this ADB command:

```shell
adb shell pm grant com.turtlepaw.wifihelper android.permission.WRITE_SECURE_SETTINGS
```
