package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.Stats;

public class FaultyAutomatedSystems2 extends BaseHullMod {

    public static final float DEPLOYMENT_COST_MULT = 0.9f;
    public static float CREW_PERCENT = 50f;

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

        stats.getMinCrewMod().modifyPercent(id, CREW_PERCENT * effect);

        modifyCost(hullSize, stats, id);
        CompromisedStructure2.modifyCost(hullSize, stats, id);
    }

    public String getDescriptionParam(int index, HullSize hullSize, ShipAPI ship) {
        float effect = 1f;
        if (ship != null) effect = ship.getMutableStats().getDynamic().getValue(Stats.DMOD_EFFECT_MULT);
        if (index == 0) return "" + (int) Math.round(CREW_PERCENT * effect) + "%";
        if (index >= 1) return CompromisedStructure2.getCostDescParam(index, 1);
        return null;
    }

    public boolean isApplicableToShip(ShipAPI ship) {
        if (ship == null) return false;
        ship.isDrone();
        return true;
    }

    public String getUnapplicableReason(ShipAPI ship) {
        return "Ship has no crew";
    }

}
