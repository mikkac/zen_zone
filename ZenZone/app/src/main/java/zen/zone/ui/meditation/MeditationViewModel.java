package zen.zone.ui.meditation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MeditationViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public MeditationViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Meditation view");
    }

    public LiveData<String> getText() {
        return mText;
    }
}