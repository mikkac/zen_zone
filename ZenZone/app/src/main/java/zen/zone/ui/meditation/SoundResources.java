package zen.zone.ui.meditation;

import zen.zone.R;

import java.util.HashMap;
import java.util.Map;

/**
 * The SoundResources class is responsible for mapping sound names to their corresponding sound resources.
 * It maps sound names that are displayed to the user to the corresponding sound files.
 */
public class SoundResources {

    /**
     * A map storing sound names (keys) and corresponding resources (values).
     */
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

    /**
     * Returns the sound resource corresponding to a given sound name.
     *
     * @param soundName The name of the sound for which the resource is to be returned.
     * @return The sound resource corresponding to the sound name, or 0 if no corresponding resource is found.
     */
    public static int getSoundResource(String soundName) {
        Integer soundResource = soundMap.get(soundName);
        return soundResource != null ? soundResource : 0;
    }
}
