package me.uuun.warProject.model;

import lombok.Builder;
import lombok.Getter;
import me.uuun.warProject.bug.Bugger;

import java.util.Set;

@Builder
@Getter
public class TroopAttack {
    private final Set<Troop> attackers;
    private final Troop defender;

    public void addAttacker(Troop attacker) {
        if (attacker == null) {
            Bugger.bug("Attacker is null at AddAttacker for TroopAttack");
            return;
        }
        this.attackers.add(attacker);
    }

    public void removeAttacker(Troop attacker) {
        if (attacker == null) {
            Bugger.bug("Attacker is null at RemoveAttacker for TroopAttack");
            return;
        }
        this.attackers.remove(attacker);
    }
}