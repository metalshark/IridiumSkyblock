package listeners.bukkit;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.block.BlockMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.User;
import com.iridium.iridiumskyblock.listeners.bukkit.BlockBreakListener;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

@DisplayName("A block break listener")
class TestBlockBreakListener {

    ServerMock server;
    Plugin plugin;
    Block block;
    Player player;

    BlockBreakEvent event;
    Consumer<Event> callEvent;

    Island island;
    Function<Location, Island> mockGetIslandByLocation = loc -> island;

    User user;
    Function<Player, User> mockGetUserByPlayer = pl -> user;

    List<Event> eventsCalled;
    boolean cancelCalledEvent;
    Consumer<Event> mockCallEvent = calledEvent -> {
        eventsCalled.add(calledEvent);
        event.setCancelled(cancelCalledEvent);
    };

    @BeforeEach
    void setUp() {
        server = MockBukkit.mock();
        plugin = MockBukkit.createMockPlugin();
        eventsCalled = new ArrayList<>();

        BlockBreakListener listener = new BlockBreakListener(mockGetIslandByLocation, mockGetUserByPlayer, mockCallEvent);
        server.getPluginManager().registerEvents(listener, plugin);

        block = new BlockMock();
        player = new PlayerMock(server, "TestPlayer");
        event = new BlockBreakEvent(block, player);
        callEvent = server.getPluginManager()::callEvent;
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    @DisplayName("does not cancel events outside of islands")
    @Test
    void testDoesNotCancelEventsOutsideOfIslands() {
        callEvent.accept(event);
        assertFalse(event.isCancelled());
    }

    @DisplayName("does not fire island leaves decay events outside of islands")
    @Test
    void testDoesNotFireLeavesDecayEventsOutsideOfIslands() {
        callEvent.accept(event);
        assertEquals(0, eventsCalled.size());
    }

}
