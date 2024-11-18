package info.preva1l.fadlc.migrators;

import info.preva1l.fadlc.managers.ClaimManager;
import info.preva1l.fadlc.models.IClaimChunk;
import info.preva1l.fadlc.models.claim.IClaim;
import info.preva1l.fadlc.utils.Logger;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class Migrator {
    public abstract String getName();

    public final CompletableFuture<Void> migrate() {
        return CompletableFuture.supplyAsync(() -> {
            Logger.info("Migration started from " + getName());

            long startTime = System.currentTimeMillis();
            AtomicInteger migratedClaims = new AtomicInteger();
            AtomicInteger scannedChunks = new AtomicInteger();

            migrateClaims().values().forEach(claim -> {
                    ClaimManager.getInstance().updateClaim(claim);
                    migratedClaims.getAndIncrement();
            });
            Logger.info("Migrated %s Claims".formatted(migratedClaims.get()));

            scanChunks().values().forEach(chunk -> {
                ClaimManager.getInstance().cacheChunk(chunk);
                scannedChunks.getAndIncrement();
            });
            Logger.info("Scanned & Migrated %s Chunks".formatted(scannedChunks.get()));

            Logger.info("Migration Complete! (%sms)".formatted(startTime - System.currentTimeMillis()));
            return null;
        });
    }

    protected abstract Map<UUID, IClaim> migrateClaims();

    protected abstract Map<UUID, IClaimChunk> scanChunks();
}
