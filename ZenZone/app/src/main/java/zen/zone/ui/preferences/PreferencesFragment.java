package zen.zone.ui.preferences;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;

import zen.zone.R;
import zen.zone.databinding.FragmentPreferencesBinding;

public class PreferencesFragment extends Fragment {

    private FragmentPreferencesBinding binding;
    private CheckBox[] dayCheckBoxes;
    private EditText timeEditText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("SetTextI18n")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_preferences, container, false);

        PreferencesViewModel preferencesViewModel =
                new ViewModelProvider(this).get(PreferencesViewModel.class);

        binding = FragmentPreferencesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textPreferences;
        preferencesViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        TextView preferencesText = view.findViewById(R.id.text_preferences);
        preferencesText.setText("Preferences");
        timeEditText = view.findViewById(R.id.text_reminder);
        timeEditText.setText("16:00");
        CheckBox monday = view.findViewById(R.id.checkBox_monday);
        monday.setText("M");
        CheckBox tuesday = view.findViewById(R.id.checkBox_tuesday);
        tuesday.setText("T");
        CheckBox wednesday = view.findViewById(R.id.checkBox_wednesday);
        wednesday.setText("W");
        CheckBox thursday = view.findViewById(R.id.checkBox_thursday);
        thursday.setText("Th");
        CheckBox friday = view.findViewById(R.id.checkBox_friday);
        friday.setText("F");
        CheckBox saturday = view.findViewById(R.id.checkBox_saturday);
        saturday.setText("S");
        CheckBox sunday = view.findViewById(R.id.checkBox_sunday);
        sunday.setText("Su");

        dayCheckBoxes = new CheckBox[] {monday, tuesday, wednesday, thursday, friday, saturday, sunday};
        Button reminderButton = view.findViewById(R.id.button_reminder);
        reminderButton.setOnClickListener(v -> saveReminderSettings());

        return view;
    }

    private void saveReminderSettings() {
        List<String> selectedDays = new ArrayList<>();
        for (CheckBox checkBox : dayCheckBoxes) {
            if(checkBox.isChecked()) {
                selectedDays.add(checkBox.getText().toString());
            }
        }
        String time = timeEditText.getText().toString();

        if(TimeValidator.isValidTime(time)) {
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
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}