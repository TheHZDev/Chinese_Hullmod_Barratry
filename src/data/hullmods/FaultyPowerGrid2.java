package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.Stats;

@SuppressWarnings("unchecked")
public class FaultyPowerGrid2 extends BaseHullMod {

    public static final float PROFILE_PERCENT = 25f;
    public static final float DEPLOYMENT_COST_MULT = 0.9f;
    private static final float CAPACITY_MULT = 0.9f;
    private static final float DISSIPATION_MULT = 0.9f;

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
        float capMult = CAPACITY_MULT + (1f - CAPACITY_MULT) * (1f - effect);
        float disMult = DISSIPATION_MULT + (1f - DISSIPATION_MULT) * (1f - effect);

        stats.getFluxCapacity().modifyMult(id, capMult);
        stats.getFluxDissipation().modifyMult(id, disMult);

        stats.getSensorProfile().modifyPercent(id, PROFILE_PERCENT * effect);

        modifyCost(hullSize, stats, id);
        CompromisedStructure2.modifyCost(hullSize, stats, id);
    }

    public String getDescriptionParam(int index, HullSize hullSize, ShipAPI ship) {
        float effect = 1f;
        if (ship != null) effect = ship.getMutableStats().getDynamic().getValue(Stats.DMOD_EFFECT_MULT);
        float capMult = CAPACITY_MULT + (1f - CAPACITY_MULT) * (1f - effect);
        float disMult = DISSIPATION_MULT + (1f - DISSIPATION_MULT) * (1f - effect);

        if (index == 0) return "" + (int) Math.round((1f - capMult) * 100f) + "%";
        if (index == 1) return "" + (int) Math.round((1f - disMult) * 100f) + "%";
//		if (index == 0) return "one third";
//		if (index == 1) return "one third";
        if (index == 2) return "" + (int) Math.round(PROFILE_PERCENT * effect) + "%";
        if (index >= 3) return CompromisedStructure2.getCostDescParam(index, 3);
        return null;
    }


}
