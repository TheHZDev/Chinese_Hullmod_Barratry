package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.Stats;

import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("unchecked")
public class DegradedLifeSupport extends BaseHullMod {

    private static final Set<String> BLOCKED_HULLMODS = new HashSet<>();
    public static float MAX_CREW_MULT = 0.5f;

    static {
        BLOCKED_HULLMODS.add("degraded_life_support2");
    }

    private final String ERROR = "IncompatibleHullmodWarning";

    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
        float effect = stats.getDynamic().getValue(Stats.DMOD_EFFECT_MULT);
        float crewMult = MAX_CREW_MULT + (1f - MAX_CREW_MULT) * (1f - effect);

        stats.getMaxCrewMod().modifyMult(id, crewMult);

        CompromisedStructure.modifyCost(hullSize, stats, id);
    }

    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
        for (String tmp : BLOCKED_HULLMODS) {
            if (ship.getVariant().getHullMods().contains(tmp)) {
                ship.getVariant().removeMod(tmp);
                ship.getVariant().addMod(ERROR);
            }
        }
    }

    public String getDescriptionParam(int index, HullSize hullSize, ShipAPI ship) {
        float effect = 1f;
        if (ship != null) effect = ship.getMutableStats().getDynamic().getValue(Stats.DMOD_EFFECT_MULT);

        float crewMult = MAX_CREW_MULT + (1f - MAX_CREW_MULT) * (1f - effect);

        if (index == 0) return "" + (int) Math.round((1f - crewMult) * 100f) + "%";
        if (index >= 1) return CompromisedStructure.getCostDescParam(index, 1);
        return null;
    }

    public boolean isApplicableToShip(ShipAPI ship) {
        for (String tmp : BLOCKED_HULLMODS) {
            if (ship.getVariant().getHullMods().contains(tmp)) {
                return false;
            }
        }
        return true;
    }

    public String getUnapplicableReason(ShipAPI ship) {
        for (String tmp : BLOCKED_HULLMODS) {
            if (ship.getVariant().getHullMods().contains(tmp)) {
                return ("Incompatible with currently applied hullmods");
            }
        }
        return null;
    }

}