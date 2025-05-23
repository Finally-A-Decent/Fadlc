package info.preva1l.fadlc.menus.lib;

import info.preva1l.fadlc.config.menus.MenuConfig;
import info.preva1l.fadlc.config.menus.lang.PaginatedLang;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.IntFunction;

/**
 * Extension of {@link FastInv} to easily create paginated inventories.
 */
public abstract class PaginatedFastInv<C extends MenuConfig<? extends PaginatedLang>> extends FastInv<C> implements PaginatedMenu {
    private final List<ItemStack> contentItems = new ArrayList<>();
    private final List<Consumer<InventoryClickEvent>> contentHandlers = new ArrayList<>();

    private List<Integer> contentSlots;
    private int page = 1;

    private IntFunction<ItemStack> previousPageItem;
    private IntFunction<ItemStack> nextPageItem;
    private int previousPageSlot = -1;
    private int nextPageSlot = -1;

    public PaginatedFastInv(Player player, C config) {
        super(player, config);

        scheme.bindPagination('X');
    }

    @Override
    protected void buttons() {
        super.buttons();
        fillPaginationItems();
    }

    @Override
    protected void placeNavigationItems() {
        scheme.bindItem('P', config.getLang().getPrevious().itemStack(), e -> {
            config.getLang().getPrevious().getSound().play((Player) e.getWhoClicked());
            openPrevious();
        });


        scheme.bindItem('N', config.getLang().getNext().itemStack(), e -> {
            config.getLang().getNext().getSound().play((Player) e.getWhoClicked());
            openNext();
        });
    }

    /**
     * Add an item to the paginated content with no click handler, the item will be added to the next available slot.
     *
     * @param item the item to add
     */
    public void addContent(ItemStack item) {
        addContent(item, null);
    }

    /**
     * Add an item to the paginated content with a click handler, the item will be added to the next available slot.
     *
     * @param item    the item to add
     * @param handler the click handler associated to this item
     */
    public void addContent(ItemStack item, Consumer<InventoryClickEvent> handler) {
        this.contentItems.add(item);
        this.contentHandlers.add(handler);
    }

    /**
     * Add a list of items to the paginated content with no click handler, the items will be added to the next available slots.
     *
     * @param content the list of items to add
     */
    public void addContent(Collection<ItemStack> content) {
        addContent(content, Collections.nCopies(content.size(), null));
    }

    /**
     * Add a list of items to the paginated content with click handlers, the items will be added to the next available slots.
     * The list of click handlers must have the same size as the list of items.
     *
     * @param content  the list of items to add
     * @param handlers the list of click handlers associated to the items
     */
    public void addContent(Collection<ItemStack> content, Collection<Consumer<InventoryClickEvent>> handlers) {
        Objects.requireNonNull(content, "content");
        Objects.requireNonNull(handlers, "handlers");

        if (content.size() != handlers.size()) {
            throw new IllegalArgumentException("The content and handlers lists must have the same size");
        }

        this.contentItems.addAll(content);
        this.contentHandlers.addAll(handlers);
    }

    /**
     * Set the item at the specified index of the paginated content, with no click handler.
     *
     * @param index the slot index
     * @param item  the item to set
     */
    public void setContent(int index, ItemStack item) {
        setContent(index, item, null);
    }

    /**
     * Set the item at the specified index of the paginated content, with a click handler.
     *
     * @param index   the slot index
     * @param item    the item to set
     * @param handler the click handler associated to this item
     */
    public void setContent(int index, ItemStack item, Consumer<InventoryClickEvent> handler) {
        this.contentItems.set(index, item);
        this.contentHandlers.set(index, handler);
    }

    /**
     * Set the list of items as the paginated content, with no click handler. The previous content will be cleared.
     *
     * @param content the list of items to set
     */
    public void setContent(List<ItemStack> content) {
        clearContent();
        addContent(content);
    }

    /**
     * Set the list of items as the paginated content, with click handlers. The previous content will be cleared.
     * The list of click handlers must have the same size as the list of items.
     *
     * @param content  the list of items to set
     * @param handlers the list of click handlers associated to the items
     */
    public void setContent(Collection<ItemStack> content, Collection<Consumer<InventoryClickEvent>> handlers) {
        Objects.requireNonNull(content, "content");
        Objects.requireNonNull(handlers, "handlers");

        if (content.size() != handlers.size()) {
            throw new IllegalArgumentException("The content and handlers lists must have the same size");
        }

        clearContent();
        addContent(content, handlers);
    }

    /**
     * Clear the paginated content and the associated click handlers.
     */
    public void clearContent() {
        this.contentItems.clear();
        this.contentHandlers.clear();
    }

    /**
     * Replace the inventory items with the content of the previous page.
     * To open the inventory itself, use {@link #open(Player)}.
     */
    public void openPrevious() {
        openPage(this.page - 1);
    }

    /**
     * Replace the inventory items with the content of the next page.
     * To open the inventory itself, use {@link #open(Player)}.
     */
    public void openNext() {
        openPage(this.page + 1);
    }

    protected abstract void fillPaginationItems();

    /**
     * Replace the inventory items with the content of the specified page.
     * To open the inventory itself, use {@link #open(Player)}.
     *
     * @param page the page to open
     */
    public void openPage(int page) {
        fillPaginationItems();
        int lastPage = lastPage();

        this.page = Math.max(1, Math.min(page, lastPage));

        int index = this.contentSlots.size() * (this.page - 1);

        for (int slot : this.contentSlots) {
            if (index >= this.contentItems.size()) {
                removeItem(slot);
                continue;
            }

            setItem(slot, contentItems.get(index), contentHandlers.get(index++));
        }

        if (this.page > 1 && this.previousPageItem != null) {
            setItem(this.previousPageSlot, this.previousPageItem.apply(this.page - 1), e -> openPrevious());
        } else if (this.previousPageSlot >= 0) {
            removeItem(this.previousPageSlot);
        }

        if (this.page < lastPage && this.nextPageItem != null) {
            setItem(this.nextPageSlot, this.nextPageItem.apply(this.page + 1), e -> openNext());
        } else if (this.nextPageSlot >= 0) {
            removeItem(this.nextPageSlot);
        }

        onPageChange(page);
    }

    /**
     * Specify the slots of the inventory that will be used to display the paginated content.
     *
     * @param contentSlots the slots of the inventory to use
     */
    public void setContentSlots(List<Integer> contentSlots) {
        this.contentSlots = Objects.requireNonNull(contentSlots, "contentSlots");
    }

    /**
     * Set the item at the specified inventory slot to open the previous page.
     *
     * @param slot the inventory to set the item
     * @param item a function to get the item to set, with the page the item opens as parameter
     */
    public void previousPageItem(int slot, IntFunction<ItemStack> item) {
        if (slot < 0 || slot >= getInventory().getSize()) {
            throw new IllegalArgumentException("Invalid slot: " + slot);
        }

        this.previousPageSlot = slot;
        this.previousPageItem = item;
    }

    /**
     * Set the item at the specified inventory slot to open the previous page.
     *
     * @param slot the inventory to set the item
     * @param item the item to set
     */
    public void previousPageItem(int slot, ItemStack item) {
        previousPageItem(slot, page -> item);
    }

    /**
     * Set the item at the specified inventory slot to open the next page.
     *
     * @param slot the inventory to set the item
     * @param item a function to get the item to set, with the page the item opens as parameter
     */
    public void nextPageItem(int slot, IntFunction<ItemStack> item) {
        if (slot < 0 || slot >= getInventory().getSize()) {
            throw new IllegalArgumentException("Invalid slot: " + slot);
        }

        this.nextPageSlot = slot;
        this.nextPageItem = item;
    }

    /**
     * Set the item at the specified inventory slot to open the next page.
     *
     * @param slot the inventory to set the item
     * @param item the item to set
     */
    public void nextPageItem(int slot, ItemStack item) {
        nextPageItem(slot, page -> item);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void open(Player player) {
        scheme.apply(this);
        openPage(this.page);

        super.open(player);
    }

    /**
     * Called when the page is changed.
     *
     * @param page the new page
     */
    protected void onPageChange(int page) {
    }

    /**
     * Return the index of the current page. The first page is 1.
     *
     * @return the index of the current page, starting at 1
     */
    public int currentPage() {
        return this.page;
    }

    /**
     * Return the index of the last page. The index of the first page is 1.
     *
     * @return the index of the last page, starting at 1
     */
    public int lastPage() {
        int last = this.contentItems.size() / this.contentSlots.size();
        int remaining = this.contentItems.size() % this.contentSlots.size();

        return remaining == 0 ? last : last + 1;
    }

    /**
     * Return if the current page is the first page.
     *
     * @return true if the current page is the first page
     * @see #currentPage()
     */
    public boolean isFirstPage() {
        return this.page == 1;
    }

    /**
     * Return if the current page is the last page.
     *
     * @return true if the current page is the last page
     * @see #currentPage()
     */
    public boolean isLastPage() {
        return this.page == lastPage();
    }
}