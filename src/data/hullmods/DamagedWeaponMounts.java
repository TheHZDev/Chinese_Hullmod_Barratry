package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.Stats;

import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("unchecked")
public class DamagedWeaponMounts extends BaseHullMod {

    private static final Set<String> BLOCKED_HULLMODS = new HashSet<>();
    public static float ROTATE_MULT = 0.75f;
    public static float RECOIL_PERCENT = 30f;

    static {
        BLOCKED_HULLMODS.add("damaged_mounts2");
    }

    private final String ERROR = "IncompatibleHullmodWarning";

    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
        float effect = stats.getDynamic().getValue(Stats.DMOD_EFFECT_MULT);
        float rotMult = ROTATE_MULT + (1f - ROTATE_MULT) * (1f - effect);

        stats.getWeaponTurnRateBonus().modifyMult(id, rotMult);
        stats.getBeamWeaponTurnRateBonus().modifyMult(id, rotMult);
        stats.getMaxRecoilMult().modifyPercent(id, RECOIL_PERCENT * effect);
        stats.getRecoilPerShotMult().modifyPercent(id, RECOIL_PERCENT * effect);

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

        float rotMult = ROTATE_MULT + (1f - ROTATE_MULT) * (1f - effect);

        if (index == 0) return "" + (int) Math.round((1f - rotMult) * 100f) + "%";
        if (index == 1) return "" + (int) Math.round(RECOIL_PERCENT * effect) + "%";
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
