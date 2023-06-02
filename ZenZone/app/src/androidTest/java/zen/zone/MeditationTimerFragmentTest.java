package zen.zone;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import android.os.CountDownTimer;
import android.view.View;

import zen.zone.ui.meditation.MeditationTimerFragment;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class MeditationTimerFragmentTest {

    @Mock
    private CountDownTimer countDownTimerMock;

    @Before
    public void setUp() {
        // Create a mock NavController
        NavController mockNavController = mock(NavController.class);

        // Use FragmentScenario to create and launch the fragment
        FragmentScenario<MeditationTimerFragment> fragmentScenario = FragmentScenario.launchInContainer(MeditationTimerFragment.class);

        // Set the NavController mock to the fragment
        fragmentScenario.onFragment(fragment ->
                Navigation.setViewNavController(fragment.getView(), mockNavController));
    }

    @Test
    public void testStartMeditationTimer() {
        // Given
        long millisUntilFinished = 5 * 60 * 1000;

        // When
        // You need to call the method on the fragment that is running in the FragmentScenario
        fragmentScenario.onFragment(fragment -> fragment.startMeditationTimer(millisUntilFinished));

        // Then
        verify(countDownTimerMock).start();
    }

    @Test
    public void testStopMeditationAndReturnToSettings() {
        // When
        meditationTimerFragment.stopMeditationAndReturnToSettings();

        // Then
        Mockito.verify(countDownTimerMock).cancel();
    }

    @Test
    public void testOnStop() {
        // When
        meditationTimerFragment.onStop();

        // Then
        Mockito.verify(countDownTimerMock).cancel();
    }
}
