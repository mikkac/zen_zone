package zen.zone.ui.meditation;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import java.util.Locale;

import zen.zone.R;

/**
 * A simple {@link Fragment} subclass representing the Meditation settings screen.
 * Use the {@link MeditationPreferencesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MeditationPreferencesFragment extends Fragment {

    public MeditationPreferencesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment.
     *
     * @return A new instance of fragment MeditationFragment.
     */
    public static MeditationPreferencesFragment newInstance() {
        return new MeditationPreferencesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_meditation_preferences, container, false);

        SeekBar sbMeditationDuration = view.findViewById(R.id.seekBar_meditation_length);
        final TextView tvMeditationDurationValue = view.findViewById(R.id.tv_meditation_length_value);
        sbMeditationDuration.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvMeditationDurationValue.setText(String.format(Locale.getDefault(), "%d %s", progress + 5, getString(R.string.minutes)));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });


        Button startMeditationButton = view.findViewById(R.id.btn_start_meditation);
        startMeditationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get values from UI elements
                SeekBar meditationDurationSeekBar = view.findViewById(R.id.seekBar_meditation_length);
                long meditationDurationMillis = (long) (meditationDurationSeekBar.getProgress() + 5) * 60 * 1000;

                CheckBox halfTimeNotificationCheckBox = view.findViewById(R.id.cb_half_time_notification);
                boolean halfTimeNotification = halfTimeNotificationCheckBox.isChecked();

                Spinner backgroundSoundSpinner = view.findViewById(R.id.sp_background_sound);
                String backgroundSound = backgroundSoundSpinner.getSelectedItem() != null ? backgroundSoundSpinner.getSelectedItem().toString() : "None";

                // Pass the meditation settings as arguments to the TimerFragment
                Bundle args = new Bundle();
                args.putLong("meditationDurationMillis", meditationDurationMillis);
                args.putBoolean("halfTimeNotification", halfTimeNotification);
                args.putString("backgroundSound", backgroundSound);

                // Navigate to TimerFragment with the meditation settings as arguments
                NavHostFragment.findNavController(MeditationPreferencesFragment.this)
                        .navigate(R.id.action_meditationFragment_to_timerFragment, args);
            }
        });


        return view;
    }
}
