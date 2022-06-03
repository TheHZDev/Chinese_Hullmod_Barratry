package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.Stats;

public class IncreasedMaintenance2 extends BaseHullMod {

    public static final float DEPLOYMENT_COST_MULT = 0.9f;
    private static final float CREW_PERCENT = 25;
    private static final float SUPPLY_USE_MULT = 1.25f;

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
        //stats.getSuppliesPerMonth().modifyMult(id, 1f + (SUPPLY_USE_MULT - 1f) * effect);
        stats.getSuppliesPerMonth().modifyPercent(id, Math.round((SUPPLY_USE_MULT - 1f) * effect * 100f));
        stats.getMinCrewMod().modifyPercent(id, CREW_PERCENT * effect);

        modifyCost(hullSize, stats, id);
        CompromisedStructure2.modifyCost(hullSize, stats, id);
    }

    public String getDescriptionParam(int index, HullSize hullSize, ShipAPI ship) {
        float effect = 1f;
        if (ship != null) effect = ship.getMutableStats().getDynamic().getValue(Stats.DMOD_EFFECT_MULT);
        if (index == 0) return "" + (int) ((1f + (SUPPLY_USE_MULT - 1f) * effect - 1f) * 100f) + "%";
        if (index == 1) return "" + (int) Math.round(CREW_PERCENT * effect) + "%";
        if (index >= 2) return CompromisedStructure2.getCostDescParam(index, 2);
        return null;
    }

    public boolean isApplicableToShip(ShipAPI ship) {
        return ship.getHullSpec().isCivilianNonCarrier();
    }

    public String getUnapplicableReason(ShipAPI ship) {
        return "只可以安装在民用舰船上";
    }

}
