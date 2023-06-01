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
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import zen.zone.R;
import zen.zone.databinding.FragmentPreferencesBinding;

public class PreferencesFragment extends Fragment {

    private FragmentPreferencesBinding binding;
    private CheckBox[] dayCheckBoxes;
    private EditText timeEditText;
    private AdView adView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("SetTextI18n")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_preferences, container, false);

        loadLanguageAndCreateChangeListener(view);
        loadThemeAndCreateChangeListener(view);
        loadRemindersAndCreateChangeListener(view);

        createAds(view);

        return view;
    }

    private void restartFragment() {
        requireActivity().recreate();
    }

    private void saveReminderSettings() {
        List<String> selectedDays = new ArrayList<>();
        for (CheckBox checkBox : dayCheckBoxes) {
            if (checkBox.isChecked()) {
                selectedDays.add(checkBox.getText().toString());
            }
        }
        String time = timeEditText.getText().toString();

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

    @Override
    public void onPause() {
        if (adView != null) {
            adView.pause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adView != null) {
            adView.resume();
        }
    }

    @Override
    public void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }

    private void changeLanguage(String languageCode) {
        Locale locale = new Locale(languageCode);
        Resources resources = getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(locale);
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());

        restartFragment();
    }

    private void loadLanguageAndCreateChangeListener(View view) {
        SharedPreferences sharedPref = getActivity().getSharedPreferences("LanguagePref", Context.MODE_PRIVATE);

        ImageButton plButton = view.findViewById(R.id.imageButton_pl);
        plButton.setOnClickListener(v -> {
            changeLanguage("pl");
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("language", "pl");
            editor.apply();
        });

        ImageButton engButton = view.findViewById(R.id.imageButton_gb);
        engButton.setOnClickListener(v -> {
            changeLanguage("en");
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("language", "en");
            editor.apply();
        });
    }

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

    private void loadRemindersAndCreateChangeListener(View view) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("ReminderPrefs", Context.MODE_PRIVATE);

        String selectedDaysString = sharedPreferences.getString("selectedDays", "");
        String[] selectedDaysArray = selectedDaysString.split(",");
        List<String> selectedDays = Arrays.asList(selectedDaysArray);  // Konwersja tablicy do listy

        String selectedTime = sharedPreferences.getString("selectedTime", "");

        timeEditText = view.findViewById(R.id.text_reminder);
        timeEditText.setText(selectedTime);  // Ustawienie wybranego czasu

        CheckBox monday = view.findViewById(R.id.checkBox_monday);
        monday.setChecked(selectedDays.contains(monday.getText().toString()));  // Ustawienie statusu CheckBoxa

        CheckBox tuesday = view.findViewById(R.id.checkBox_tuesday);
        tuesday.setChecked(selectedDays.contains(tuesday.getText().toString()));

        CheckBox wednesday = view.findViewById(R.id.checkBox_wednesday);
        wednesday.setChecked(selectedDays.contains(wednesday.getText().toString()));

        CheckBox thursday = view.findViewById(R.id.checkBox_thursday);
        thursday.setChecked(selectedDays.contains(thursday.getText().toString()));

        CheckBox friday = view.findViewById(R.id.checkBox_friday);
        friday.setChecked(selectedDays.contains(friday.getText().toString()));

        CheckBox saturday = view.findViewById(R.id.checkBox_saturday);
        saturday.setChecked(selectedDays.contains(saturday.getText().toString()));

        CheckBox sunday = view.findViewById(R.id.checkBox_sunday);
        sunday.setChecked(selectedDays.contains(sunday.getText().toString()));

        dayCheckBoxes = new CheckBox[]{monday, tuesday, wednesday, thursday, friday, saturday, sunday};
        Button reminderButton = view.findViewById(R.id.button_reminder);
        reminderButton.setOnClickListener(v -> saveReminderSettings());
    }

    private void createAds(View view) {
        MobileAds.initialize(requireContext(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        adView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }
}