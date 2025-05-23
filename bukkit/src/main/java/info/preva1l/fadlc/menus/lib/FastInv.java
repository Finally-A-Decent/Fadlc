package info.preva1l.fadlc.menus.lib;

import com.github.puregero.multilib.MultiLib;
import info.preva1l.fadlc.Fadlc;
import info.preva1l.fadlc.config.menus.MenuConfig;
import info.preva1l.fadlc.config.menus.lang.MenuLang;
import info.preva1l.fadlc.user.OnlineUser;
import info.preva1l.fadlc.user.UserService;
import info.preva1l.fadlc.utils.Executors;
import info.preva1l.fadlc.utils.Tasks;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.IntStream;

/**
 * Lightweight and easy-to-use inventory API for Bukkit plugins.
 * The project is on <a href="https://github.com/MrMicky-FR/FastInv">GitHub</a>.
 *
 * @author MrMicky
 * @version 3.1.1
 */
public abstract class FastInv<C extends MenuConfig<? extends MenuLang>> implements InventoryHolder {
    protected final C config;
    protected final InventoryScheme scheme;
    private final Map<Integer, Consumer<InventoryClickEvent>> itemHandlers = new HashMap<>();
    private final List<Consumer<InventoryOpenEvent>> openHandlers = new ArrayList<>();
    private final List<Consumer<InventoryCloseEvent>> closeHandlers = new ArrayList<>();
    private final List<Consumer<InventoryClickEvent>> clickHandlers = new ArrayList<>();
    private final List<Consumer<InventoryDragEvent>> dragHandlers = new ArrayList<>();
    private final Inventory inventory;
    @Setter private Predicate<Player> closeFilter;

    protected final OnlineUser user;

    public FastInv(Player player, C config) {
        this(owner -> Bukkit.createInventory(owner, config.getSize() * 9, config.title()), player, config);
    }

    public FastInv(Function<InventoryHolder, Inventory> inventoryFunction, Player player, C config) {
        Objects.requireNonNull(inventoryFunction, "inventoryFunction");
        Inventory inv = inventoryFunction.apply(this);

        if (inv.getHolder() != this) {
            throw new IllegalStateException("Inventory holder is not FastInv, found: " + inv.getHolder());
        }

        this.user = UserService.getInstance().getUser(player).orElseThrow();
        this.inventory = inv;
        this.config = config;
        this.scheme = new InventoryScheme()
                .masks(config.getLayout().toArray(new String[0]))
                .bindItem('0', config.getLang().getFiller().itemStack());

        CompletableFuture.runAsync(this::buttons, Executors.VTHREAD).thenRun(() -> Tasks.runSync(player, this::open));
    }

    protected void buttons() {
        placeNavigationItems();
    }

    protected abstract void placeNavigationItems();

    /**
     * Called when the inventory is opened.
     *
     * @param event the InventoryOpenEvent that triggered this method
     */
    protected void onOpen(InventoryOpenEvent event) {
    }

    /**
     * Called when the inventory is clicked.
     *
     * @param event the InventoryClickEvent that triggered this method
     */
    protected void onClick(InventoryClickEvent event) {
    }

    /**
     * Called when the player drags an item in their cursor across the inventory.
     *
     * @param event the InventoryDragEvent that triggered this method
     */
    protected void onDrag(InventoryDragEvent event) {
    }

    /**
     * Called when the inventory is closed.
     *
     * @param event the InventoryCloseEvent that triggered this method
     */
    protected void onClose(InventoryCloseEvent event) {
    }

    /**
     * Add an {@link ItemStack} to the inventory on the first empty slot, with no click handler.
     *
     * @param item the item to add
     */
    public void addItem(ItemStack item) {
        addItem(item, null);
    }

    /**
     * Add an {@link ItemStack} to the inventory on the first empty slot with a click handler.
     *
     * @param item    the item to add.
     * @param handler the click handler associated to this item
     */
    public void addItem(ItemStack item, Consumer<InventoryClickEvent> handler) {
        int slot = this.inventory.firstEmpty();
        if (slot >= 0) {
            setItem(slot, item, handler);
        }
    }

    /**
     * Add an {@link ItemStack} to the inventory on a specific slot, with no click handler.
     *
     * @param slot The slot where to add the item.
     * @param item The item to add.
     */
    public void setItem(int slot, ItemStack item) {
        setItem(slot, item, null);
    }

    /**
     * Add an {@link ItemStack} to the inventory on specific slot with a click handler.
     *
     * @param slot    the slot where to add the item
     * @param item    the item to add.
     * @param handler the click handler associated to this item
     */
    public void setItem(int slot, ItemStack item, Consumer<InventoryClickEvent> handler) {
        if (slot == -1) return;
        this.inventory.setItem(slot, item);

        if (handler != null) {
            this.itemHandlers.put(slot, handler);
        } else {
            this.itemHandlers.remove(slot);
        }
    }

    /**
     * Add an {@link ItemStack} to the inventory on a range of slots, with no click handler.
     *
     * @param slotFrom starting slot (inclusive) to put the item in
     * @param slotTo   ending slot (exclusive) to put the item in
     * @param item     The item to add.
     */
    public void setItems(int slotFrom, int slotTo, ItemStack item) {
        setItems(slotFrom, slotTo, item, null);
    }

    /**
     * Add an {@link ItemStack} to the inventory on a range of slots with a click handler.
     *
     * @param slotFrom starting slot (inclusive) to put the item in
     * @param slotTo   ending slot (exclusive) to put the item in
     * @param item     the item to add
     * @param handler  the click handler associated to these items
     */
    public void setItems(int slotFrom, int slotTo, ItemStack item, Consumer<InventoryClickEvent> handler) {
        for (int i = slotFrom; i < slotTo; i++) {
            setItem(i, item, handler);
        }
    }

    /**
     * Add an {@link ItemStack} to the inventory on multiple slots, with no click handler.
     *
     * @param slots the slots where to add the item
     * @param item  the item to add
     */
    public void setItems(int[] slots, ItemStack item) {
        setItems(slots, item, null);
    }

    /**
     * Add an {@link ItemStack} to the inventory on multiples slots with a click handler.
     *
     * @param slots   the slots where to add the item
     * @param item    the item to add
     * @param handler the click handler associated to this item
     */
    public void setItems(int[] slots, ItemStack item, Consumer<InventoryClickEvent> handler) {
        for (int slot : slots) {
            setItem(slot, item, handler);
        }
    }

    /**
     * Add an {@link ItemStack} to the inventory on multiple slots, with no click handler.
     *
     * @param slots the list of slots where to add the item
     * @param item  the item to add
     */
    public void setItems(Iterable<Integer> slots, ItemStack item) {
        setItems(slots, item, null);
    }

    /**
     * Add an {@link ItemStack} to the inventory on multiple slots with a click handler.
     *
     * @param slots   the list of slots where to add the item
     * @param item    the item to add
     * @param handler the click handler associated to this item
     */
    public void setItems(Iterable<Integer> slots, ItemStack item, Consumer<InventoryClickEvent> handler) {
        for (Integer slot : slots) {
            setItem(slot, item, handler);
        }
    }

    /**
     * Remove an {@link ItemStack} from the inventory.
     *
     * @param slot the slot from where to remove the item
     */
    public void removeItem(int slot) {
        this.inventory.clear(slot);
        this.itemHandlers.remove(slot);
    }

    /**
     * Remove multiples {@link ItemStack} from the inventory.
     *
     * @param slots the slots from where to remove the items
     */
    public void removeItems(int... slots) {
        for (int slot : slots) {
            removeItem(slot);
        }
    }

    /**
     * Clear all items from the inventory and remove the click handlers.
     */
    public void clearItems() {
        this.inventory.clear();
        this.itemHandlers.clear();
    }

    /**
     * Add a handler that will be called when the inventory is opened.
     *
     * @param openHandler the handler to add
     */
    public void addOpenHandler(Consumer<InventoryOpenEvent> openHandler) {
        this.openHandlers.add(openHandler);
    }

    /**
     * Add a handler that will be called when the inventory is closed.
     *
     * @param closeHandler the handler to add
     */
    public void addCloseHandler(Consumer<InventoryCloseEvent> closeHandler) {
        this.closeHandlers.add(closeHandler);
    }

    /**
     * Add a handler that will be called when an item is clicked.
     *
     * @param clickHandler the handler to add
     */
    public void addClickHandler(Consumer<InventoryClickEvent> clickHandler) {
        this.clickHandlers.add(clickHandler);
    }

    /**
     * Add a handler that will be called when the player drags an item in their cursor across the inventory.
     *
     * @param dragHandler the handler to add
     */
    public void addDragHandler(Consumer<InventoryDragEvent> dragHandler) {
        this.dragHandlers.add(dragHandler);
    }

    /**
     * Open the inventory to the given player.
     *
     * @param player the player to open the inventory to
     */
    protected void open(Player player) {
        scheme.apply(this);
        MultiLib.getEntityScheduler(player).execute(Fadlc.i(), () -> player.openInventory(this.inventory), null, 0L);
    }

    /**
     * Get the borders of this inventory. If the inventory size is under 27, all slots are returned.
     *
     * @return the inventory borders slots
     */
    public int[] getBorders() {
        int size = this.inventory.getSize();
        return IntStream.range(0, size).filter(i -> size < 27 || i < 9 || i % 9 == 0 || (i - 8) % 9 == 0 || i > size - 9).toArray();
    }

    /**
     * Get the corners of this inventory.
     *
     * @return the inventory corners slots
     */
    public int[] getCorners() {
        int size = this.inventory.getSize();
        return IntStream.range(0, size).filter(i -> i < 2 || (i > 6 && i < 10) || i == 17 || i == size - 18 || (i > size - 11 && i < size - 7) || i > size - 3).toArray();
    }

    /**
     * Get the underlying Bukkit inventory.
     *
     * @return the Bukkit inventory
     */
    @Override
    public @NotNull Inventory getInventory() {
        return this.inventory;
    }

    void handleOpen(InventoryOpenEvent e) {
        onOpen(e);

        this.openHandlers.forEach(c -> c.accept(e));
    }

    boolean handleClose(InventoryCloseEvent e) {
        onClose(e);

        this.closeHandlers.forEach(c -> c.accept(e));

        return this.closeFilter != null && this.closeFilter.test((Player) e.getPlayer());
    }

    void handleClick(InventoryClickEvent e) {
        onClick(e);

        this.clickHandlers.forEach(c -> c.accept(e));

        Consumer<InventoryClickEvent> clickConsumer = this.itemHandlers.get(e.getRawSlot());

        if (clickConsumer != null) {
            clickConsumer.accept(e);
        }
    }

    void handleDrag(InventoryDragEvent e) {
        onDrag(e);

        this.dragHandlers.forEach(c -> c.accept(e));
    }
}