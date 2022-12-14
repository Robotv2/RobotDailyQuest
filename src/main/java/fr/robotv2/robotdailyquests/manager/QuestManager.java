package fr.robotv2.robotdailyquests.manager;

import fr.robotv2.robotdailyquests.data.impl.ActiveQuest;
import fr.robotv2.robotdailyquests.enums.QuestResetDelay;
import fr.robotv2.robotdailyquests.quest.Quest;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class QuestManager {

    private static final ThreadLocalRandom random = ThreadLocalRandom.current();

    private final Map<String, Quest> quests = new HashMap<>();
    private final List<ActiveQuest> activeQuests = new ArrayList<>();

    public void cacheQuest(Quest quest) {
        this.quests.put(quest.getId().toLowerCase(), quest);
    }

    public void cacheLoadedQuest(ActiveQuest quest) {
        this.activeQuests.add(quest);
    }

    @UnmodifiableView
    public Collection<Quest> getQuests() {
        return Collections.unmodifiableCollection(this.quests.values());
    }

    @UnmodifiableView
    public List<Quest> getQuests(QuestResetDelay delay) {
        return getQuests().stream()
                .filter(quest -> quest.getDelay() == delay)
                .toList();
    }

    public Quest getRandomQuest(QuestResetDelay delay) {
        final List<Quest> quests = this.getQuests(delay);
        if(quests.isEmpty()) return null;
        return quests.get(random.nextInt(quests.size()));
    }

    @UnmodifiableView
    public List<ActiveQuest> getActiveQuests() {
        return Collections.unmodifiableList(this.activeQuests);
    }

    @UnmodifiableView
    public List<ActiveQuest> getActiveQuests(QuestResetDelay delay) {
        return getActiveQuests().stream()
                .filter(quest -> quest.getResetDelay() == delay)
                .toList();
    }

    public void removeActiveQuest(QuestResetDelay delay) {
        getActiveQuests(delay).forEach(this::removeActiveQuest);
    }

    public void removeActiveQuest(ActiveQuest quest) {
        this.activeQuests.remove(quest);
    }

    public void fillLoadedQuest(QuestResetDelay delay, int amount) {

        final List<Quest> quests = new ArrayList<>();
        final int max = this.getQuests(delay).size();

        while(quests.size() < amount) {

            if(quests.size() >= max) {
                break;
            }

            final Quest random = this.getRandomQuest(delay);
            if(random == null) {
                break; //There is no quest available for this delay.
            }

            if(!quests.contains(random)) {
                quests.add(random);
            }
        }

        final List<ActiveQuest> activeQuests =
                quests.stream()
                .map(quest -> new ActiveQuest(quest, System.currentTimeMillis()))
                .toList();

        this.activeQuests.addAll(activeQuests);
    }

    @Nullable
    public Quest fromId(String id) {
        return this.quests.get(id.toLowerCase());
    }
}
