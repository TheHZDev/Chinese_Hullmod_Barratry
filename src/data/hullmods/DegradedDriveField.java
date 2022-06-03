package data.hullmods;

import java.util.HashMap;
import java.util.Map;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.impl.hullmods.CompromisedStructure;

import java.util.HashSet;
import java.util.Set;

public class DegradedDriveField extends BaseHullMod {
    public static final float PROFILE_PERCENT = 50f;

    private static final Set<String> BLOCKED_HULLMODS = new HashSet<>();
    private static Map mag = new HashMap();

    static {
        BLOCKED_HULLMODS.add("degraded_drive_field2");
    }

    static {
        mag.put(HullSize.FRIGATE, -1f);
        mag.put(HullSize.DESTROYER, -1f);
        mag.put(HullSize.CRUISER, -1f);
        mag.put(HullSize.CAPITAL_SHIP, -1f);
    }

    private final String ERROR = "IncompatibleHullmodWarning";

    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
        float effect = stats.getDynamic().getValue(Stats.DMOD_EFFECT_MULT);

        stats.getMaxBurnLevel().modifyFlat(id, (Float) mag.get(hullSize));
        stats.getSensorProfile().modifyPercent(id, PROFILE_PERCENT * effect);

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

        if (index == 0) return "" + Math.abs(((Float) mag.get(hullSize)).intValue());
        if (index == 1) return "" + (int) Math.round(PROFILE_PERCENT * effect) + "%";
        if (index >= 2) return CompromisedStructure.getCostDescParam(index, 2);
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







