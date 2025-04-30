package info.preva1l.fadlc.api.events;

import info.preva1l.fadlc.claim.IClaim;
import info.preva1l.fadlc.claim.IClaimChunk;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

@Getter
@Setter
public class ClaimLeaveEvent extends ClaimChunkEvent {
    @Getter private static final HandlerList handlerList = new HandlerList();

    public ClaimLeaveEvent(Player who, IClaim claim, IClaimChunk chunk) {
        super(who, claim, chunk);
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
