# Tiered [heavy beta]

Tiered is a Fabric mod inspired by (https://www.curseforge.com/minecraft/mc-mods/quality-tools)[Quality Tools]. Every tool you make will have a special modifier, as seen below:

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
  "style": {
    "color": "GREEN"
  },
  "attributes": [
    {
      "type": "generic.digSpeed",
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

Tiered currently provides 2 custom attributes: Dig Speed and Crit chance. Dig Speed increases the speed of your block breaking (think: haste), and Crit Chance offers an additional random chance to crit when using a tool.


### License
Tiered is licensed under MIT. You are free to use the code inside this repo as you want.
