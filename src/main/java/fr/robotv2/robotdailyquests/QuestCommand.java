package fr.robotv2.robotdailyquests;

import fr.robotv2.robotdailyquests.enums.QuestResetDelay;
import fr.robotv2.robotdailyquests.importer.QuestImporterManager;
import fr.robotv2.robotdailyquests.util.DateUtil;
import org.bukkit.ChatColor;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.BukkitCommandActor;

@Command("robotquest")
public class QuestCommand {

    private final RobotDailyQuest instance;
    public QuestCommand(RobotDailyQuest instance) {
        this.instance = instance;
    }

    @Subcommand("menu")
    public void onDefault(BukkitCommandActor actor) {
        instance.getGuiHandler().openMenu(actor.requirePlayer());
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
    public void onImport(BukkitCommandActor actor,
                         QuestImporterManager.ImportPlugin importer, String resourcePath) {

        final boolean result = this.instance.getQuestImporterManager().startImport(importer, resourcePath);

        if(result) {
            actor.reply(ChatColor.GREEN + "Toutes quêtes ont été importées avec succès.");
        } else {
            actor.reply(ChatColor.RED + "Une erreur a eu lieu pendant l'importation.");
        }
    }
}
