package fr.robotv2.robotdailyquests.listeners.quest;

import fr.robotv2.robotdailyquests.RobotDailyQuest;
import fr.robotv2.robotdailyquests.data.impl.LoadedQuest;
import fr.robotv2.robotdailyquests.events.QuestDoneEvent;
import fr.robotv2.robotdailyquests.events.QuestIncrementEvent;
import fr.robotv2.robotdailyquests.quest.Quest;
import fr.robotv2.robotdailyquests.util.ColorUtil;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class QuestListener implements Listener {

    private final RobotDailyQuest instance;
    public QuestListener(RobotDailyQuest instance) {
        this.instance = instance;
    }

    @EventHandler
    public void onQuestDone(QuestDoneEvent event) {

        final Player player = event.getPlayer();
        final LoadedQuest loadedQuest = event.getQuest();
        final Quest quest = loadedQuest.getQuest();

        if(quest == null) {
            return;
        }

        player.sendMessage(ColorUtil.color("&aVous venez de terminer la quête: " + quest.getName()));
    }

    @EventHandler
    public void onQuestIncrement(QuestIncrementEvent event) {

        final Player player = event.getPlayer();
        final LoadedQuest loadedQuest = event.getQuest();
        final Quest quest = loadedQuest.getQuest();

        if(quest == null) {
            return;
        }

        final String message = "&f" + quest.getName() + " &8| &e" + event.getPlayerProgression() + "&7/&e" + event.getAmountRequired();
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ColorUtil.color(message)));
    }
}
