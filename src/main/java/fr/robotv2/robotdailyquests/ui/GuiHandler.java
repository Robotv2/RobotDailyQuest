package fr.robotv2.robotdailyquests.ui;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.PatternPane;
import com.github.stefvanschie.inventoryframework.pane.util.Pattern;
import fr.robotv2.robotdailyquests.RobotDailyQuest;
import fr.robotv2.robotdailyquests.data.impl.ActiveQuest;
import fr.robotv2.robotdailyquests.enums.QuestResetDelay;
import fr.robotv2.robotdailyquests.quest.Quest;
import fr.robotv2.robotdailyquests.util.ColorUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class GuiHandler {

    private final RobotDailyQuest instance;

    private final ItemStack FILLER = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);

    public GuiHandler(RobotDailyQuest instance) {
        this.instance = instance;
    }

    public void openBasicQuestGui(Player player, QuestResetDelay delay) {

        final ChestGui gui =  new ChestGui(6, ColorUtil.color("&7Quest &8- &e"  + delay.name().toLowerCase()));
        gui.setOnGlobalClick(event -> event.setCancelled(true));

        final Pattern pattern = new Pattern(
                "111111111",
                "100000001",
                "100000001",
                "100000001",
                "100000001",
                "111111111"
        );
        final PatternPane fill = new PatternPane(0 ,0 , 9, 6, pattern);;
        fill.bindItem('1', new GuiItem(FILLER));
        gui.addPane(fill);

        final OutlinePane items = new OutlinePane(1, 1, 7, 4, Pane.Priority.HIGH);
        final List<ActiveQuest> quests = instance.getQuestManager().getActiveQuests(delay);

        for(ActiveQuest activeQuest : quests) {

            final Quest quest = activeQuest.getQuest();

            if(quest == null) {
                continue;
            }

            final int progress = activeQuest.getCurrentProgress(player.getUniqueId());
            items.addItem(new GuiItem(quest.getGuiItem(progress)));
        }

        gui.addPane(items);
        gui.show(player);
    }

}
