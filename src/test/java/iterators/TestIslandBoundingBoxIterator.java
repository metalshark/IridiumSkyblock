package iterators;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import com.iridium.iridiumskyblock.iterators.IslandBoundingBoxIterator;
import org.bukkit.block.BlockFace;
import org.bukkit.util.BoundingBox;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

@DisplayName("An island location iterator")
class TestIslandBoundingBoxIterator {

    private final int distance = 150;
    private final int countInDirection = 0;
    private final int maxInDirection = 1;

    private BoundingBox boundingBox;
    private IslandBoundingBoxIterator iterator;

    @BeforeEach
    void setUp() {
        ServerMock server = MockBukkit.mock();
        boundingBox = new BoundingBox(50, 0, 50, 100, 255, 100);
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    @DisplayName("increases count in the same direction")
    void testIncreasesCountInDirection() {
        iterator = new IslandBoundingBoxIterator(
            distance, boundingBox, BlockFace.NORTH, countInDirection, 2);
        iterator.next();
        assertEquals(1, iterator.getCountInDirection());
    }

    @Test
    @DisplayName("resets count when changing direction")
    void testResetsCountWhenChangingDirection() {
        iterator = new IslandBoundingBoxIterator(
            distance, boundingBox, BlockFace.NORTH, countInDirection, maxInDirection);
        iterator.next();
        assertEquals(0, iterator.getCountInDirection());
    }

    @Nested
    @DisplayName("has a getter for")
    class HasGetters {

        private final BlockFace direction = BlockFace.NORTH;

        @BeforeEach
        void setUp() {
            iterator = new IslandBoundingBoxIterator(
                distance, boundingBox, direction, countInDirection, maxInDirection);
        }

        @DisplayName("bounding box")
        @Test
        void testBoundingBox(){
            assertEquals(iterator.getBoundingBox(), boundingBox);
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
            iterator = new IslandBoundingBoxIterator(
                distance, boundingBox, BlockFace.WEST, countInDirection, maxInDirection);
            iterator.next();
            assertEquals(BlockFace.NORTH, iterator.getDirection());
        }

        @DisplayName("east")
        @Test
        void testEast() {
            iterator = new IslandBoundingBoxIterator(
                distance, boundingBox, BlockFace.NORTH, countInDirection, maxInDirection);
            iterator.next();
            assertEquals(BlockFace.EAST, iterator.getDirection());
        }

        @DisplayName("south")
        @Test
        void testSouth() {
            iterator = new IslandBoundingBoxIterator(
                distance, boundingBox, BlockFace.EAST, countInDirection, maxInDirection);
            iterator.next();
            assertEquals(BlockFace.SOUTH, iterator.getDirection());
        }

        @DisplayName("west")
        @Test
        void testWest() {
            iterator = new IslandBoundingBoxIterator(
                distance, boundingBox, BlockFace.SOUTH, countInDirection, maxInDirection);
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
            iterator = new IslandBoundingBoxIterator(
                distance, boundingBox, BlockFace.WEST, countInDirection, maxInDirection);
            iterator.next();
            assumingThat(iterator.getDirection() == BlockFace.NORTH, () ->
                assertEquals(2, iterator.getMaxInDirection())
            );
        }

        @DisplayName("south")
        @Test
        void testSouth() {
            iterator = new IslandBoundingBoxIterator(
                distance, boundingBox, BlockFace.EAST, countInDirection, maxInDirection);
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
            iterator = new IslandBoundingBoxIterator(
                distance, boundingBox, BlockFace.NORTH, countInDirection, maxInDirection);
            iterator.next();
            assumingThat(iterator.getDirection() == BlockFace.EAST, () ->
                assertEquals(1, iterator.getMaxInDirection())
            );
        }

        @DisplayName("west")
        @Test
        void testSouth() {
            iterator = new IslandBoundingBoxIterator(
                distance, boundingBox, BlockFace.SOUTH, countInDirection, maxInDirection);
            iterator.next();
            assumingThat(iterator.getDirection() == BlockFace.WEST, () ->
                assertEquals(1, iterator.getMaxInDirection())
            );
        }

    }

    @Nested
    @DisplayName("shifts position when facing")
    class ShiftsPositionWhenFacing {

        @DisplayName("north")
        @Test
        void testNorth() {
            iterator = new IslandBoundingBoxIterator(
                distance, boundingBox, BlockFace.NORTH, countInDirection, maxInDirection);
            iterator.next();
            assertEquals(200, iterator.getBoundingBox().getMinX());
            assertEquals(250, iterator.getBoundingBox().getMaxX());
            assertEquals(50, iterator.getBoundingBox().getMinZ());
            assertEquals(100, iterator.getBoundingBox().getMaxZ());
        }

        @DisplayName("east")
        @Test
        void testEast() {
            iterator = new IslandBoundingBoxIterator(
                distance, boundingBox, BlockFace.EAST, countInDirection, maxInDirection);
            iterator.next();
            assertEquals(50, iterator.getBoundingBox().getMinX());
            assertEquals(100, iterator.getBoundingBox().getMaxX());
            assertEquals(200, iterator.getBoundingBox().getMinZ());
            assertEquals(250, iterator.getBoundingBox().getMaxZ());
        }

        @DisplayName("south")
        @Test
        void testSouth() {
            iterator = new IslandBoundingBoxIterator(
                distance, boundingBox, BlockFace.SOUTH, countInDirection, maxInDirection);
            iterator.next();
            assertEquals(-100, iterator.getBoundingBox().getMinX());
            assertEquals(-50, iterator.getBoundingBox().getMaxX());
            assertEquals(50, iterator.getBoundingBox().getMinZ());
            assertEquals(100, iterator.getBoundingBox().getMaxZ());
        }

        @DisplayName("west")
        @Test
        void testWest() {
            iterator = new IslandBoundingBoxIterator(
                distance, boundingBox, BlockFace.WEST, countInDirection, maxInDirection);
            iterator.next();
            assertEquals(50, iterator.getBoundingBox().getMinX());
            assertEquals(100, iterator.getBoundingBox().getMaxX());
            assertEquals(-100, iterator.getBoundingBox().getMinZ());
            assertEquals(-50, iterator.getBoundingBox().getMaxZ());
        }

    }

}
