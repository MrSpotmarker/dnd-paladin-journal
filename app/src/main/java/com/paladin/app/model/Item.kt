package com.paladin.app.model

import kotlinx.serialization.Serializable

@Serializable
enum class ItemType {
    WEAPON,
    ARMOR,
    SHIELD,
    MAGIC_ITEM,
    POTION,
    GEAR
}

@Serializable
enum class ArmorType {
    LIGHT,
    MEDIUM,
    HEAVY,
    SHIELD
}

@Serializable
enum class WeaponMastery(val propertyName: String, val description: String) {
    CLEAVE("Cleave", "Triffst du eine Kreatur im Nahkampf, kannst du einen zweiten Angriff gegen eine benachbarte Kreatur würfeln."),
    GRAZE("Graze", "Verfehlst du einen Angriff, fügst du dem Ziel dennoch Schaden in Höhe deines Attributsmodifikators zu."),
    NICK("Nick", "Du kannst den zusätzlichen Angriff der Light-Eigenschaft als Teil der Angriffsaktion ausführen statt als Bonus-Aktion."),
    PUSH("Push", "Triffst du eine Kreatur, kannst du sie bis zu 10 Fuß (ca. 3 Meter) geradlinig von dir wegschieben (Größe bis Large)."),
    SAP("Sap", "Triffst du eine Kreatur, hat sie Nachteil auf ihren nächsten Angriffswurf vor dem Beginn deines nächsten Zugs."),
    SLOW("Slow", "Triffst du eine Kreatur und verursachst Schaden, verringert sich ihre Bewegungsrate um 10 Fuß bis zu deinem nächsten Zug."),
    TOPPLE("Topple", "Triffst du eine Kreatur, muss sie einen CON-Rettungswurf (DC 8 + PB + Mod) bestehen oder geht zu Boden (Prone)."),
    VEX("Vex", "Triffst du eine Kreatur und verursachst Schaden, hast du Vorteil auf deinen nächsten Angriffswurf gegen sie.")
}

@Serializable
sealed class ItemEffect {
    @Serializable
    data class AcFlat(val ac: Int) : ItemEffect()

    @Serializable
    data class AcBonus(val bonus: Int) : ItemEffect()

    @Serializable
    data class AbilityOverride(val ability: Ability, val score: Int) : ItemEffect()

    @Serializable
    data class AbilityBonus(val ability: Ability, val bonus: Int) : ItemEffect()

    @Serializable
    data class AttackBonus(val bonus: Int) : ItemEffect()

    @Serializable
    data class DamageBonus(val bonus: Int) : ItemEffect()

    @Serializable
    data class SavingThrowBonus(val bonus: Int) : ItemEffect()
}

@Serializable
data class Item(
    val id: String,
    val name: String,
    val type: ItemType,
    val description: String = "",
    val cost: String = "0 gp",
    val weightLbs: Double = 0.0,
    val quantity: Int = 1,
    val isEquipped: Boolean = false,
    val requiresAttunement: Boolean = false,
    val isAttuned: Boolean = false,
    
    // Rüstungsdetails (falls Armor/Shield)
    val armorType: ArmorType? = null,
    val baseAc: Int = 0,
    val stealthDisadvantage: Boolean = false,
    val minStrength: Int = 0,

    // Waffendetails (falls Weapon)
    val damageDice: String = "1d8",
    val damageType: String = "Slashing",
    val isFinesse: Boolean = false,
    val isHeavy: Boolean = false,
    val isLight: Boolean = false,
    val isReach: Boolean = false,
    val isTwoHanded: Boolean = false,
    val isVersatile: Boolean = false,
    val versatileDamageDice: String? = null,
    val mastery: WeaponMastery? = null,

    // Effekte wenn ausgerüstet/attuned
    val effects: List<ItemEffect> = emptyList()
)
