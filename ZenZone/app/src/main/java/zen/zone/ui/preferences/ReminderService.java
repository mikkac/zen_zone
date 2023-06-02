package zen.zone.ui.preferences;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.Calendar;

import zen.zone.MainActivity;
import zen.zone.R;

public class ReminderService extends Service {

    private static final int REMINDER_NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "reminder_channel";
    private static final String TAG = ReminderService.class.getName();

    private PendingIntent pendingIntent;
    private AlarmManager alarmManager;
    private SharedPreferences sharedPreferences;
    private NotificationManager notificationManager;

    @Override
    public void onCreate() {
        super.onCreate();
        sharedPreferences = getSharedPreferences("ReminderPrefs", Context.MODE_PRIVATE);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        notificationManager = getSystemService(NotificationManager.class);

        createNotificationChannel();

        Intent intent = new Intent(this, ReminderService.class);
        pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        showForegroundNotification();
        triggerReminders();
        return START_STICKY;
    }

    private void showForegroundNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setContentTitle("Meditation Reminder")
                .setContentText("TIME TO GET INTO ZEN ZONE")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Notification notification = builder.build();
        startForeground(REMINDER_NOTIFICATION_ID, notification);
    }

    private void triggerReminders() {
        String selectedDaysString = sharedPreferences.getString("selectedDays", "");
        String[] selectedDays = selectedDaysString.split(",");

        String selectedTime = sharedPreferences.getString("selectedTime", "");

        Log.i(TAG, "Selected days: " + selectedDaysString + " & selectedTime: " + selectedTime);
        Calendar calendar = Calendar.getInstance();
        int currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute = calendar.get(Calendar.MINUTE);

        for (String day : selectedDays) {
            int reminderDay = getReminderDayOfWeek(day);

            if (currentDayOfWeek == reminderDay && selectedTimeValid(selectedTime, currentHour, currentMinute)) {
                showNotification();
                break;
            }
        }

        scheduleNextReminder();
    }

    private int getReminderDayOfWeek(String day) {
        switch (day) {
            case "Pn":
            case "M":
                return Calendar.MONDAY;
            case "Wt":
            case "T":
                return Calendar.TUESDAY;
            case "Śr":
            case "W":
                return Calendar.WEDNESDAY;
            case "Czw":
            case "Th":
                return Calendar.THURSDAY;
            case "Pt":
            case "F":
                return Calendar.FRIDAY;
            case "Sob":
            case "S":
                return Calendar.SATURDAY;
            case "Nd":
            case "Su":
                return Calendar.SUNDAY;
            default:
                return -1;
        }
    }

    private boolean selectedTimeValid(String selectedTime, int currentHour, int currentMinute) {
        String[] timeParts = selectedTime.split(":");
        int selectedHour = Integer.parseInt(timeParts[0]);
        int selectedMinute = Integer.parseInt(timeParts[1]);

        return (selectedHour > currentHour || (selectedHour == currentHour && selectedMinute > currentMinute));
    }

    private void showNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setContentTitle("Reminder")
                .setContentText("It's time for your reminder!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        builder.setContentIntent(contentIntent);

        notificationManager.notify(REMINDER_NOTIFICATION_ID, builder.build());
    }

    private void scheduleNextReminder() {
        Calendar calendar = Calendar.getInstance();
        int currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute = calendar.get(Calendar.MINUTE);

        String selectedTime = sharedPreferences.getString("selectedTime", "");

        int nextDay = getNextReminderDay(currentDayOfWeek, selectedTime, currentHour, currentMinute);

        if (nextDay != -1) {
            calendar.set(Calendar.DAY_OF_WEEK, nextDay);
            String[] timeParts = selectedTime.split(":");
            int selectedHour = Integer.parseInt(timeParts[0]);
            int selectedMinute = Integer.parseInt(timeParts[1]);
            calendar.set(Calendar.HOUR_OF_DAY, selectedHour);
            calendar.set(Calendar.MINUTE, selectedMinute);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }

    private int getNextReminderDay(int currentDayOfWeek, String selectedTime, int currentHour, int currentMinute) {
        String[] timeParts = selectedTime.split(":");
        int selectedHour = Integer.parseInt(timeParts[0]);
        int selectedMinute = Integer.parseInt(timeParts[1]);

        if (currentHour < selectedHour || (currentHour == selectedHour && currentMinute < selectedMinute)) {
            int selectedDay = getReminderDayOfWeek(sharedPreferences.getString("selectedDays", "").split(",")[0]);
            if (selectedDay != -1 && selectedDay >= currentDayOfWeek) {
                return selectedDay;
            }
        }

        for (String day : sharedPreferences.getString("selectedDays", "").split(",")) {
            int reminderDay = getReminderDayOfWeek(day);
            if (reminderDay != -1 && reminderDay > currentDayOfWeek) {
                return reminderDay;
            }
        }

        return -1;
    }

    private void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Reminder Service",
                NotificationManager.IMPORTANCE_LOW
        );
        notificationManager.createNotificationChannel(channel);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
