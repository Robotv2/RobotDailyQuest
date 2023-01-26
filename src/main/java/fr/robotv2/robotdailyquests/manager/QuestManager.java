package fr.robotv2.robotdailyquests.manager;

import fr.robotv2.robotdailyquests.data.impl.ActiveQuest;
import fr.robotv2.robotdailyquests.data.impl.QuestPlayer;
import fr.robotv2.robotdailyquests.enums.QuestDifficulty;
import fr.robotv2.robotdailyquests.enums.QuestResetDelay;
import fr.robotv2.robotdailyquests.quest.Quest;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class QuestManager {

    private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();

    private final Map<String, Quest> quests = new HashMap<>();

    public void clearQuests() {
        this.quests.clear();
    }

    public void cacheQuest(Quest quest) {
        this.quests.put(quest.getId().toLowerCase(), quest);
    }

    @UnmodifiableView
    public Collection<Quest> getQuests() {
        return Collections.unmodifiableCollection(this.quests.values());
    }

    @UnmodifiableView
    public List<Quest> getQuests(QuestResetDelay delay, QuestDifficulty difficulty) {
        return getQuests().stream()
                .filter(quest -> quest.getDelay() == delay && quest.getDifficulty() == difficulty)
                .toList();
    }

    public Quest getRandomQuest(QuestResetDelay delay, QuestDifficulty difficulty) {
        final List<Quest> quests = this.getQuests(delay, difficulty);

        if(quests.isEmpty()) {
            return null;
        }

        return quests.get(RANDOM.nextInt(quests.size()));
    }

    public void fillQuest(QuestPlayer player, QuestResetDelay delay, QuestDifficulty difficulty, int amount) {

        final List<Quest> quests = new ArrayList<>();
        final int max = this.getQuests(delay, difficulty).size();

        while(quests.size() < amount) {

            if(quests.size() >= max) {
                break;
            }

            final Quest random = this.getRandomQuest(delay, difficulty);

            if(random == null) {
                break; //There is no quest available for this delay.
            }

            if(!quests.contains(random)) {
                quests.add(random);
            }
        }

        final long now = System.currentTimeMillis();

        for(Quest quest : quests) {
            final ActiveQuest activeQuest = new ActiveQuest(player.getUniqueId(), quest, now);
            player.addActiveQuest(activeQuest);
        }
    }

    @Nullable
    public Quest fromId(String id) {
        return this.quests.get(id.toLowerCase());
    }
}
