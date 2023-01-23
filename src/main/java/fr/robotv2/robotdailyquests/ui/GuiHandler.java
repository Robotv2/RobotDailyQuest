package fr.robotv2.robotdailyquests.ui;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.PatternPane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.github.stefvanschie.inventoryframework.pane.util.Pattern;
import fr.robotv2.robotdailyquests.RobotDailyQuest;
import fr.robotv2.robotdailyquests.data.impl.ActiveQuest;
import fr.robotv2.robotdailyquests.data.impl.QuestPlayer;
import fr.robotv2.robotdailyquests.enums.QuestResetDelay;
import fr.robotv2.robotdailyquests.file.ConfigFile;
import fr.robotv2.robotdailyquests.util.ColorUtil;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Objects;

public class GuiHandler {

    private final RobotDailyQuest instance;

    private final ItemStack FILLER = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
    private final ConfigFile guiFile;

    public GuiHandler(RobotDailyQuest instance) {
        this.instance = instance;
        this.guiFile = new ConfigFile(instance, "gui.yml", true);
    }

    public void reloadGuiConfig() {
        this.guiFile.reload();
    }

    private ItemStack getItemFromChar(char character) {

        final ConfigurationSection section = this.guiFile
                .getConfiguration().getConfigurationSection("quest-gui.items." + String.valueOf(character).toUpperCase());

        if(section == null) {
            return null;
        }

        final Material material = Material.matchMaterial(section.getString("material", "BOOK"));
        final String name = section.getString("name");
        final List<String> lore = section.getStringList("lore");

        final ItemStack stack = new ItemStack(material != null ? material : Material.BOOK);
        final ItemMeta meta = Objects.requireNonNull(stack.getItemMeta());

        if(name != null) {
            meta.setDisplayName(ColorUtil.color(name));
        }

        meta.setLore(lore.stream().map(ColorUtil::color).toList());
        stack.setItemMeta(meta);

        return stack;
    }

    public void openMenu(Player player) {

        final ConfigurationSection section = this.guiFile.getConfiguration().getConfigurationSection("quest-gui");

        if(section == null) {
            return;
        }

        final int row = section.getInt("row", 5);
        final ChestGui gui =  new ChestGui(row, ColorUtil.color(section.getString("name")));

        gui.setOnGlobalClick(event -> event.setCancelled(true));

        final Pattern pattern = new Pattern(section.getStringList("pattern").toArray(new String[0]));
        final PatternPane pane = new PatternPane(0, 0, 9, row, pattern);

        final ConfigurationSection items = Objects.requireNonNull(section.getConfigurationSection("items"));

        for(String item : items.getKeys(false)) {

            final char character = item.toUpperCase().charAt(0);
            final ItemStack stack = this.getItemFromChar(character);

            if(stack == null) continue;
            pane.bindItem(character, new GuiItem(stack));
        }

        final StaticPane staticPane = new StaticPane(0, 0, 9, row, Pane.Priority.HIGH);

        for(QuestResetDelay delay : QuestResetDelay.VALUES) {

            int index = 0;
            final List<ActiveQuest> quests = QuestPlayer.getQuestPlayer(player).getActiveQuests(delay);

            if(quests.isEmpty()) {
                continue;
            }

            for(String slotStr : section.getStringList(delay.name().toLowerCase())) {

                try {

                    final int slot = Integer.parseInt(slotStr);
                    final ActiveQuest quest = quests.get(index);
                    ++index;

                    if(quest.getQuest() == null) {
                        continue;
                    }

                    final int x = slot % 9;
                    final int y = slot / 9;

                    final int progress = quest.getCurrentProgress();
                    staticPane.addItem(new GuiItem(quest.getQuest().getGuiItem(progress)), x, y);

                } catch (NumberFormatException | ArrayIndexOutOfBoundsException ignored) {}
            }
        }

        gui.addPane(staticPane);
        gui.addPane(pane);

        gui.show(player);
    }
}
