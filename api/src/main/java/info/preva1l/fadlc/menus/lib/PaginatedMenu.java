package info.preva1l.fadlc.menus.lib;

import org.jetbrains.annotations.ApiStatus;

/**
 * Exposes some methods from the GUI library to the API for use in adding custom settings.
 */
@ApiStatus.NonExtendable
public interface PaginatedMenu {
    void openPage(int page);

    int currentPage();
}
