package com.yuhtin.lauren.module.impl.player.module;

import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import com.yuhtin.lauren.Lauren;
import com.yuhtin.lauren.cache.ModularCache;
import com.yuhtin.lauren.config.YamlConfiguration;
import com.yuhtin.lauren.database.MongoModule;
import com.yuhtin.lauren.database.MongoOperation;
import com.yuhtin.lauren.database.OperationFilter;
import com.yuhtin.lauren.module.ConfigurableModule;
import com.yuhtin.lauren.module.Module;
import com.yuhtin.lauren.module.impl.player.Player;
import com.yuhtin.lauren.util.FutureBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

public class PlayerModule extends ConfigurableModule {

    private final ModularCache<Long, Player> cache = ModularCache.create(Caffeine.newBuilder()
            .expireAfterAccess(3, TimeUnit.MINUTES)
            .removalListener(this::saveOnRemoval)
            .buildAsync(this::asyncLoad));

    @Override
    public boolean setup(Lauren lauren) {
        setConfig(YamlConfiguration.load("features/player.yml"));

        MongoModule mongoModule = Module.instance(MongoModule.class);
        if (mongoModule == null) {
            throw new IllegalStateException("MongoModule is not loaded!");
        }

        mongoModule.registerBinding("player_data", Player.class);
        return true;
    }

    public FutureBuilder<Player> retrieve(long userId) {
        return FutureBuilder.of(cache.get(userId));
    }

    private void saveOnRemoval(Long userId, Player player, RemovalCause removalCause) {
        MongoOperation.bind(Player.class)
                .filter(OperationFilter.EQUALS, "id", userId)
                .insert(player)
                .queue();
    }

    private CompletableFuture<? extends Player> asyncLoad(long userId, Executor executor) {
        return MongoOperation.bind(Player.class)
                .filter(OperationFilter.EQUALS, "id", userId)
                .findOrCreate(new Player(userId))
                .future();
    }

    public boolean isDJ(Member member) {
        long value = getConfig().getNumber("roles.dj", 0L).longValue();
        return hasRole(member, value);
    }

    public boolean isPrime(Member member) {
        long value = getConfig().getNumber("roles.prime", 0L).longValue();
        return hasRole(member, value);
    }

    public boolean hasRole(Member member, long roleId) {
        if (roleId == 0) return false;

        for (Role role : member.getRoles()) {
            if (role.getIdLong() == roleId) {
                return true;
            }
        }

        return false;
    }

    public FutureBuilder<Void> saveAll() {
        Collection<CompletableFuture<Player>> cacheValues = cache.values();

        CompletableFuture<Void> task = CompletableFuture.allOf(cacheValues.stream()
                .map(future -> future.thenAcceptAsync(Player::save))
                .toArray(CompletableFuture[]::new)
        );

        return FutureBuilder.of(task.thenAccept(unused -> cache.invalidateAll()));
    }
}
