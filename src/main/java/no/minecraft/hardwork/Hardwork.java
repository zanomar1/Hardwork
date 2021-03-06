package no.minecraft.hardwork;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import no.minecraft.hardwork.commands.WhoCommand;
import no.minecraft.hardwork.database.DataConsumer;
import no.minecraft.hardwork.database.Database;
import no.minecraft.hardwork.handlers.UserHandler;
import no.minecraft.hardwork.listeners.PlayerListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public class Hardwork implements DataConsumer {
    private final JavaPlugin plugin;

    private Database database;
    private CacheManager cacheManager;

    private final UserHandler userHandler = new UserHandler(this);

    public Hardwork(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void onEnable() {
        this.plugin.getServer().getPluginManager().registerEvents(new PlayerListener(this), this.plugin);

        this.plugin.getCommand("who").setExecutor(new WhoCommand(this));
    }

    public JavaPlugin getPlugin() {
        return this.plugin;
    }

    public Database getDatabase() {
        if (this.database == null)
            this.database = new Database(
                this.plugin,
                this,
                this.plugin.getConfig().getString("mysql.hostname"),
                this.plugin.getConfig().getInt("mysql.port"),
                this.plugin.getConfig().getString("mysql.username"),
                this.plugin.getConfig().getString("mysql.password")
            );

        return this.database;
    }

    public Cache getCache(String name) {
        if (this.cacheManager == null)
            this.cacheManager = new CacheManager();

        if (!this.cacheManager.cacheExists(name))
            this.cacheManager.addCache(new Cache(name, 200, false, false, 3600, 3600));

        return this.cacheManager.getCache(name);
    }

    public UserHandler getUserHandler() {
        return this.userHandler;
    }

    public void prepareStatements() throws SQLException {
        this.userHandler.prepareStatements();
    }
}
