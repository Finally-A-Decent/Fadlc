package info.preva1l.fadlc.menus.lib;

public interface PaginatedMenu {
    void openPage(int page);

    int currentPage();

    void fillPaginationItems();
}
