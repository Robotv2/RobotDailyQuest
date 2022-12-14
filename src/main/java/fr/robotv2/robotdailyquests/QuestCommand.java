package fr.robotv2.robotdailyquests;

import fr.robotv2.robotdailyquests.data.impl.ActiveQuest;
import fr.robotv2.robotdailyquests.enums.QuestResetDelay;
import fr.robotv2.robotdailyquests.importer.QuestImporterManager;
import fr.robotv2.robotdailyquests.quest.Quest;
import fr.robotv2.robotdailyquests.util.ColorUtil;
import fr.robotv2.robotdailyquests.util.DateUtil;
import org.bukkit.ChatColor;
import revxrsal.commands.annotation.Command;
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
        final List<ActiveQuest> quests = this.instance.getQuestManager().getActiveQuests(delay);

        for(ActiveQuest activeQuest : quests) {

            final Quest quest = activeQuest.getQuest();
            if(quest == null) return;

            actor.reply(" ");
            actor.reply("Name: " + ColorUtil.color(quest.getName()));
            actor.reply("Current: " + activeQuest.getCurrentProgress(playerUUID));
            actor.reply("Required: " + quest.getRequirement().getAmount());
            actor.reply("Done: " + activeQuest.hasBeenDone(playerUUID));
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

    @Subcommand("import")
    public void onImport(BukkitCommandActor actor, QuestImporterManager.ImportPlugin importer) {

        final boolean result = this.instance.getQuestImporterManager().startImport(importer);

        if(result) {
            actor.reply(ChatColor.GREEN + "Toutes quêtes ont été importées avec succès.");
        } else {
            actor.reply(ChatColor.RED + "Une erreur a eu lieu pendant l'importation.");
        }
    }

    @Subcommand("debug")
    public void onDebug(BukkitCommandActor actor) {
        for (QuestResetDelay value : QuestResetDelay.VALUES) {
            actor.reply(" ");
            actor.reply(value + ": " + this.instance.getQuestManager().getQuests(value).size());
            actor.reply(value + ": " + this.instance.getQuestManager().getActiveQuests(value).size());
            actor.reply(" ");
        }
    }
}
