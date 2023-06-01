package zen.zone;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Locale;

import zen.zone.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences sharedPrefs;
    private static int currentDayStreak;
    private static int longestStreak;
    private ActivityMainBinding binding;

    public static int[] getDaysStreaks() {
        return new int[]{currentDayStreak, longestStreak};
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAppLanguage();
        setAppTheme();
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_meditation, R.id.navigation_stats, R.id.navigation_preferences)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        this.dayStreak();
    }

    private void setAppLanguage() {
        SharedPreferences sharedPref = getSharedPreferences("LanguagePref", Context.MODE_PRIVATE);

        // Read the saved language choice
        String savedLanguage = sharedPref.getString("language", "en"); // default is English

        Locale locale = new Locale(savedLanguage);
        Resources resources = getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(locale);
    }

    private void setAppTheme() {
        SharedPreferences sharedPref = getSharedPreferences("ThemePref", Context.MODE_PRIVATE);

        // Read the saved theme choice
        String savedTheme = sharedPref.getString("theme", "light"); // default is light theme

        // Set the theme at startup
        if (savedTheme.equals("light")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else if (savedTheme.equals("dark")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
    }

    private void dayStreak() {
        // Get current date (from 0:00) in ms
        long todayInMs = LocalDate.now().atStartOfDay().toEpochSecond(ZoneOffset.UTC) * 1000;

        // Get the stored date (in ms) from SharedPreferences
        sharedPrefs = getSharedPreferences("streak", MODE_PRIVATE);
        long storedTime = sharedPrefs.getLong("lastOpenedApp", -1);
        currentDayStreak = sharedPrefs.getInt("streakCounter", 0);

        long dayDifference = todayInMs - storedTime;

        // Check whether today is at least one day from y-day
        if (dayDifference == 86400000) {
            currentDayStreak++;
            Toast.makeText(this.getApplicationContext(),
                    currentDayStreak + " days streak.\nYou are doing great!",
                    Toast.LENGTH_SHORT).show();
        } else {
            currentDayStreak = 1;
        }

        sharedPrefs.edit()
                .putInt("streakCounter", currentDayStreak)
                .putLong("lastOpenedApp", todayInMs)
                .apply();

        checkLongestStreak(currentDayStreak);
    }

    private void checkLongestStreak(int streakNow) {
        sharedPrefs = getSharedPreferences("streak", MODE_PRIVATE);
        longestStreak = sharedPrefs.getInt("longestStreak", 0);

        if (streakNow > longestStreak) {
            longestStreak = streakNow;
            sharedPrefs.edit()
                    .putInt("longestStreak", longestStreak)
                    .apply();
        }
    }

    public void hideBottomNav() {
        binding.navView.setVisibility(View.GONE);
    }

    public void showBottomNav() {
        binding.navView.setVisibility(View.VISIBLE);
    }
}