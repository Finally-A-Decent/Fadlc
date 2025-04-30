package info.preva1l.fadlc.api.events;

import info.preva1l.fadlc.claim.IClaim;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerEvent;

@Getter
public abstract class ClaimEvent extends PlayerEvent {
    protected final IClaim claim;

    public ClaimEvent(Player who, IClaim claim) {
        super(who);
        this.claim = claim;
    }
}
