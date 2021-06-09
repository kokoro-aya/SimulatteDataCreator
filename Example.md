Here is an example that the tool provides.

Notice that the tool provides a layout of point of view from the client's data structure, therefore you need to replace `block` to only `OPEN` and `BLOCKED` to make it conform to the request format to the server. 

```json
{
    "type": "default",
    "code": "",
    "grid": [
        [
            {
                "block": "OPEN",
                "level": 1,
                "biome": "SNOWY"
            },
            {
                "block": "OPEN",
                "level": 1,
                "biome": "SNOWY"
            },
            {
                "block": "OPEN",
                "level": 1,
                "biome": "SNOWY"
            },
            {
                "block": "OPEN",
                "level": 1,
                "biome": "PLAINS"
            },
            {
                "block": "OPEN",
                "level": 1,
                "biome": "PLAINS"
            },
            {
                "block": "MOUNTAIN",
                "level": 1,
                "biome": "PLAINS"
            }
        ],
        [
            {
                "block": "OPEN",
                "level": 1,
                "biome": "SNOWY"
            },
            {
                "block": "MOUNTAIN",
                "level": 1,
                "biome": "SNOWY"
            },
            {
                "block": "WATER",
                "level": 1,
                "biome": "PLAINS"
            },
            {
                "block": "TREE",
                "level": 1,
                "biome": "PLAINS"
            },
            {
                "block": "OPEN",
                "level": 1,
                "biome": "PLAINS"
            },
            {
                "block": "WATER",
                "level": 1,
                "biome": "RAINY"
            }
        ],
        [
            {
                "block": "OPEN",
                "level": 1,
                "biome": "SNOWY"
            },
            {
                "block": "OPEN",
                "level": 1,
                "biome": "SNOWY"
            },
            {
                "block": "OPEN",
                "level": 1,
                "biome": "PLAINS"
            },
            {
                "block": "OPEN",
                "level": 1,
                "biome": "PLAINS"
            },
            {
                "block": "OPEN",
                "level": 1,
                "biome": "RAINY"
            },
            {
                "block": "OPEN",
                "level": 1,
                "biome": "RAINY"
            }
        ],
        [
            {
                "block": "MOUNTAIN",
                "level": 1,
                "biome": "SNOWY"
            },
            {
                "block": "OPEN",
                "level": 1,
                "biome": "PLAINS"
            },
            {
                "block": "WATER",
                "level": 1,
                "biome": "PLAINS"
            },
            {
                "block": "MOUNTAIN",
                "level": 1,
                "biome": "PLAINS"
            },
            {
                "block": "TREE",
                "level": 1,
                "biome": "RAINY"
            },
            {
                "block": "OPEN",
                "level": 1,
                "biome": "RAINY"
            }
        ],
        [
            {
                "block": "TREE",
                "level": 1,
                "biome": "PLAINS"
            },
            {
                "block": "OPEN",
                "level": 1,
                "biome": "PLAINS"
            },
            {
                "block": "OPEN",
                "level": 1,
                "biome": "PLAINS"
            },
            {
                "block": "OPEN",
                "level": 1,
                "biome": "PLAINS"
            },
            {
                "block": "OPEN",
                "level": 1,
                "biome": "PLAINS"
            },
            {
                "block": "OPEN",
                "level": 1,
                "biome": "RAINY"
            }
        ]
    ],
    "gems": [
        {
            "x": 1,
            "y": 2
        },
        {
            "x": 4,
            "y": 2
        }
    ],
    "beepers": [
    ],
    "switches": [
        {
            "coo": {
                "x": 0,
                "y": 2
            },
            "on": true
        },
        {
            "coo": {
                "x": 2,
                "y": 2
            },
            "on": false
        },
        {
            "coo": {
                "x": 4,
                "y": 2
            },
            "on": false
        },
        {
            "coo": {
                "x": 3,
                "y": 4
            },
            "on": false
        },
        {
            "coo": {
                "x": 4,
                "y": 4
            },
            "on": true
        }
    ],
    "portals": [
    ],
    "locks": [
    ],
    "stairs": [
    ],
    "monsters": [
        {
            "x": 0,
            "y": 1
        },
        {
            "x": 5,
            "y": 3
        }
    ],
    "platforms": [
    ],
    "players": [
        {
            "id": 1,
            "x": 0,
            "y": 0,
            "dir": "DOWN",
            "role": "PLAYER",
            "stamina": 200
        }
    ],
    "gamingCondition": {
        "collectGemsBy": 2,
        "switchesOnBy": 4,
        "arriveAt": [
            {
                "x": 5,
                "y": 4
            }
        ],
        "monstersKilledLessThan": 1
    },
    "userCollision": true
}
```

A code that could make this playground win:

```kotlin
val p = Player()

fun Player.processRow(times: Int) {
    repeat (times) {
        if (this.isOnClosedSwitch) {
            this.toggleSwitch()
        }
        this.moveForward()
    }
}
p.turnLeft()
p.processRow(4)
p.turnRight()
repeat (2) { p.moveForward() }
p.turnRight()
p.collectGem()
p.processRow(3)
p.collectGem()
p.toggleSwitch()
p.turnLeft()
repeat (2) { p.moveForward() }
p.turnLeft()
p.processRow(4)
```