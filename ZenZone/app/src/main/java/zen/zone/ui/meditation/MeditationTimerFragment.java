package zen.zone.ui.meditation;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import java.util.Locale;

import zen.zone.R;

public class MeditationTimerFragment extends Fragment {

    private TextView tvTimeRemaining;
    private Button btnPause;
    private LinearLayout llPausedControls;
    private CountDownTimer meditationTimer;
    private long timeLeftInMillis;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_meditation_timer, container, false);

        tvTimeRemaining = view.findViewById(R.id.tv_time_remaining);
        btnPause = view.findViewById(R.id.btn_pause);
        llPausedControls = view.findViewById(R.id.ll_paused_controls);
        Button btnPlay = view.findViewById(R.id.btn_play);
        Button btnStop = view.findViewById(R.id.btn_stop);

        assert getArguments() != null;
        timeLeftInMillis = getArguments().getLong("meditationDurationMillis", 5 * 60 * 1000);
        startMeditationTimer(timeLeftInMillis);

        btnPause.setOnClickListener(v -> {
            meditationTimer.cancel();
            btnPause.setVisibility(View.GONE);
            llPausedControls.setVisibility(View.VISIBLE);
        });

        btnPlay.setOnClickListener(v -> {
            startMeditationTimer(timeLeftInMillis);
            btnPause.setVisibility(View.VISIBLE);
            llPausedControls.setVisibility(View.GONE);
        });

        btnStop.setOnClickListener(v -> stopMeditationAndReturnToSettings());

        // Hide ActionBar
        if (getActivity() != null) {
            ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (actionBar != null) {
                actionBar.hide();
            }
        }

        return view;
    }

    private void startMeditationTimer(long millisUntilFinished) {
        meditationTimer = new CountDownTimer(millisUntilFinished, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                int minutes = (int) (millisUntilFinished / 1000) / 60;
                int seconds = (int) (millisUntilFinished / 1000) % 60;
                String timeRemaining = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
                tvTimeRemaining.setText(timeRemaining);
            }

            @Override
            public void onFinish() {
                // Notify the user that the meditation has ended
                // Return to the meditation settings screen (SettingsFragment)
                stopMeditationAndReturnToSettings();
            }
        }.start();
    }

    public void stopMeditationAndReturnToSettings() {
        if (meditationTimer != null) {
            meditationTimer.cancel();
        }
        Navigation.findNavController(getView()).navigate(R.id.navigation_meditation);

        // Show ActionBar
        if (getActivity() != null) {
            ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        stopMeditationAndReturnToSettings();
    }
}
