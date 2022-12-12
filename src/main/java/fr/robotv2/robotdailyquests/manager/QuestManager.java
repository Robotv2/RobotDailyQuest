package fr.robotv2.robotdailyquests.manager;

import fr.robotv2.robotdailyquests.data.impl.LoadedQuest;
import fr.robotv2.robotdailyquests.enums.QuestResetDelay;
import fr.robotv2.robotdailyquests.quest.Quest;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class QuestManager {

    private static final ThreadLocalRandom random = ThreadLocalRandom.current();

    private final Map<String, Quest> quests = new HashMap<>();
    private final List<LoadedQuest> loadedQuests = new ArrayList<>();

    public void cacheQuest(Quest quest) {
        this.quests.put(quest.getId(), quest);
    }

    public void cacheLoadedQuest(LoadedQuest quest) {
        this.loadedQuests.add(quest);
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
    public List<LoadedQuest> getLoadedQuests() {
        return Collections.unmodifiableList(this.loadedQuests);
    }

    @UnmodifiableView
    public List<LoadedQuest> getLoadedQuests(QuestResetDelay delay) {
        return getLoadedQuests().stream()
                .filter(quest -> quest.getResetDelay() == delay)
                .toList();
    }

    public void removeLoadedQuest(QuestResetDelay delay) {
        getLoadedQuests(delay).forEach(this::removeLoadedQuest);
    }

    public void removeLoadedQuest(LoadedQuest quest) {
        this.loadedQuests.remove(quest);
    }

    public void fillLoadedQuest(QuestResetDelay delay, int amount) {

        final List<Quest> quests = new ArrayList<>();
        final int maxAttempt = 10;

        for(int i = 0; i < maxAttempt; i++) {

            if(quests.size() >= amount) {
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

        final List<LoadedQuest> loadedQuests =
                quests.stream()
                .map(quest -> new LoadedQuest(quest, System.currentTimeMillis()))
                .toList();

        this.loadedQuests.addAll(loadedQuests);
    }

    @Nullable
    public Quest fromId(String id) {
        return this.quests.get(id.toLowerCase());
    }
}
