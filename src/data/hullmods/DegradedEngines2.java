package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.Stats;

public class DegradedEngines2 extends BaseHullMod {
    //public static final float PROFILE_PERCENT = 50f;

    public static final float MANEUVER_PENALTY_MULT = 0.92f;
    public static final float DEPLOYMENT_COST_MULT = 0.9f;

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

//	private static Map mag = new HashMap(); <--Why is this still here?
//	static {
//		mag.put(HullSize.FRIGATE, -1f);
//		mag.put(HullSize.DESTROYER, -1f);
//		mag.put(HullSize.CRUISER, -1f);
//		mag.put(HullSize.CAPITAL_SHIP, -1f);
//	}

    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
        float effect = stats.getDynamic().getValue(Stats.DMOD_EFFECT_MULT);
        float mult = MANEUVER_PENALTY_MULT + (1f - MANEUVER_PENALTY_MULT) * (1f - effect);

        stats.getMaxSpeed().modifyMult(id, mult);

        stats.getAcceleration().modifyMult(id, mult);
        stats.getDeceleration().modifyMult(id, mult);
        stats.getTurnAcceleration().modifyMult(id, mult);
        stats.getMaxTurnRate().modifyMult(id, mult);

        modifyCost(hullSize, stats, id);
        CompromisedStructure2.modifyCost(hullSize, stats, id);
    }

    public String getDescriptionParam(int index, HullSize hullSize, ShipAPI ship) {
        float effect = 1f;
        if (ship != null) effect = ship.getMutableStats().getDynamic().getValue(Stats.DMOD_EFFECT_MULT);
        float mult = MANEUVER_PENALTY_MULT + (1f - MANEUVER_PENALTY_MULT) * (1f - effect);

        if (index == 0) return "" + (int) Math.round((1f - mult) * 100f) + "%";
        if (index >= 1) return CompromisedStructure2.getCostDescParam(index, 1);
        return null;
    }

    public boolean isApplicableToShip(ShipAPI ship) {
        return ship.getHullSpec().isCivilianNonCarrier();
    }

    public String getUnapplicableReason(ShipAPI ship) {
        return "Only applicable to civilian ships";
    }

}







