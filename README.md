# Tiered

Tiered is a Fabric mod inspired by [Quality Tools](https://www.curseforge.com/minecraft/mc-mods/quality-tools). Every tool you make will have a special modifier, as seen below:

<img src="resources/legendary_chestplate.png" width="400">

### Customization

Tiered is entirely data-driven, which means you can add, modify, and remove modifiers as you see fit. The base path for modifiers is `data/modid/item_attributes`, and tiered modifiers are stored under the modid of tiered. Here's an example modifier called "Hasteful," which grants additional dig speed when any of the valid tools are held:
```json
{
  "id": "tiered:hasteful",
  "verifiers": [
    {
      "tag": "fabric:pickaxes"
    },
    {
      "tag": "fabric:shovels"
    },
    {
      "tag": "fabric:axes"
    }
  ],
  "weight": 10,
  "style": {
    "color": "GREEN"
  },
  "attributes": [
    {
      "type": "generic.dig_speed",
      "modifier": {
        "name": "tiered:hasteful",
        "operation": "MULTIPLY_TOTAL",
        "amount": 0.10
      },
      "optional_equipment_slots": [
        "MAINHAND"
      ]
    }
  ]
}
```

Tiered currently provides 3 custom attributes: Dig Speed, Crit chance and Durability. Dig Speed increases the speed of your block breaking (think: haste), Crit Chance offers an additional random chance to crit when using a tool and Durability increases, who would have thought it, the durability of an item.

### Verifiers

A verifier (specified in the "verifiers" array of your modifier json file) defines whether or not a given tag or tool is valid for the modifier. 

A specific item ID can be specified with:
```json
"id": "minecraft:apple"
```

and a tag can be specified with:
```json
"tag": "fabric:helmets"
```

Tiered provides 4 armor tags (`fabric:helmets`, `fabric:chestplates`, `fabric:leggings`, `fabric:boots`) and 6 tool tags (`fabric:axes`, `faric:hoes`, `fabric:pickaxes`, `fabric:shields`, `fabric:shovels` and `fabric:swords`).

### Weight

The weight determines the commonness of the tier. Higher weights increase the chance of being applied on the item and vice versa.


### License
Tiered is licensed under MIT. You are free to use the code inside this repo as you want.