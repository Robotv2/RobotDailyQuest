package fr.robotv2.robotdailyquests.quest;

import fr.robotv2.robotdailyquests.enums.QuestDifficulty;
import fr.robotv2.robotdailyquests.enums.QuestResetDelay;
import fr.robotv2.robotdailyquests.enums.QuestType;
import fr.robotv2.robotdailyquests.util.ColorUtil;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Quest {

    private final ConfigurationSection section;
    private final String id;

    private final String name;
    private final List<String> description;
    private final Material material;

    private final QuestResetDelay delay;
    private final QuestType type;
    private final QuestDifficulty difficulty;

    private final int requiredAmount;
    private final List<String> rewards;

    public Quest(ConfigurationSection section) {
        this.section = section;

        this.id = section.getName();
        this.name = section.getString("name");
        this.description = section.getStringList("description");
        this.material = Material.matchMaterial(section.getString("menu_item", "BOOK"));

        this.type = QuestType.valueOf(section.getString("quest_type"));
        this.difficulty = QuestDifficulty.valueOf(section.getString("quest_difficulty"));
        this.delay = QuestResetDelay.valueOf(section.getString("quest_reset_delay"));

        this.requiredAmount = section.getInt("required_amount", 0);
        this.rewards = section.getStringList("rewards");
    }

    public ConfigurationSection getSection() {
        return this.section;
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public List<String> getDescription() {
        return this.description;
    }

    public Material getMaterial() {
        return this.material;
    }

    public QuestDifficulty getDifficulty() {
        return this.difficulty;
    }

    public ItemStack getGuiItem(int progress) {

        final ItemStack itemStack = new ItemStack(this.getMaterial());
        final ItemMeta meta = Objects.requireNonNull(itemStack.getItemMeta());
        final List<String> description = new ArrayList<>(this.description);

        meta.setDisplayName(ColorUtil.color(this.name));

        description.add(" ");
        description.add("&7Progression: &e" + progress + "&8/&e" + this.requiredAmount);

        meta.setLore(description.stream().map(ColorUtil::color).toList());

        if(progress >= requiredAmount) {
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.addEnchant(Enchantment.ARROW_FIRE, 1, true);
        }

        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public QuestType getType() {
        return this.type;
    }

    public QuestResetDelay getDelay() {
        return this.delay;
    }

    public List<String> getRewards() {
        return this.rewards;
    }

    public int getRequiredAmount() {
        return this.requiredAmount;
    }

    @Override
    public boolean equals(Object object) {

        if(!(object instanceof final Quest target)) {
            return false;
        }

        return Objects.equals(this.id, target.id);
    }
}
