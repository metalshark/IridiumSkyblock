package listeners.bukkit;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.block.BlockMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.IslandConfiguration;
import com.iridium.iridiumskyblock.User;
import com.iridium.iridiumskyblock.enumerators.Permission;
import com.iridium.iridiumskyblock.events.island.IslandBlockBreakEvent;
import com.iridium.iridiumskyblock.listeners.bukkit.BlockBreakListener;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
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

    List<IslandBlockBreakEvent> eventsCalled;
    int expToDrop;
    boolean dropItems;
    boolean cancelCalledEvent;
    Consumer<Event> mockCallEvent = calledEvent -> {
        IslandBlockBreakEvent islandBlockBreakEvent = (IslandBlockBreakEvent) calledEvent;
        eventsCalled.add(islandBlockBreakEvent);
        islandBlockBreakEvent.setExperienceToDrop(expToDrop);
        islandBlockBreakEvent.setDropItems(dropItems);
        islandBlockBreakEvent.setCancelled(cancelCalledEvent);
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

    @DisplayName("on an island")
    @Nested
    class OnAnIsland {

        Map<World.Environment, World> worlds = new HashMap<>();
        boolean userForbidden;
        BiFunction<User, Permission, Boolean> isUserForbidden = (user, permission) -> userForbidden;

        @BeforeEach
        void setUp() {
            IslandConfiguration islandConfiguration = new IslandConfiguration();
            island = new Island(islandConfiguration, 0, 0, 0, worlds, isUserForbidden);
        }

        @DisplayName("with no user")
        @Nested
        class WithNoUser {

            @DisplayName("cancels the event")
            @Test
            void testCancelsEvent() {
                callEvent.accept(event);
                assertTrue(event.isCancelled());
            }

            @DisplayName("does not fire an island event")
            @Test
            void testDoesNotFireIslandEventOutsideOfIslands() {
                callEvent.accept(event);
                assertEquals(0, eventsCalled.size());
            }

        }

        @DisplayName("with a user")
        @Nested
        class WithAUser {

            @BeforeEach
            void setUp() {
                user = new User(player.getUniqueId());
            }

            @DisplayName("who is not permitted")
            @Nested
            class WhoIsNotPermitted {

                @BeforeEach
                void setUp() {
                    userForbidden = true;
                }

                @DisplayName("cancels the event")
                @Test
                void testCancelsEvent() {
                    callEvent.accept(event);
                    assertTrue(event.isCancelled());
                }

                @DisplayName("does not fire an island event")
                @Test
                void testDoesNotFireIslandEventOutsideOfIslands() {
                    callEvent.accept(event);
                    assertEquals(0, eventsCalled.size());
                }

            }

            @DisplayName("who is permitted")
            @Nested
            class WhoIsPermitted {

                @BeforeEach
                void setUp() {
                    userForbidden = false;
                }

                @DisplayName("does not the cancel event")
                @Test
                void testDoesNotCancelEvent() {
                    callEvent.accept(event);
                    assertFalse(event.isCancelled());
                }

                @DisplayName("fires an island event")
                @Test
                void testFireIslandEvent() {
                    callEvent.accept(event);
                    assertEquals(1, eventsCalled.size());
                }

                @DisplayName("relays experience to drop of an island event")
                @Test
                void testIslandEventExperience() {
                    expToDrop = 1;
                    callEvent.accept(event);
                    assumingThat(eventsCalled.size() == 1, () -> {
                        assertEquals(expToDrop, event.getExpToDrop());
                    });
                }

                @DisplayName("relays enabling item drops of an island event")
                @Test
                void testIslandEventDropItems() {
                    dropItems = true;
                    callEvent.accept(event);
                    assumingThat(eventsCalled.size() == 1, () -> {
                        assertTrue(event.isDropItems());
                    });
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

        }

    }

}
