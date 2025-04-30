package info.preva1l.fadlc;

import info.preva1l.trashcan.plugin.libloader.BaseLibraryLoader;

import java.nio.file.Path;
import java.util.function.Predicate;

/**
 * Created on 30/04/2025
 *
 * @author Preva1l
 */
public final class FadlcLibraryLoader extends BaseLibraryLoader {
    @Override
    protected Predicate<Path> remapPredicate() {
        return path -> path.toString().toLowerCase().contains("anvilgui");
    }
}
