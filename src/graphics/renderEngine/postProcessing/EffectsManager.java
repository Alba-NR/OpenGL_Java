package graphics.renderEngine.postProcessing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Manages effects, especially association btwn effects and an int id to identify an effect.
 */
public class EffectsManager {
    private static List<PostProcessingEffect> mapIntIDtoEffect; // list serves as a map btwn int ids (indeces) & effects (values)
    private static boolean mapIsInitialised = false;
    private static int numOfEffects = -1;

    /**
     * Returns the effect associated w/the given int id.
     * @param id int id number of an effect
     * @return {@link PostProcessingEffect} to which that id belongs
     */
    public static PostProcessingEffect getEffectByIntID(int id){
        if(!mapIsInitialised) initialiseMap();

        if(id >= 0 && id < mapIntIDtoEffect.size()) return mapIntIDtoEffect.get(id);
        else return PostProcessingEffect.NONE;
    }

    /**
     * Returns the int id for the given effect.
     * @param effect {@link PostProcessingEffect} for which to get int id
     * @return int id for given effect
     */
    public static int getEffectIntID(PostProcessingEffect effect){
        if(!mapIsInitialised) initialiseMap();

        return mapIntIDtoEffect.indexOf(effect);
    }

    /**
     * Initialises int id to effects map.
     */
    private static void initialiseMap(){
        PostProcessingEffect[] effects = PostProcessingEffect.values();
        mapIntIDtoEffect = new ArrayList<>((Arrays.asList(effects)));
        mapIsInitialised = true;
    }

    public static int getNumOfEffects() {
        if(numOfEffects == -1) numOfEffects = PostProcessingEffect.values().length;
        return numOfEffects;
    }
}
