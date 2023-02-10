package fr.robotv2.robotdailyquests;

import fr.robotv2.robotdailyquests.data.impl.QuestPlayer;
import fr.robotv2.robotdailyquests.enums.QuestDifficulty;
import fr.robotv2.robotdailyquests.enums.QuestResetDelay;
import fr.robotv2.robotdailyquests.importer.QuestImporterManager;
import fr.robotv2.robotdailyquests.quest.QuestRewardProcessor;
import fr.robotv2.robotdailyquests.util.DateUtil;
import org.bukkit.ChatColor;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command({"robotquest", "quest", "quests","quete", "quetes"})
@CommandPermission("robotdailyquest.admin")
public class QuestCommand {

    private final RobotDailyQuest instance;
    public QuestCommand(RobotDailyQuest instance) {
        this.instance = instance;
    }

    @Subcommand("menu")
    @CommandPermission("robotdailyquest.command.menu")
    public void onDefault(BukkitCommandActor actor) {
        instance.getGuiHandler().openMenu(actor.requirePlayer());
    }

    @Subcommand("reload")
    @CommandPermission("robotdailyquest.command.reload")
    public void onReload(BukkitCommandActor actor) {
        instance.onReload();
        actor.reply(ChatColor.GREEN + "Vous venez de recharger le plugin.");
    }

    @Subcommand("next-reset")
    @CommandPermission("robotdailyquest.command.next-reset")
    public void onNextReset(BukkitCommandActor actor, QuestResetDelay delay) {
        actor.reply(ChatColor.GREEN + "Prochain reset pour le delai " + delay + " le: " + DateUtil.getDateFormatted(delay.nextResetToEpochMilli()));
    }

    @Subcommand("force-reset")
    @CommandPermission("robotdailyquest.command.force-reset")
    public void onReset(BukkitCommandActor actor) {

        for (QuestResetDelay value : QuestResetDelay.VALUES) {
            instance.getResetService().getResetRunnable(value, false).run();
        }

        actor.reply(ChatColor.GREEN + "Vous venez de recharger les quêtes.");
    }

    @Subcommand("import")
    @CommandPermission("robotdailyquest.command.import")
    public void onImport(BukkitCommandActor actor,
                         QuestImporterManager.ImportPlugin importer, String resourcePath) {

        final boolean result = this.instance.getQuestImporterManager().startImport(importer, resourcePath);

        if(result) {
            actor.reply(ChatColor.GREEN + "Toutes les quêtes ont été importées avec succès.");
        } else {
            actor.reply(ChatColor.RED + "Une erreur a eu lieu pendant l'importation.");
        }
    }

    @Subcommand("debug")
    @CommandPermission("robotdailyquest.command.debug")
    public void onDebug(BukkitCommandActor actor) {
        new QuestRewardProcessor().process(actor.requirePlayer(), QuestPlayer.getQuestPlayer(actor.requirePlayer()).getActiveQuests().get(0).getQuest());
    }
}
