package fr.robotv2.robotdailyquests.listeners.quest;

import fr.robotv2.robotdailyquests.RobotDailyQuest;
import fr.robotv2.robotdailyquests.data.impl.ActiveQuest;
import fr.robotv2.robotdailyquests.data.impl.QuestPlayer;
import fr.robotv2.robotdailyquests.enums.QuestResetDelay;
import fr.robotv2.robotdailyquests.events.DelayQuestDoneEvent;
import fr.robotv2.robotdailyquests.events.QuestDoneEvent;
import fr.robotv2.robotdailyquests.events.QuestIncrementEvent;
import fr.robotv2.robotdailyquests.quest.Quest;
import fr.robotv2.robotdailyquests.quest.QuestRewardProcessor;
import fr.robotv2.robotdailyquests.util.ColorUtil;
import fr.robotv2.robotdailyquests.util.PlaceholderUtil;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public class QuestListener implements Listener {

    private final RobotDailyQuest instance;
    public QuestListener(RobotDailyQuest instance) {
        this.instance = instance;
    }

    private void sendCongratulationTitle(Player player, Quest quest) {

        final int fadeIn = this.instance.getConfig().getInt("cosmetics.title.fade-in", 10);
        final int stay = this.instance.getConfig().getInt("cosmetics.title.stay", 20);
        final int fadeOut = this.instance.getConfig().getInt("cosmetics.title.fade-out", 10);

        String title = this.instance.getConfig().getString("cosmetics.title.title", "&bBravo, %player%");
        String subtitle = this.instance.getConfig().getString("cosmetics.title.subtitle", "&aQuête terminée: &7%quest-name%&7 &a!");

        title = PlaceholderUtil.PLAYER_PLACEHOLDER.apply(player, title);
        title = PlaceholderUtil.QUEST_PLACEHOLDER.apply(quest, title);
        title = ColorUtil.color(title);

        subtitle = PlaceholderUtil.PLAYER_PLACEHOLDER.apply(player, subtitle);
        subtitle = PlaceholderUtil.QUEST_PLACEHOLDER.apply(quest, subtitle);
        subtitle = ColorUtil.color(subtitle);

        player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
    }

    @EventHandler
    public void onQuestDone(QuestDoneEvent event) {

        final Player player = event.getPlayer();
        final ActiveQuest activeQuest = event.getQuest();
        final Quest quest = activeQuest.getQuest();

        if(quest == null) {
            return;
        }

        if(this.instance.getConfig().getBoolean("cosmetics.title.enabled")) {
            this.sendCongratulationTitle(player, quest);
        }

        final QuestPlayer questPlayer = QuestPlayer.getQuestPlayer(player);
        final List<ActiveQuest> quests = questPlayer.getActiveQuests(quest.getDelay());

        if(quests.stream().allMatch(ActiveQuest::isDone)) {
            Bukkit.getPluginManager().callEvent(new DelayQuestDoneEvent(player, activeQuest));
        }
    }

    @EventHandler
    public void onQuestIncrement(QuestIncrementEvent event) {

        final Player player = event.getPlayer();
        final ActiveQuest activeQuest = event.getQuest();
        final Quest quest = activeQuest.getQuest();

        if(quest == null) {
            return;
        }

        final String message = "&f" + quest.getName() + " &8| &e" + event.getPlayerProgression() + "&7/&e" + event.getAmountRequired();
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ColorUtil.color(message)));
    }

    @EventHandler
    public void onDelayQuestDoneEvent(DelayQuestDoneEvent event) {

        final Player player = event.getPlayer();
        final QuestResetDelay delay = event.getDelay();

        final List<String> rewards = this.instance.getConfig().getStringList("all-quests-done." + delay.name().toLowerCase());
        new QuestRewardProcessor().process(player, rewards);
    }
}
