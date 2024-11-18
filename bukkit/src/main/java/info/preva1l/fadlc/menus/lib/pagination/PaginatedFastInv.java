package info.preva1l.fadlc.menus.lib.pagination;

import info.preva1l.fadlc.Fadlc;
import info.preva1l.fadlc.config.Menus;
import info.preva1l.fadlc.config.misc.ConfigurableItem;
import info.preva1l.fadlc.managers.LayoutManager;
import info.preva1l.fadlc.menus.lib.FastInv;
import info.preva1l.fadlc.menus.lib.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class PaginatedFastInv extends FastInv {
    protected final Player player;

    protected int page = 0;
    protected int index = 0;
    private List<Integer> paginationMappings;
    private final List<PaginatedItem> paginatedItems = new ArrayList<>();

    private final BukkitTask updateTask;

    protected PaginatedFastInv(Player player, LayoutManager.MenuType menuType) {
        this(menuType.getSize(), menuType.getTitle(), player, menuType);
    }

    protected PaginatedFastInv(Player player, LayoutManager.MenuType menuType, @NotNull List<Integer> paginationMappings) {
        this(menuType.getSize(), menuType.getTitle(), player, menuType, paginationMappings);
    }

    protected PaginatedFastInv(int size, @NotNull String title, @NotNull Player player, LayoutManager.MenuType menuType) {
        super(size, title, menuType);
        this.player = player;
        this.paginationMappings = List.of(
                11, 12, 13, 14, 15, 16, 20,
                21, 22, 23, 24, 25, 29, 30,
                31, 32, 33, 34, 38, 39, 40,
                41, 42, 43);


        this.updateTask = Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(Fadlc.i(), this::updatePagination, 20L, 20L);
        getCloseHandlers().add((e) -> updateTask.cancel());
    }

    protected PaginatedFastInv(int size, @NotNull String title, @NotNull Player player, LayoutManager.MenuType menuType, @NotNull List<Integer> paginationMappings) {
        super(size, title, menuType);
        this.player = player;
        this.paginationMappings = paginationMappings;

        this.updateTask = Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(Fadlc.i(), this::updatePagination, 20L, 20L);
        getCloseHandlers().add((e) -> updateTask.cancel());
    }

    protected void setPaginationMappings(List<Integer> list) {
        this.paginationMappings = list;
    }

    protected void nextPage() {
        if (paginatedItems == null || paginatedItems.size() < index + 1) {
            return;
        }
        page++;
        populatePage();
        addPaginationControls();
    }

    protected void previousPage() {
        if (page == 0) {
            return;
        }
        page--;
        populatePage();
        addPaginationControls();
    }

    protected void populatePage() {
        int maxItemsPerPage = paginationMappings.size();
        boolean empty = paginatedItems == null || paginatedItems.isEmpty();
        if (empty) {
            paginationEmpty();
        }

        for (int i = 0; i < maxItemsPerPage; i++) {
            removeItem(paginationMappings.get(i));
            index = maxItemsPerPage * page + i;
            if (index >= paginatedItems.size()) continue;
            PaginatedItem item = paginatedItems.get(index);
            setItem(paginationMappings.get(i), item.itemStack(), item.eventConsumer());
        }

    }

    protected void updatePagination() {
        paginatedItems.clear();
        fillPaginationItems();
        populatePage();
        addPaginationControls();
    }

    protected void paginationEmpty() {
        List<Integer> noItems = getLayout().noItems();
        ConfigurableItem conf = Menus.getInstance().getNoItems();
        if (!noItems.isEmpty()) {
            setItems(noItems.stream().mapToInt(Integer::intValue).toArray(),
                    new ItemBuilder(conf.getMaterial())
                            .name(conf.getName())
                            .modelData(conf.getModelData())
                            .lore(conf.getLore()).build());
        }
    }

    protected abstract void fillPaginationItems();

    protected abstract void addPaginationControls();

    protected void addPaginationItem(PaginatedItem item) {
        paginatedItems.add(item);
    }
}