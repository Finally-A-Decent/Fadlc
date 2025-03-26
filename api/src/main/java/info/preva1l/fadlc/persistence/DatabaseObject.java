package info.preva1l.fadlc.persistence;

import java.util.UUID;

/**
 * Represents an object that can be stored in a database.
 */
public interface DatabaseObject {
    UUID getUniqueId();
}