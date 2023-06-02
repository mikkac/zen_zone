package zen.zone.ui.meditation;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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

    /**
     * The tag used for logging.
     */
    private static final String TAG = MeditationPreferencesFragment.class.getName();

    /**
     * Default constructor.
     */
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

    /**
     * Called when the fragment's activity has been created and this fragment's
     * view hierarchy instantiated. It can be used to do final initialization once these pieces are in place,
     * such as retrieving views or restoring state.
     *
     * @param savedInstanceState If the fragment is being re-created from a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     * This is optional, and non-graphical fragments can return null. This will be called between onCreate(Bundle) and onActivityCreated(Bundle).
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to. The fragment should not add the view itself, but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     *
     * @return Return the View for the fragment's UI, or null.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_meditation_preferences, container, false);

        SeekBar sbMeditationDuration = view.findViewById(R.id.seekBar_meditation_length);
        final TextView tvMeditationDurationValue = view.findViewById(R.id.tv_meditation_length_value);
        sbMeditationDuration.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvMeditationDurationValue.setText(String.format(Locale.getDefault(), "%d %s", progress + 1, getString(R.string.minutes)));
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
                long meditationDurationMillis = (long) (meditationDurationSeekBar.getProgress() + 1) * 60 * 1000;

                CheckBox halfTimeNotificationCheckBox = view.findViewById(R.id.cb_half_time_notification);
                boolean halfTimeNotification = halfTimeNotificationCheckBox.isChecked();

                Spinner backgroundSoundSpinner = view.findViewById(R.id.sp_background_sound);
                String backgroundSound = backgroundSoundSpinner.getSelectedItem() != null ? backgroundSoundSpinner.getSelectedItem().toString() : "None";

                Log.i(TAG, "meditationDurationMillis = " + meditationDurationMillis);
                Log.i(TAG, "halfTimeNotification = " + halfTimeNotification);
                Log.i(TAG, "backgroundSound = " + backgroundSound);
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
