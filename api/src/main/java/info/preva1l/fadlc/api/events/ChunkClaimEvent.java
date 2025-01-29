package info.preva1l.fadlc.api.events;

import info.preva1l.fadlc.models.IClaimChunk;
import info.preva1l.fadlc.models.claim.IClaim;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

@Getter
@Setter
public class ChunkClaimEvent extends ClaimChunkEvent implements Cancellable {
    @Getter private static final HandlerList handlerList = new HandlerList();
    private boolean cancelled;

    public ChunkClaimEvent(Player who, IClaim claim, IClaimChunk chunk) {
        super(who, claim, chunk);
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
