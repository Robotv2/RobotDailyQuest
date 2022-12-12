package fr.robotv2.robotdailyquests;

import fr.robotv2.robotdailyquests.data.impl.LoadedQuest;
import fr.robotv2.robotdailyquests.enums.QuestResetDelay;
import fr.robotv2.robotdailyquests.quest.Quest;
import fr.robotv2.robotdailyquests.util.ColorUtil;
import fr.robotv2.robotdailyquests.util.DateUtil;
import org.bukkit.ChatColor;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Default;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.BukkitCommandActor;

import java.util.List;
import java.util.UUID;

@Command("robotquest")
public class QuestCommand {

    private final RobotDailyQuest instance;
    public QuestCommand(RobotDailyQuest instance) {
        this.instance = instance;
    }

    @Subcommand("menu")
    public void onDefault(BukkitCommandActor actor, QuestResetDelay delay) {
        instance.getGuiHandler().openBasicQuestGui(actor.requirePlayer(), delay);
    }

    @Subcommand("progression")
    public void onProgression(BukkitCommandActor actor, QuestResetDelay delay) {

        final UUID playerUUID = actor.requirePlayer().getUniqueId();
        final List<LoadedQuest> quests = this.instance.getQuestManager().getLoadedQuests(delay);

        for(LoadedQuest loadedQuest : quests) {

            final Quest quest = loadedQuest.getQuest();
            if(quest == null) return;

            actor.reply(" ");
            actor.reply("Name: " + ColorUtil.color(quest.getName()));
            actor.reply("Current: " + loadedQuest.getCurrentProgress(playerUUID));
            actor.reply("Required: " + quest.getRequirement().getAmount());
            actor.reply("Done: " + loadedQuest.hasBeenDone(playerUUID));
            actor.reply(" ");
        }
    }

    @Subcommand("reload")
    public void onReload(BukkitCommandActor actor) {
        instance.onReload();
        actor.reply(ChatColor.GREEN + "Vous venez de recharger le plugin.");
    }

    @Subcommand("next-reset")
    public void onNextReset(BukkitCommandActor actor, QuestResetDelay delay) {
        actor.reply(ChatColor.GREEN + "Prochain reset pour le delai " + delay + " le: " + DateUtil.getDateFormatted(delay.nextResetToEpochMilli()));
    }

    @Subcommand("force-reset")
    public void onReset(BukkitCommandActor actor) {

        for (QuestResetDelay value : QuestResetDelay.VALUES) {
            instance.getResetService().getResetRunnable(value, false).run();
        }

        actor.reply(ChatColor.GREEN + "Vous venez de recharger les quêtes.");
    }
}
