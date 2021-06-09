import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

enum class Biome {
    SNOWY, PLAINS, RAINY, HELL
}

enum class Block {
    OPEN, HILL, WATER, TREE, DESERT, HOME, MOUNTAIN, STONE, LOCK, STAIR, VOID,
}

enum class Direction {
    UP, DOWN, LEFT, RIGHT
}

enum class Role {
    PLAYER, SPECIALIST,
}

@Serializable
data class Tile(var block: Block, var level: Int, var biome: Biome)

@Serializable
data class Coordinate(val x: Int, val y: Int)

@Serializable
data class Switch(val coo: Coordinate, val on: Boolean)

@Serializable
data class Portal(val coo: Coordinate, val dest: Coordinate, val isActive: Boolean)

@Serializable
data class Lock(val coo: Coordinate, val controlled: List<Coordinate>)

@Serializable
data class Stair(val coo: Coordinate, val dir: Direction)

@Serializable
data class Platform(val coo: Coordinate, val level: Int)

@Serializable
data class Player(val id: Int, val x: Int, val y: Int, val dir: Direction, val role: Role, val stamina: Int)

@Serializable
data class GamingCondition(
    val collectGemsBy: Int? = null,
    val switchesOnBy: Int? = null,
    val arriveAt: List<Coordinate>? = null,
    val monstersKilled: Int? = null,
    val monstersKilledLessThan: Int? = null,
    val noSameTileRepassed: Boolean? = null,
    val endGameAfter: Int? = null
)

@Serializable
data class Data(
    val type: String,
    val code: String,
    val grid: List<List<Tile>>,
    val gems: List<Coordinate>,
    val beepers: List<Coordinate>,
    val switches: List<Switch>,
    val portals: List<Portal>,
    val locks: List<Lock>,
    val stairs: List<Stair>,
    val monsters: List<Coordinate>,
    val platforms: List<Platform>,
    val players: List<Player>,
    val gamingCondition: GamingCondition,
    val userCollision: Boolean,
)

fun main(args: Array<String>) {
    val type = "default"

    println("> Please specify your playground's depth and length, separated by space")
    val (dep, len) = readLine()!!.split(" ").filterNot { it.isBlank() }.map { Integer.parseInt(it) }

    val grid = List(dep) { List(len) { Tile(Block.OPEN, 1, Biome.PLAINS) } }

    println("> Please specify your playground's block layout, separated by space")
    println("> You could choose blocks from the following: ")
    println(">  OPEN, HILL, WATER, TREE, DESERT, HOME")
    println(">  MOUNTAIN, STONE, LOCK, STAIR, VOID")
    println("> Enter '###' to skip, this will create a map with only open")

    for (i in 0 until dep) {
        print("line $i / $dep > ")
        val line = readLine()!!
        if (i == 0 && line == "###") break
        else {
            val decoded = line.split(" ").filterNot { it.isBlank() }.map {
                when (it.uppercase()) {
                    "OPEN" -> Block.OPEN
                    "HILL" -> Block.HILL
                    "WATER" -> Block.WATER
                    "TREE" -> Block.TREE
                    "DESERT" -> Block.DESERT
                    "HOME" -> Block.HOME
                    "MOUNTAIN" -> Block.MOUNTAIN
                    "STONE" -> Block.STONE
                    "LOCK" -> Block.LOCK
                    "STAIR" -> Block.STAIR
                    "VOID" -> Block.VOID
                    else -> throw Exception("Unknown block, contact the admin to add new blocks")
                }
            }
            grid[i].forEachIndexed { i, t -> t.block = decoded[i] }
        }
    }

    println("> Please specify your playground's level layout, separated by space")
    println("> Enter '###' to skip, this will create a map with only level 1")
    for (i in 0 until dep) {
        print("line $i / $dep > ")
        val line = readLine()!!
        if (i == 0 && line == "###") break
        else {
            val decoded = line.split(" ").filterNot { it.isBlank() }.map { Integer.parseInt(it) }
            grid[i].forEachIndexed { i, t -> t.level = decoded[i] }
        }
    }

    println("> Please specify your playground's biome layout, separated by space")
    println("> You could choose biomes from the following: ")
    println(">  SNOWY, PLAINS, RAINY, HELL")
    println("> Enter '###' to skip, this will create a map with only biome PLAINS")
    for (i in 0 until dep) {
        print("line $i / $dep > ")
        val line = readLine()!!
        if (i == 0 && line == "###") break
        else {
            val decoded = line.split(" ").filterNot { it.isBlank() }.map {
                when (it.uppercase()) {
                    "SNOWY" -> Biome.SNOWY
                    "PLAINS" -> Biome.PLAINS
                    "RAINY" -> Biome.RAINY
                    "HELL" -> Biome.HELL
                    else -> throw Exception("Unknown biome, contact the admin to add new blocks")
                }
            }
            grid[i].forEachIndexed { i, t -> t.biome = decoded[i] }
        }
    }

    val gems = mutableListOf<Coordinate>()

    println("> Now please specify your gems, line by line, x and y separated by ',', end with new line of '###'")
    var line = readLine()!!
    while (line != "###") {
        val (x, y) = line.split(",").map { Integer.parseInt(it) }
        check(x in grid[0].indices && y in grid.indices) { "Error: Gem position out of bound" }
        gems.add(Coordinate(x, y))
        line = readLine()!!
    }

    val beepers = mutableListOf<Coordinate>()

    println("> Now please specify your beepers, line by line, x and y separated by ',', end with new line of '###'")
    line = readLine()!!
    while (line != "###") {
        val (x, y) = line.split(",").map { Integer.parseInt(it) }
        check(x in grid[0].indices && y in grid.indices) { "Error: Beeper position out of bound" }
        beepers.add(Coordinate(x, y))
        line = readLine()!!
    }

    val switches = mutableListOf<Switch>()

    println("> Now please specify your switches, line by line, x and y separated by ',', end with new line of '###")
    println("> You split with `on` (true or false) by using ':'")
    line = readLine()!!
    while (line != "###") {
        val (coo, on) = line.split(":")
        val (x, y) = coo.split(",").map { Integer.parseInt(it) }
        check(switches.none{ it.coo == Coordinate(x, y) }) { "Error: duplicated coordinate found in switches declaration" }
        check(x in grid[0].indices && y in grid.indices) { "Error: Switch position out of bound" }
        val o = on.toBoolean()
        switches.add(Switch(Coordinate(x, y), o))
        line = readLine()!!
    }

    val portals = mutableListOf<Portal>()

    println("> Now please specify your portals, line by line, x and y separated by ',', end with new line of '###'")
    println("> For each portal, you use ':' to separate its coo and its dest and if it's active")
    line = readLine()!!
    while (line != "###") {
        val (coo, dest, active) = line.split(":")
        val (x1, y1) = coo.split(",").map { Integer.parseInt(it) }
        val (x2, y2) = dest.split(",").map { Integer.parseInt(it) }
        check(portals.none{ it.coo == Coordinate(x1, y1) }) { "Error: duplicated coordinate found in switches declaration[1]\n" +
                "A tile can only have one switch, whether it's starting or arriving" }
        check(portals.none{ it.coo == Coordinate(x2, y2) }) { "Error: duplicated coordinate found in switches declaration[2]\n" +
                "A tile can only have one switch, whether it's starting or arriving" }
        check(portals.none{ it.dest == Coordinate(x1, y1) }) { "Error: duplicated coordinate found in switches declaration[3]\n" +
                "A tile can only have one switch, whether it's starting or arriving" }
        check(portals.none{ it.dest == Coordinate(x2, y2) }) { "Error: duplicated coordinate found in switches declaration[4]\n" +
                "A tile can only have one switch, whether it's starting or arriving" }
        check(x1 in grid[0].indices && y1 in grid.indices) { "Error: Portal starting position out of bound" }
        check(x2 in grid[0].indices && y2 in grid.indices) { "Error: Portal ending position out of bound" }

        val ia = active.toBoolean()
        portals.add(Portal(Coordinate(x1, y1), Coordinate(x2, y2), ia))
        line = readLine()!!
    }

    val stairs = mutableListOf<Stair>()

    println("> Now please specify you stairs, line by line, x and y separated by ',', end with new line of '###'")
    println("> For each stair, you use ':' to separate its coo and its direction")
    line = readLine()!!
    while (line != "###") {
        val (coo, dir) = line.split(":")
        val (x, y) = coo.split(",").map { Integer.parseInt(it) }
        check(stairs.none { it.coo == Coordinate(x, y) }) { "Error: duplicated coordinate found in stairs declaration" }
        check(x in grid[0].indices && y in grid.indices) { "Error: Stair position out of bound" }
        val d = when (dir.uppercase()) {
            "UP" -> Direction.UP
            "DOWN" -> Direction.DOWN
            "LEFT" -> Direction.LEFT
            "RIGHT" -> Direction.RIGHT
            else -> throw Exception("Unknown direction")
        }
        stairs.add(Stair(Coordinate(x, y), d))
        line = readLine()!!
    }

    println("> For the moment locks/platforms are unsupported.")

    val monsters = mutableListOf<Coordinate>()

    println("> Now please specify your monsters, line by line, x and y separated by ',', end with new line of '###'")
    line = readLine()!!
    while (line != "###") {
        val (x, y) = line.split(",").map { Integer.parseInt(it) }
        check(monsters.none { it == Coordinate(x, y) }) { "Error: duplicated coordinate found in monsters declaration" }
        check(x in grid[0].indices && y in grid.indices) { "Error: Monster position out of bound" }
        monsters.add(Coordinate(x, y))
        line = readLine()!!
    }

    val players = mutableListOf<Player>()

    println("> Now please specify your players, line by line, each entry separated by ':', end with new line of '###'")
    println("> entries follows the following format:")
    println("> x:y:direction:role")
    println("> for direction you can choose from UP, DOWN, LEFT and RIGHT")
    println("> for role you can choose from PLAYER or SPECIALIST")
    line = readLine()!!
    var id = 1;
    while (line != "###") {
        val (_x, _y, _dir, _role) = line.split(":")
        val (x, y) = listOf(_x, _y).map { Integer.parseInt(it) }
        check(players.none { it.x == x && it.y == y }) { "Error: duplicated coordinate found in player declaration\n" +
                "Currently duplicated players in same tile is not supported." }
        check(x in grid[0].indices && y in grid.indices) { "Error: Monster position out of bound" }
        val dir = when (_dir.uppercase()) {
            "UP" -> Direction.UP
            "DOWN" -> Direction.DOWN
            "LEFT" -> Direction.LEFT
            "RIGHT" -> Direction.RIGHT
            else -> throw Exception("Unknown direction")
        }
        val role = when (_role.uppercase()) {
            "PLAYER" -> Role.PLAYER
            "SPECIALIST" -> Role.SPECIALIST
            else -> throw Exception("Unknown role")
        }
        players.add(Player(id++, x, y, dir, role, 200))
        line = readLine()!!
    }
    check(players.size > 0) { "Error: At least a player should be specified" }

    val conditions = mutableSetOf<Int>()

    println("> Now please specify the game's winning/losing conditions, separated by space.\n" +
            "> Here are conditions that you can use:")
    println("> Winning conditions")
    println("> 1. collectGemsBy: Int 2. switchesOnBy: Int 3. arriveAt: List<Coordinate> ")
    println("> 4. monstersKilled: Int 5. monstersKilledLessThan")
    println("> Losing conditions")
    println("> 6. noSameTileRepassed: Bool 7. endGameAfter: Int")
    println("> Choose all numbers of condition that you want to apply in this game, if none, please enter '###'")
    line = readLine()!!
    if (line != "###") {
        line.split(" ").filterNot { it.isBlank() }.map { Integer.parseInt(it) }.forEach { conditions.add(it) }
    }

    var collectGemsBy: Int? = null
    var switchesOnBy: Int? = null
    var arriveAt: List<Coordinate>? = null
    var monstersKilled: Int? = null
    var monstersKilledLessThan: Int? = null
    var noSameTileRepassed: Boolean? = null
    var endGameAfter: Int? = null

    if (conditions.contains(1)) {
        println("> Please specify an Int for which players should collect more or as many gems as it")
        collectGemsBy = readLine()!!.let { Integer.parseInt(it) }
    }
    if (conditions.contains(2)) {
        println("> Please specify an Int for which players should toggle more or as many opened switch as it")
        switchesOnBy = readLine()!!.let { Integer.parseInt(it) }
    }
    if (conditions.contains(3)) {
        println("> Please specify a List of Coordinate for which players should arrive at")
        println("> Each line is a pair of x, y separated by ',', the last line should be '###' to indicate that it ends")
        arriveAt = mutableListOf()
        line = readLine()!!
        while (line != "###") {
            val (x, y) = line.split(",").map { Integer.parseInt(it) }
            check(arriveAt.none { it == Coordinate(x, y) }) { "Error: duplicated coordinate found in arriving declaration" }
            check(x in grid[0].indices && y in grid.indices) { "Error: Arriving position out of bound" }
            arriveAt.add(Coordinate(x, y))
            line = readLine()!!
        }
    }
    if (conditions.contains(4)) {
        println("> Please specify an Int for which players should kill more or as many monsters as it")
        monstersKilled = readLine()!!.let { Integer.parseInt(it) }
    }
    if (conditions.contains(5)) {
        println("> Please specify an Int for which players should kill less monsters than it")
        monstersKilledLessThan = readLine()!!.let { Integer.parseInt(it) }
    }
    if (conditions.contains(6)) {
        println("> Please specify if player could repass the same tile (y/n)")
        noSameTileRepassed = readLine()!!.let { when (it.lowercase()) {
                "y" -> false
                "n" -> true
                else -> throw Exception("You must select from yes (y) or no (n)")
            }
        }
    }
    if (conditions.contains(7)) {
        println("> Please specify a turn limit after which the game fails")
        endGameAfter = readLine()!!.let { Integer.parseInt(it) }
    }

    println("> All data collected, processing the json generation")

    val gameCondition = GamingCondition(
        collectGemsBy, switchesOnBy, arriveAt, monstersKilled, monstersKilledLessThan, noSameTileRepassed, endGameAfter
    )

    val data = Data(
        type, "", grid, gems, beepers, switches, portals, listOf(), stairs, monsters, listOf(), players, gameCondition, true
    )

    val output = Json {
        prettyPrint = true
    }.encodeToString(data)

    println(output)

    println("> Specify the path of file you want to write into")
    val path = readLine()!!

    File(path).writeText(output)

    println("> All is done. Program will exit...")
}