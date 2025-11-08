package com.domain.filter;

import android.net.VpnService;
import android.os.ParcelFileDescriptor;
import android.content.Intent;
import android.os.Build;

public class MyVpnService extends VpnService {

    // טוען את ספריית ה-C/C++ native
    static {
        System.loadLibrary("vpnrelay");
    }

    // הצהרות לפונקציות ה-native ב-C/C++
    //public native void nativeStart(int tunFd);
    public native void nativeStart(int fd, MyVpnService vpn, int sdk);
    
    public native void nativeStop();

    // משתנים לשמירת מצב ממשק ה-TUN וה-thread של ה-VPN
    private ParcelFileDescriptor tunInterface;
    private Thread vpnThread;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // רישום לוג לתחילת השירות
        LogUtil.logToFile("onStartCommand: " + (intent != null ? intent.getAction() : "null"));

        // טיפול בפקודת עצירה
        if (intent != null && "STOP_VPN".equals(intent.getAction())) {
            onDestroy(); // קורא לפונקציית הניקוי
            stopSelf();  // עוצר את השירות
            return START_NOT_STICKY; // לא מנסה להפעיל מחדש אם נהרג
        }

        // אם ה-VPN כבר רץ, אל תתחיל אותו שוב
        if (vpnThread != null && vpnThread.isAlive()) {
            LogUtil.logToFile("VPN already running");
            return START_STICKY; // נסה להפעיל מחדש אם נהרג ע"י המערכת
        }

        // בניית ממשק ה-TUN
        Builder builder = new Builder();
        builder.setSession("MyVPN"); // שם הסשן שיוצג למשתמש

        // הגדרת MTU - חשוב להתאמת גודל החבילות
        // ערך של 1500 הוא סטנדרטי עבור רשתות Ethernet, אבל עבור VPNs
        // יש לקחת בחשבון את תוספת הכותרות של ה-VPN, לכן 1400-1420 יכול להיות בטוח יותר
        // ערך של 1400 מתאים ל-MSS של 1360 ב-TCP (20 בתים לכותרת TCP, 20 בתים לכותרת IP)
        // אם ה-C code מצפה MSS מסוים, נתאים את ה-MTU כאן.
        // נשתמש ב-1500 ונניח שה-C code יטפל ב-MSS claming/adjusting.
        builder.setMtu(1500); 

        // הגדרת כתובת IP מקומית לממשק ה-TUN (IPv4)
        builder.addAddress("10.0.0.2", 32); 
        // הגדרת נתיב ברירת מחדל לתעבורת IPv4 דרך ה-TUN
        builder.addRoute("0.0.0.0", 0);

        // *** חדש: הוספת הגדרות IPv6 ***
        // הגדרת כתובת IP מקומית לממשק ה-TUN (IPv6)
        builder.addAddress("fd00:1:1:1::1", 120); 
        // הגדרת נתיב ברירת מחדל לתעבורת IPv6 דרך ה-TUN
        builder.addRoute("::", 0); 

        // *** חדש: הוספת שרתי DNS ***
        // שרתי DNS ציבוריים (של גוגל) עבור IPv4
        builder.addDnsServer("8.8.8.8");
        builder.addDnsServer("8.8.4.4");
        // שרתי DNS ציבוריים (של גוגל) עבור IPv6
        builder.addDnsServer("2001:4860:4860::8888");
        builder.addDnsServer("2001:4860:4860::8844");


        // הקמת ממשק ה-TUN
        tunInterface = builder.establish();
        if (tunInterface != null) {
            LogUtil.logToFile("TUN interface established successfully.");
            // יצירת והפעלת thread חדש להרצת קוד ה-native
            vpnThread = new Thread(new Runnable() {
                    public void run() {
                        // העברת ה-File Descriptor של ה-TUN לקוד ה-native
                        nativeStart(tunInterface.getFd(), MyVpnService. this, Build.VERSION.SDK_INT);
                    }
                });
            vpnThread.start();
        } else {
            LogUtil.logToFile("Failed to establish TUN interface");
            // אם ממשק ה-TUN לא הוקם, ניתן לנסות להציג הודעה למשתמש או לעצור את השירות
            stopSelf();
        }

        return START_STICKY; // נסה להפעיל מחדש אם נהרג ע"י המערכת
    }

    @Override
    public void onDestroy() {
        LogUtil.logToFile("onDestroy called");
        nativeStop(); // קורא לפונקציית העצירה ב-native
        try {
            // ממתין שה-thread של ה-VPN יסיים (עד שנייה אחת)
            if (vpnThread != null) vpnThread.join(1000);
            // סוגר את ממשק ה-TUN
            if (tunInterface != null) tunInterface.close();

            // מאפס את המשתנים לאחר סגירה וסיום
            vpnThread = null;
            tunInterface = null;
        } catch (Exception e) {
            LogUtil.logToFile("Error in onDestroy: " + e.toString());
        }
        super.onDestroy();
    }
}

