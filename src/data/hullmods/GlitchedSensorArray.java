package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.Stats;

import java.util.HashSet;
import java.util.Set;

public class GlitchedSensorArray extends BaseHullMod {
    public static final float RANGE_MULT = 0.85f;
    public static final float SENSOR_MULT = 0.5f;

    private static final Set<String> BLOCKED_HULLMODS = new HashSet<>();

    static {
        BLOCKED_HULLMODS.add("glitched_sensors2");
    }

    private final String ERROR = "IncompatibleHullmodWarning";

    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
        float effect = stats.getDynamic().getValue(Stats.DMOD_EFFECT_MULT);
        float rangeMult = RANGE_MULT + (1f - RANGE_MULT) * (1f - effect);
        float sensorMult = SENSOR_MULT + (1f - SENSOR_MULT) * (1f - effect);


        stats.getBallisticWeaponRangeBonus().modifyMult(id, rangeMult);
        stats.getEnergyWeaponRangeBonus().modifyMult(id, rangeMult);
        stats.getSensorStrength().modifyMult(id, sensorMult);
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
        float rangeMult = RANGE_MULT + (1f - RANGE_MULT) * (1f - effect);
        float sensorMult = SENSOR_MULT + (1f - SENSOR_MULT) * (1f - effect);

        if (index == 0) return "" + (int) Math.round((1f - rangeMult) * 100f) + "%";
        //if (index == 1) return "50%";
        if (index == 1) return "" + (int) Math.round((1f - sensorMult) * 100f) + "%";
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
                return ("不与当前安装的其它船插兼容");
            }
        }
        return null;
    }

}




