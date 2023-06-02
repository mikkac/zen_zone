package zen.zone.ui.preferences;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import zen.zone.R;
import zen.zone.databinding.FragmentPreferencesBinding;


/**
 * The PreferencesFragment class is responsible for managing and rendering
 * user preferences in the ZenZone app. This includes theme selection,
 * language selection, and setting up reminders. The fragment also manages
 * ad displays using the Google Mobile Ads SDK.
 */
public class PreferencesFragment extends Fragment {

    private FragmentPreferencesBinding binding;
    private CheckBox[] dayCheckBoxes;
    private EditText timeEditText;
    private AdView adView;

    /**
     * Android system calls this when creating the fragment. We use this to setup
     * any essential resources needed for the fragment.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * The system calls this when it's time for the fragment to draw its user interface
     * for the first time. In this method, we inflate the layout and setup the view objects.
     */
    @SuppressLint("SetTextI18n")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_preferences, container, false);

        loadThemeAndCreateChangeListener(view);
        loadLanguageAndCreateChangeListener(view);
        loadRemindersAndCreateChangeListener(view);

        createAds(view);

        return view;
    }

    /**
     * This method restarts the activity to apply the changes in user settings like
     * theme or language preference.
     */
    private void restartFragment() {
        requireActivity().recreate();
    }

    /**
     * This method saves the reminder settings configured by the user including the
     * selected days and time for the reminders.
     */
    private void saveReminderSettings() {
        List<String> selectedDays = new ArrayList<>();
        for (CheckBox checkBox : dayCheckBoxes) {
            if (checkBox.isChecked()) {
                selectedDays.add(checkBox.getText().toString());
            }
        }
        String time = timeEditText.getText().toString();

        if (selectedDays.size() == 0) {
            Toast.makeText(requireContext(), "You have to choose at least one day", Toast.LENGTH_LONG).show();
        } else {
            if (TimeValidator.isValidTime(time)) {
                SharedPreferences sharedPreferences = this.requireContext().getSharedPreferences("ReminderPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                StringBuilder daysStringBuilder = new StringBuilder();
                for (String day : selectedDays) {
                    daysStringBuilder.append(day).append(",");
                }
                editor.putString("selectedDays", daysStringBuilder.toString());
                editor.putString("selectedTime", time);
                editor.apply();

                Context context = requireContext();
                Intent intent = new Intent(context, ReminderService.class);
                context.startForegroundService(intent);

                Toast.makeText(getContext(), "Reminders were set-up!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Time format should be HH:MM", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * System calls this method as the first indication that the user is leaving the fragment.
     * Here, we pause the AdView to ensure it doesn't remain active when not in view.
     */
    @Override
    public void onPause() {
        if (adView != null) {
            adView.pause();
        }
        super.onPause();
    }

    /**
     * The system calls this method as the first indication that the user is interacting
     * with the fragment. Here, we resume the AdView if it was previously paused.
     */
    @Override
    public void onResume() {
        super.onResume();
        if (adView != null) {
            adView.resume();
        }
    }

    /**
     * This method is called when the fragment is no longer in use. This is the place
     * to finalise all changes and save to permanent storage if required. Here, we
     * also destroy the AdView.
     */
    @Override
    public void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }

    /**
     * This method changes the language of the app based on the languageCode provided.
     */
    private void changeLanguage(String languageCode) {
        Locale locale = new Locale(languageCode);
        Resources resources = getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(locale);
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());

        restartFragment();
    }

    /**
     * This method loads the language preference from the shared preferences and sets
     * the onClick listeners for the language buttons.
     */
    private void loadLanguageAndCreateChangeListener(View view) {
        SharedPreferences sharedPref = getActivity().getSharedPreferences("LanguagePref", Context.MODE_PRIVATE);

        ImageButton plButton = view.findViewById(R.id.imageButton_pl);
        plButton.setOnClickListener(v -> {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("language", "pl");
            editor.apply();
            changeLanguage("pl");
        });

        ImageButton engButton = view.findViewById(R.id.imageButton_gb);
        engButton.setOnClickListener(v -> {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("language", "en");
            editor.apply();
            changeLanguage("en");
        });
    }

    /**
     * This method loads the theme preference from the shared preferences and sets
     * the onCheckedChangeListener for the theme radio buttons.
     */
    private void loadThemeAndCreateChangeListener(View view) {
        RadioGroup radioGroup = view.findViewById(R.id.radioGroup_motive);
        SharedPreferences sharedPref = getActivity().getSharedPreferences("ThemePref", Context.MODE_PRIVATE);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                SharedPreferences.Editor editor = sharedPref.edit();
                if (checkedId == R.id.radioButton_light) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    editor.putString("theme", "light");
                } else if (checkedId == R.id.radioButton_dark) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    editor.putString("theme", "dark");
                }
                editor.apply();
            }
        });

        String savedTheme = sharedPref.getString("theme", "light"); // default is light theme
        if (savedTheme.equals("light")) {
            radioGroup.check(R.id.radioButton_light);
        } else if (savedTheme.equals("dark")) {
            radioGroup.check(R.id.radioButton_dark);
        }
    }


    /**
     * This method loads the reminder preferences from the shared preferences and sets
     * the onClick listener for the reminder button.
     */
    private void loadRemindersAndCreateChangeListener(View view) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("ReminderPrefs", Context.MODE_PRIVATE);

        String selectedDaysString = sharedPreferences.getString("selectedDays", "");
        String[] selectedDaysArray = selectedDaysString.split(",");
        List<String> selectedDays = Arrays.asList(selectedDaysArray);  // Konwersja tablicy do listy

        String selectedTime = sharedPreferences.getString("selectedTime", "");

        timeEditText = view.findViewById(R.id.text_reminder);
        timeEditText.setText(selectedTime);  // Ustawienie wybranego czasu

        CheckBox monday = view.findViewById(R.id.checkBox_monday);
        monday.setChecked(selectedDays.contains("M") || selectedDays.contains("Pn"));  // Ustawienie statusu CheckBoxa

        CheckBox tuesday = view.findViewById(R.id.checkBox_tuesday);
        tuesday.setChecked(selectedDays.contains("T") || selectedDays.contains("Wt"));

        CheckBox wednesday = view.findViewById(R.id.checkBox_wednesday);
        wednesday.setChecked(selectedDays.contains("W") || selectedDays.contains("Śr"));

        CheckBox thursday = view.findViewById(R.id.checkBox_thursday);
        thursday.setChecked(selectedDays.contains("Th") || selectedDays.contains("Czw"));

        CheckBox friday = view.findViewById(R.id.checkBox_friday);
        friday.setChecked(selectedDays.contains("F") || selectedDays.contains("Pt"));

        CheckBox saturday = view.findViewById(R.id.checkBox_saturday);
        saturday.setChecked(selectedDays.contains("S") || selectedDays.contains("Sob"));

        CheckBox sunday = view.findViewById(R.id.checkBox_sunday);
        sunday.setChecked(selectedDays.contains("Su") || selectedDays.contains("Nd"));

        dayCheckBoxes = new CheckBox[]{monday, tuesday, wednesday, thursday, friday, saturday, sunday};
        Button reminderButton = view.findViewById(R.id.button_reminder);
        reminderButton.setOnClickListener(v -> saveReminderSettings());
    }

    /**
     * This method initializes the MobileAds SDK and creates an AdRequest to load an ad.
     */
    private void createAds(View view) {
        MobileAds.initialize(requireContext(), initializationStatus -> {
        });
        adView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        new RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("FEE379885695322A91C7073603A68825"));
    }
}