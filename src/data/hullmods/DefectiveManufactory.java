package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.Stats;

import java.util.HashSet;
import java.util.Set;

public class DefectiveManufactory extends BaseHullMod {

    private static final Set<String> BLOCKED_HULLMODS = new HashSet<>();
    public static float SPEED_REDUCTION = 0.33333333f;
    public static float DAMAGE_INCREASE = 0.5f;

    static {
        BLOCKED_HULLMODS.add("defective_manufactory2");
    }

    private final String ERROR = "IncompatibleHullmodWarning";

    public void applyEffectsToFighterSpawnedByShip(ShipAPI fighter, ShipAPI ship, String id) {
        float effect = ship.getMutableStats().getDynamic().getValue(Stats.DMOD_EFFECT_MULT);

        MutableShipStatsAPI stats = fighter.getMutableStats();

        stats.getMaxSpeed().modifyMult(id, 1f - SPEED_REDUCTION * effect);

        stats.getArmorDamageTakenMult().modifyPercent(id, DAMAGE_INCREASE * 100f * effect);
        stats.getShieldDamageTakenMult().modifyPercent(id, DAMAGE_INCREASE * 100f * effect);
        stats.getHullDamageTakenMult().modifyPercent(id, DAMAGE_INCREASE * 100f * effect);

        fighter.setHeavyDHullOverlay();
    }

    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
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

        if (index == 0) return "" + (int) Math.round(SPEED_REDUCTION * 100f * effect) + "%";
        if (index == 1) return "" + (int) Math.round(DAMAGE_INCREASE * 100f * effect) + "%";
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




