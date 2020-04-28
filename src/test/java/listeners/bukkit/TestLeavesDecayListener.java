package listeners.bukkit;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.MockPlugin;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.block.BlockMock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.IslandConfiguration;
import com.iridium.iridiumskyblock.User;
import com.iridium.iridiumskyblock.enumerators.Permission;
import com.iridium.iridiumskyblock.events.island.IslandLeavesDecayEvent;
import com.iridium.iridiumskyblock.listeners.bukkit.LeavesDecayListener;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.block.LeavesDecayEvent;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

@DisplayName("A leaves decay listener")
class TestLeavesDecayListener {

    ServerMock server;
    MockPlugin plugin;
    Block block;

    LeavesDecayEvent event;
    Consumer<Event> callEvent;

    Island island;
    Function<Location, Island> mockGetIslandByLocation = loc -> island;

    List<IslandLeavesDecayEvent> eventsCalled;
    boolean cancelCalledEvent;
    Consumer<Event> mockCallEvent = calledEvent -> {
        IslandLeavesDecayEvent islandLeavesDecayEvent = (IslandLeavesDecayEvent) calledEvent;
        eventsCalled.add(islandLeavesDecayEvent);
        islandLeavesDecayEvent.setCancelled(cancelCalledEvent);
    };

    @BeforeEach
    void setUp() {
        server = MockBukkit.mock();
        plugin = MockBukkit.createMockPlugin();
        eventsCalled = new ArrayList<>();

        LeavesDecayListener listener = new LeavesDecayListener(mockGetIslandByLocation, mockCallEvent);
        server.getPluginManager().registerEvents(listener, plugin);

        block = new BlockMock();
        event = new LeavesDecayEvent(block);
        callEvent = server.getPluginManager()::callEvent;
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    @DisplayName("outside an island")
    @Nested
    class OutsideAnIsland {

        @DisplayName("does not the cancel event")
        @Test
        void testDoesNotCancelEvent() {
            callEvent.accept(event);
            assertFalse(event.isCancelled());
        }

        @DisplayName("does not fire island event")
        @Test
        void testDoesNotFireIslandEvent() {
            callEvent.accept(event);
            assertEquals(0, eventsCalled.size());
        }

    }

    @DisplayName("when an island")
    @Nested
    class WhenAnIsland {

        IslandConfiguration islandConfiguration;
        Map<World.Environment, World> worlds = new HashMap<>();
        BiFunction<User, Permission, Boolean> isUserForbidden = (user, permission) -> true;

        @BeforeEach
        void setUp() {
            islandConfiguration = new IslandConfiguration();
            island = new Island(islandConfiguration, 0, 0, 0, worlds, isUserForbidden);
        }

        @DisplayName("enables leaf decay")
        @Nested
        class EnablesLeafDelay {

            @BeforeEach
            void setUp() {
                islandConfiguration.setLeavesDecayEnabled(true);
            }

            @DisplayName("does not cancel the event")
            @Test
            void testEventCancelled() {
                callEvent.accept(event);
                assertFalse(event.isCancelled());
            }

            @DisplayName("fires an island event")
            @Test
            void testIslandEventFired() {
                callEvent.accept(event);
                assertEquals(1, eventsCalled.size());
                IslandLeavesDecayEvent islandEvent = (IslandLeavesDecayEvent) eventsCalled.get(0);
                assertEquals(block, islandEvent.getBlock());
                assertEquals(island, islandEvent.getIsland());
            }

            @DisplayName("relays cancellation of an island event")
            @Test
            void testIslandEventCancellation() {
                cancelCalledEvent = true;
                callEvent.accept(event);
                assumingThat(eventsCalled.size() == 1, () -> {
                    assertTrue(event.isCancelled());
                });
            }

        }

        @DisplayName("disables leaf decay")
        @Nested
        class DisablesLeafDelay {

            @BeforeEach
            void setUp() {
                islandConfiguration.setLeavesDecayEnabled(false);
                callEvent.accept(event);
            }

            @DisplayName("cancels the leaves decay event")
            @Test
            void testEventCancelled() {
                assertTrue(event.isCancelled());
            }

            @DisplayName("does not fire an island leaves decay event")
            @Test
            void testIslandEventFired() {
                assertEquals(0, eventsCalled.size());
            }

        }

    }

}
