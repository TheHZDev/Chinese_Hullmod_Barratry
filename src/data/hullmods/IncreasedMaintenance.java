package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.Stats;

import java.util.HashSet;
import java.util.Set;

public class IncreasedMaintenance extends BaseHullMod {

    private static final float CREW_PERCENT = 50;
    private static final float SUPPLY_USE_MULT = 1.5f;

    private static final Set<String> BLOCKED_HULLMODS = new HashSet<>();

    static {
        BLOCKED_HULLMODS.add("increased_maintenance2");
    }

    private final String ERROR = "IncompatibleHullmodWarning";

    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
        float effect = stats.getDynamic().getValue(Stats.DMOD_EFFECT_MULT);
        //stats.getSuppliesPerMonth().modifyMult(id, 1f + (SUPPLY_USE_MULT - 1f) * effect);
        stats.getSuppliesPerMonth().modifyPercent(id, Math.round((SUPPLY_USE_MULT - 1f) * effect * 100f));
        stats.getMinCrewMod().modifyPercent(id, CREW_PERCENT * effect);
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
        if (index == 0) return "" + (int) ((1f + (SUPPLY_USE_MULT - 1f) * effect - 1f) * 100f) + "%";
        if (index == 1) return "" + (int) Math.round(CREW_PERCENT * effect) + "%";
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
