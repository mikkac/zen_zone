package zen.zone.ui.meditation;

import zen.zone.R;

import java.util.HashMap;
import java.util.Map;

public class SoundResources {

    private static final Map<String, Integer> soundMap = new HashMap<String, Integer>() {{
        put("None", 0);
        // English
        put("Birds", R.raw.birds);
        put("Rain", R.raw.rain);
        put("Chimes", R.raw.chimes);
        // Polish
        put("Ptaki", R.raw.birds);
        put("Deszcz", R.raw.rain);
        put("Dzwonki", R.raw.chimes);
    }};

    public static int getSoundResource(String soundName) {
        Integer soundResource = soundMap.get(soundName);
        return soundResource != null ? soundResource : 0;
    }
}
