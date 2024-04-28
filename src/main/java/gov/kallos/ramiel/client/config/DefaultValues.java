package gov.kallos.ramiel.client.config;

import gov.kallos.ramiel.client.model.Standing;

/**
 * Default Values class, used by the config to quickly pull the default values of certain configuration options.
 */
public class DefaultValues {
    //In minutes.
    public static final int DEFAULT_DISAPPEAR_TIME = 10;

    public static final RGBValue DEFAULT_FRIENDLY_COLOUR = new RGBValue(
            Standing.FRIENDLY.r, Standing.FRIENDLY.g, Standing.FRIENDLY.b
    );

    public static final RGBValue DEFAULT_NEUTRAL_COLOUR = new RGBValue(
            Standing.NEUTRAL.r, Standing.NEUTRAL.g, Standing.NEUTRAL.b
    );

    public static final RGBValue DEFAULT_ENEMY_COLOUR = new RGBValue(
            Standing.ENEMY.r, Standing.ENEMY.g, Standing.ENEMY.b
    );

}
