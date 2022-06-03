package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.impl.hullmods.CompromisedStructure;

import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("unchecked")
public class FaultyPowerGrid extends BaseHullMod {

    public static final float PROFILE_PERCENT = 50f;

    private static final float CAPACITY_MULT = 0.8f;
    private static final float DISSIPATION_MULT = 0.8f;

    private static final Set<String> BLOCKED_HULLMODS = new HashSet<>();

    static {
        BLOCKED_HULLMODS.add("faulty_grid2");
    }

    private final String ERROR = "IncompatibleHullmodWarning";

    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
        float effect = stats.getDynamic().getValue(Stats.DMOD_EFFECT_MULT);
        float capMult = CAPACITY_MULT + (1f - CAPACITY_MULT) * (1f - effect);
        float disMult = DISSIPATION_MULT + (1f - DISSIPATION_MULT) * (1f - effect);

        stats.getFluxCapacity().modifyMult(id, capMult);
        stats.getFluxDissipation().modifyMult(id, disMult);

        stats.getSensorProfile().modifyPercent(id, PROFILE_PERCENT * effect);

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
        float capMult = CAPACITY_MULT + (1f - CAPACITY_MULT) * (1f - effect);
        float disMult = DISSIPATION_MULT + (1f - DISSIPATION_MULT) * (1f - effect);

        if (index == 0) return "" + (int) Math.round((1f - capMult) * 100f) + "%";
        if (index == 1) return "" + (int) Math.round((1f - disMult) * 100f) + "%";
//		if (index == 0) return "one third";
//		if (index == 1) return "one third";
        if (index == 2) return "" + (int) Math.round(PROFILE_PERCENT * effect) + "%";
        if (index >= 3) return CompromisedStructure.getCostDescParam(index, 3);
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
