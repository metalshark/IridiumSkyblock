package iterators;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.WorldMock;
import com.iridium.iridiumskyblock.iterators.IslandLocationIterator;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

@DisplayName("An island location iterator")
class TestIslandLocationIterator {

    private final int distance = 123;
    private final int countInDirection = 0;
    private final int maxInDirection = 1;

    private Location location;
    private IslandLocationIterator iterator;

    @BeforeEach
    void setUp() {
        ServerMock server = MockBukkit.mock();
        WorldMock world = server.addSimpleWorld("TestIridiumSkyblock");
        location = new Location(world, 0, 0, 0);
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    @DisplayName("increases count in the same direction")
    void testIncreasesCountInDirection() {
        iterator = new IslandLocationIterator(
            distance, location, BlockFace.NORTH, countInDirection, 2);
        iterator.next();
        assertEquals(1, iterator.getCountInDirection());
    }

    @Test
    @DisplayName("resets count when changing direction")
    void testResetsCountWhenChangingDirection() {
        iterator = new IslandLocationIterator(
            distance, location, BlockFace.NORTH, countInDirection, maxInDirection);
        iterator.next();
        assertEquals(0, iterator.getCountInDirection());
    }

    @Nested
    @DisplayName("has a getter for")
    class HasGetters {

        private final BlockFace direction = BlockFace.NORTH;

        @BeforeEach
        void setUp() {
            iterator = new IslandLocationIterator(
                distance, location, direction, countInDirection, maxInDirection);
        }

        @DisplayName("location")
        @Test
        void testLocation(){
            assertEquals(iterator.getLocation(), location);
        }

        @DisplayName("direction")
        @Test
        void testDirection(){
            assertEquals(iterator.getDirection(), direction);
        }

        @DisplayName("count in direction")
        @Test
        void testCountInDirection(){
            assertEquals(iterator.getCountInDirection(), countInDirection);
        }

        @DisplayName("max in direction")
        @Test
        void testMaxInDirection(){
            assertEquals(iterator.getMaxInDirection(), maxInDirection);
        }

    }

    @Nested
    @DisplayName("changes direction to")
    class ChangesDirection {

        @DisplayName("north")
        @Test
        void testNorth() {
            iterator = new IslandLocationIterator(
                distance, location, BlockFace.WEST, countInDirection, maxInDirection);
            iterator.next();
            assertEquals(BlockFace.NORTH, iterator.getDirection());
        }

        @DisplayName("east")
        @Test
        void testEast() {
            iterator = new IslandLocationIterator(
                distance, location, BlockFace.NORTH, countInDirection, maxInDirection);
            iterator.next();
            assertEquals(BlockFace.EAST, iterator.getDirection());
        }

        @DisplayName("south")
        @Test
        void testSouth() {
            iterator = new IslandLocationIterator(
                distance, location, BlockFace.EAST, countInDirection, maxInDirection);
            iterator.next();
            assertEquals(BlockFace.SOUTH, iterator.getDirection());
        }

        @DisplayName("west")
        @Test
        void testWest() {
            iterator = new IslandLocationIterator(
                distance, location, BlockFace.SOUTH, countInDirection, maxInDirection);
            iterator.next();
            assertEquals(BlockFace.WEST, iterator.getDirection());
        }

    }

    @Nested
    @DisplayName("increases max direction when facing")
    class IncreasesMaxInDirection {

        @DisplayName("north")
        @Test
        void testNorth() {
            iterator = new IslandLocationIterator(
                distance, location, BlockFace.WEST, countInDirection, maxInDirection);
            iterator.next();
            assumingThat(iterator.getDirection() == BlockFace.NORTH, () ->
                assertEquals(2, iterator.getMaxInDirection())
            );
        }

        @DisplayName("south")
        @Test
        void testSouth() {
            iterator = new IslandLocationIterator(
                distance, location, BlockFace.EAST, countInDirection, maxInDirection);
            iterator.next();
            assumingThat(iterator.getDirection() == BlockFace.SOUTH, () ->
                assertEquals(2, iterator.getMaxInDirection())
            );
        }

    }

    @Nested
    @DisplayName("does not increase max direction when facing")
    class DoesNotIncreaseMaxInDirection {

        @DisplayName("east")
        @Test
        void testNorth() {
            iterator = new IslandLocationIterator(
                distance, location, BlockFace.NORTH, countInDirection, maxInDirection);
            iterator.next();
            assumingThat(iterator.getDirection() == BlockFace.EAST, () ->
                assertEquals(1, iterator.getMaxInDirection())
            );
        }

        @DisplayName("west")
        @Test
        void testSouth() {
            iterator = new IslandLocationIterator(
                distance, location, BlockFace.SOUTH, countInDirection, maxInDirection);
            iterator.next();
            assumingThat(iterator.getDirection() == BlockFace.WEST, () ->
                assertEquals(1, iterator.getMaxInDirection())
            );
        }

    }

}
