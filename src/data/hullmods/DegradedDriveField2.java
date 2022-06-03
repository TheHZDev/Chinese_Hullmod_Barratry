package data.hullmods;

import java.util.HashMap;
import java.util.Map;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.Stats;

public class DegradedDriveField2 extends BaseHullMod {
    public static final float PROFILE_PERCENT = 25f;
    public static final float DEPLOYMENT_COST_MULT = 0.9f;
    private static Map mag = new HashMap();

    static {
        mag.put(HullSize.FRIGATE, -0f);
        mag.put(HullSize.DESTROYER, -0f);
        mag.put(HullSize.CRUISER, -1f);
        mag.put(HullSize.CAPITAL_SHIP, -1f);
    }

    public static void modifyCost(HullSize hullSize, MutableShipStatsAPI stats, String id) {
        stats.getSuppliesToRecover().modifyMult(id, DEPLOYMENT_COST_MULT);

        float effect = stats.getDynamic().getValue(Stats.DMOD_REDUCE_MAINTENANCE, 0);
        if (effect > 0) {
            stats.getSuppliesPerMonth().modifyMult(id, DEPLOYMENT_COST_MULT);
        }
    }

    public static String getCostDescParam(int index, int startIndex) {
        if (index - startIndex == 0) {
            return "" + (int) Math.round((1f - DEPLOYMENT_COST_MULT) * 100f) + "%";
        }
        return null;
    }

    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
        float effect = stats.getDynamic().getValue(Stats.DMOD_EFFECT_MULT);

        stats.getMaxBurnLevel().modifyFlat(id, (Float) mag.get(hullSize));
        stats.getSensorProfile().modifyPercent(id, PROFILE_PERCENT * effect);

        modifyCost(hullSize, stats, id);
        CompromisedStructure2.modifyCost(hullSize, stats, id);
    }

    public String getDescriptionParam(int index, HullSize hullSize, ShipAPI ship) {
        float effect = 1f;
        if (ship != null) effect = ship.getMutableStats().getDynamic().getValue(Stats.DMOD_EFFECT_MULT);

        if (index == 0) return "" + Math.abs(((Float) mag.get(hullSize)).intValue());
        if (index == 1) return "" + (int) Math.round(PROFILE_PERCENT * effect) + "%";
        if (index >= 2) return CompromisedStructure2.getCostDescParam(index, 2);

        return null;
    }

    public boolean isApplicableToShip(ShipAPI ship) {
        return ship.getHullSpec().isCivilianNonCarrier();
    }

    public String getUnapplicableReason(ShipAPI ship) {
        return "Only applicable to civilian ships";
    }

}







