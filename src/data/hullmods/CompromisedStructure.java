package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.Stats;

import java.util.HashSet;
import java.util.Set;

public class CompromisedStructure extends BaseHullMod {
    public static final float DEPLOYMENT_COST_MULT = 0.8f;
    public static final float ARMOR_PENALTY_MULT = 0.8f;
    public static final float HULL_PENALTY_MULT = 0.8f;
    private static final Set<String> BLOCKED_HULLMODS = new HashSet<>();

    static {
        BLOCKED_HULLMODS.add("comp_structure2");
    }

    private final String ERROR = "IncompatibleHullmodWarning";

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
        float armorMult = ARMOR_PENALTY_MULT + (1f - ARMOR_PENALTY_MULT) * (1f - effect);
        float hullMult = HULL_PENALTY_MULT + (1f - HULL_PENALTY_MULT) * (1f - effect);

        stats.getArmorBonus().modifyMult(id, armorMult);
        stats.getHullBonus().modifyMult(id, hullMult);
        modifyCost(hullSize, stats, id);
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

        float armorMult = ARMOR_PENALTY_MULT + (1f - ARMOR_PENALTY_MULT) * (1f - effect);
        float hullMult = HULL_PENALTY_MULT + (1f - HULL_PENALTY_MULT) * (1f - effect);

        if (index == 0) return "" + (int) Math.round((1f - armorMult) * 100f) + "%";
        if (index == 1) return "" + (int) Math.round((1f - hullMult) * 100f) + "%";
        if (index >= 2) return getCostDescParam(index, 2);
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




