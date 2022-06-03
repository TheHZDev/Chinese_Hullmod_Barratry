package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.Stats;

public class GlitchedSensorArray2 extends BaseHullMod {
    public static final float RANGE_MULT = 0.92f;
    public static final float SENSOR_MULT = 0.75f;
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

    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
        float effect = stats.getDynamic().getValue(Stats.DMOD_EFFECT_MULT);
        float rangeMult = RANGE_MULT + (1f - RANGE_MULT) * (1f - effect);
        float sensorMult = SENSOR_MULT + (1f - SENSOR_MULT) * (1f - effect);


        stats.getBallisticWeaponRangeBonus().modifyMult(id, rangeMult);
        stats.getEnergyWeaponRangeBonus().modifyMult(id, rangeMult);
        stats.getSensorStrength().modifyMult(id, sensorMult);

        modifyCost(hullSize, stats, id);
        CompromisedStructure2.modifyCost(hullSize, stats, id);
    }

    public String getDescriptionParam(int index, HullSize hullSize, ShipAPI ship) {
        float effect = 1f;
        if (ship != null) effect = ship.getMutableStats().getDynamic().getValue(Stats.DMOD_EFFECT_MULT);
        float rangeMult = RANGE_MULT + (1f - RANGE_MULT) * (1f - effect);
        float sensorMult = SENSOR_MULT + (1f - SENSOR_MULT) * (1f - effect);

        if (index == 0) return "" + (int) Math.round((1f - rangeMult) * 100f) + "%";
        //if (index == 1) return "50%";
        if (index == 1) return "" + (int) Math.round((1f - sensorMult) * 100f) + "%";
        if (index >= 2) return CompromisedStructure2.getCostDescParam(index, 2);
        return null;
    }


}




