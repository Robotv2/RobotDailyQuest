package fr.robotv2.robotdailyquests.ui;

import com.google.common.collect.Iterators;
import dev.triumphteam.gui.builder.gui.SimpleBuilder;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.components.GuiType;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import fr.robotv2.robotdailyquests.RobotDailyQuest;
import fr.robotv2.robotdailyquests.data.impl.LoadedQuest;
import fr.robotv2.robotdailyquests.enums.QuestResetDelay;
import fr.robotv2.robotdailyquests.quest.Quest;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;

import java.util.Iterator;

public class GuiHandler {

    private final RobotDailyQuest instance;
    private final GuiItem filler = ItemBuilder
            .from(Material.BLACK_STAINED_GLASS_PANE)
            .name(Component.text(" "))
            .flags(ItemFlag.HIDE_ATTRIBUTES).asGuiItem();

    public GuiHandler(RobotDailyQuest instance) {
        this.instance = instance;
    }

    public void openBasicQuestGui(Player player, QuestResetDelay delay) {

        final SimpleBuilder builder = Gui.gui(GuiType.CHEST);
        final int row = 9 * 6;

        builder.title(Component.text("Quest >> " + delay));
        builder.rows(9 * 6);
        builder.disableAllInteractions();

        final Gui gui = builder.create();
        gui.getFiller().fillBorder(filler);

        final Iterator<LoadedQuest> quests = instance.getQuestManager().getLoadedQuests(delay).iterator();

        for(int i = 0; i < row; i++) {

            final GuiItem item = gui.getGuiItem(i);

            if(item != null) {
                continue;
            }

            if(!quests.hasNext()) {
                break;
            }

            final LoadedQuest loadedQuest = quests.next();
            final Quest quest = loadedQuest.getQuest();

            if(quest == null) {
                continue;
            }

            final int progress = loadedQuest.getCurrentProgress(player.getUniqueId());
            gui.setItem(i, ItemBuilder.from(quest.getGuiItem(progress)).asGuiItem());
        }

        gui.open(player);
    }

}
