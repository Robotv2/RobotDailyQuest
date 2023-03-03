package fr.robotv2.robotdailyquests;

import fr.robotv2.robotdailyquests.enums.QuestResetDelay;
import fr.robotv2.robotdailyquests.util.DateUtil;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.AutoComplete;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Default;
import revxrsal.commands.annotation.Optional;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.BukkitCommandActor;
import revxrsal.commands.bukkit.BukkitCommandHandler;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command({"robotquest", "quest", "quests", "quete", "quetes"})
@CommandPermission("robotdailyquest.admin")
public class QuestCommand {

    private final RobotDailyQuest instance;

    public QuestCommand(BukkitCommandHandler handler, RobotDailyQuest instance) {
        this.instance = instance;
        handler.register(new PlayerQuestCommand());
        handler.register(new AdminQuestCommand());
    }

    @Command({"quest", "quests", "quete", "quetes"})
    private final class PlayerQuestCommand {
        @Default
        @CommandPermission("robotdailyquest.command.menu")
        public void onMenu(BukkitCommandActor actor) {
            instance.getGuiHandler().openMenu(actor.requirePlayer());
        }
    }

    @Command({"robotquest", "qadmin"})
    private final class AdminQuestCommand {

        @Subcommand("reload")
        @CommandPermission("robotdailyquest.command.reload")
        public void onReload(BukkitCommandActor actor) {
            instance.onReload();
            actor.reply(ChatColor.GREEN + "Vous venez de recharger le plugin.");
        }

        @Subcommand("next-reset")
        @CommandPermission("robotdailyquest.command.next-reset")
        public void onNextReset(BukkitCommandActor actor, QuestResetDelay delay) {
            actor.reply(ChatColor.GREEN + "Prochain reset pour le delai " + delay + " le: " + DateUtil.getDateFormatted(delay));
        }

        @Subcommand("force-reset")
        @CommandPermission("robotdailyquest.command.force-reset")
        @AutoComplete("@players")
        public void onReset(BukkitCommandActor actor, @Optional OfflinePlayer target) {

            for (QuestResetDelay value : QuestResetDelay.VALUES) {
                if(target != null) {
                    instance.getResetService().reset(target.getUniqueId(), value);
                } else {
                    instance.getResetService().getResetRunnable(value, false).run();
                }
            }

            final String message = target != null ? String.format("Vous venez de recharger les quêtes pour le joueur: %s.", target.getName()) : "Vous venez de recharger les quêtes.";
            actor.reply(ChatColor.GREEN + message);
        }

        @Subcommand("show")
        @CommandPermission("robotdailyquest.command.show")
        @AutoComplete("@players")
        public void onShow(BukkitCommandActor actor, Player target) {
            instance.getGuiHandler().createQuestGui(target).show(actor.requirePlayer());
        }
    }
}
