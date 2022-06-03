package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.Stats;

@SuppressWarnings("unchecked")
public class DamagedWeaponMounts2 extends BaseHullMod {

    public static final float DEPLOYMENT_COST_MULT = 0.9f;
    public static float ROTATE_MULT = 0.87f;
    public static float RECOIL_PERCENT = 15f;

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
        float rotMult = ROTATE_MULT + (1f - ROTATE_MULT) * (1f - effect);

        stats.getWeaponTurnRateBonus().modifyMult(id, rotMult);
        stats.getBeamWeaponTurnRateBonus().modifyMult(id, rotMult);
        stats.getMaxRecoilMult().modifyPercent(id, RECOIL_PERCENT * effect);
        stats.getRecoilPerShotMult().modifyPercent(id, RECOIL_PERCENT * effect);

        modifyCost(hullSize, stats, id);
        CompromisedStructure2.modifyCost(hullSize, stats, id);
    }

    public String getDescriptionParam(int index, HullSize hullSize, ShipAPI ship) {
        float effect = 1f;
        if (ship != null) effect = ship.getMutableStats().getDynamic().getValue(Stats.DMOD_EFFECT_MULT);

        float rotMult = ROTATE_MULT + (1f - ROTATE_MULT) * (1f - effect);

        if (index == 0) return "" + (int) Math.round((1f - rotMult) * 100f) + "%";
        if (index == 1) return "" + (int) Math.round(RECOIL_PERCENT * effect) + "%";
        if (index >= 2) return CompromisedStructure2.getCostDescParam(index, 2);
        return null;
    }


}
