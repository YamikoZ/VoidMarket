package com.voidsmp.voidmarket;

import com.voidsmp.voidmarket.command.MarketCommand;
import com.voidsmp.voidmarket.command.PlayerShopCommand;
import com.voidsmp.voidmarket.config.Settings;
import com.voidsmp.voidmarket.economy.EconomyService;
import com.voidsmp.voidmarket.gui.GuiService;
import com.voidsmp.voidmarket.listener.GuiListener;
import com.voidsmp.voidmarket.market.MarketService;
import com.voidsmp.voidmarket.notification.*;
import com.voidsmp.voidmarket.placeholder.VoidMarketExpansion;
import com.voidsmp.voidmarket.pricing.PricingService;
import com.voidsmp.voidmarket.shop.ShopService;
import com.voidsmp.voidmarket.storage.*;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VoidMarketPlugin extends JavaPlugin {
    private ExecutorService databaseExecutor;
    private StorageProvider storage;
    private MessageService messageService;
    private Settings settings;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        saveResource("messages_en.yml", false);
        saveResource("messages_th.yml", false);

        databaseExecutor = Executors.newFixedThreadPool(2, runnable -> {
            Thread thread = new Thread(runnable, "VoidMarket-DB");
            thread.setDaemon(true);
            return thread;
        });
        settings = new Settings(getConfig());
        messageService = new MessageService(this);

        EconomyService economy = new EconomyService(this);
        if (!economy.hook()) {
            getLogger().severe("Vault economy provider not found. Install Vault + EssentialsX Economy.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        String type = getConfig().getString("storage.type", "sqlite").toLowerCase(Locale.ROOT);
        storage = type.equals("mysql") || type.equals("mariadb") ? new MySQLStorageProvider(this) : new SQLiteStorageProvider(this);
        try {
            storage.init();
            new MigrationManager(storage).migrate();
        } catch (SQLException exception) {
            getLogger().severe("Database initialization failed: " + exception.getMessage());
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        MarketRepository marketRepository = new MarketRepository(storage);
        ShopRepository shopRepository = new ShopRepository(storage);
        TransactionRepository transactionRepository = new TransactionRepository();
        PricingService pricing = new PricingService(settings);
        MarketService market = new MarketService(this, storage, marketRepository, transactionRepository, pricing, economy, settings, databaseExecutor);
        ShopService shops = new ShopService(this, storage, shopRepository, transactionRepository, economy, settings, databaseExecutor);
        market.seedFromConfig();

        TitleService titles = new TitleService(messageService);
        ActionBarService actionBars = new ActionBarService(messageService);
        SoundService sounds = new SoundService(messageService);
        NotificationService notifications = new NotificationService(messageService, titles, actionBars, sounds);
        GuiService gui = new GuiService(this, messageService, market, shops);

        getServer().getPluginManager().registerEvents(new GuiListener(this, gui, market, shops, notifications), this);
        MarketCommand marketCommand = new MarketCommand(this, market, gui, messageService, notifications);
        PlayerShopCommand shopCommand = new PlayerShopCommand(this, shops, gui, messageService, notifications);
        register("market", marketCommand);
        register("pshop", shopCommand);

        if (getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new VoidMarketExpansion(market, shops).register();
            getLogger().info("PlaceholderAPI hook registered.");
        }

        scheduleReset(market);
        getLogger().info("VoidMarket enabled on " + storage.type() + ".");
    }

    @Override
    public void onDisable() {
        if (storage != null) storage.close();
        if (databaseExecutor != null) databaseExecutor.shutdownNow();
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();
        if (settings != null) settings.reload();
        if (messageService != null) messageService.reload();
    }

    private void register(String name, Object executor) {
        PluginCommand command = getCommand(name);
        if (command == null) return;
        command.setExecutor((org.bukkit.command.CommandExecutor) executor);
        command.setTabCompleter((org.bukkit.command.TabCompleter) executor);
    }

    private void scheduleReset(MarketService market) {
        long day = 20L * 60L * 60L * 24L;
        getServer().getScheduler().runTaskTimerAsynchronously(this, () -> market.resetDaily()
                .exceptionally(error -> {
                    getLogger().warning("Daily reset failed: " + error.getMessage());
                    return null;
                }), day, day);
    }
}
