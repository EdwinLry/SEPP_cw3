package model.FAQ;

import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

public class FAQSection {
    private final String topic;
    private final List<FAQItem> items = new LinkedList<>();
    private FAQSection parent;
    private final List<FAQSection> subsections = new LinkedList<>();
    private int nextid = 0;
    private final Queue<Integer> freeIds = new PriorityQueue<>();

    public FAQSection(String topic) {
        this.topic = topic;
    }

    public void addSubsection(FAQSection section) {
        subsections.add(section);
        section.parent = this;
    }

    public void addItem(String question, String answer) {
        if (freeIds.isEmpty()) {
            items.add(new FAQItem(nextid++, question, answer));
        } else {
            items.add(new FAQItem(freeIds.poll(), question, answer));
        }
    }

    public void addItem(String question, String answer, String courseTag) {
        if (freeIds.isEmpty()) {
            items.add(new FAQItem(nextid++, question, answer, courseTag));
        } else {
            items.add(new FAQItem(freeIds.poll(), question, answer, courseTag));
        }
    }

    public boolean removeItem2(int itemId) {
        if (itemId < 0 || itemId >= items.size()) {
            return false;
        }
        for(FAQItem item : items) {
            if (item.getId() == itemId) {
                freeIds.add(itemId);
                items.remove(item);
                return true;
            }
        }
        return false;
    }

    public boolean removeItem(int itemId) {
        FAQItem toRemove = null;

        for (FAQItem item : items) {
            if (item.getId() == itemId) {
                toRemove = item;
                break;
            }
        }

        if (toRemove != null) {
            items.remove(toRemove);
            // Reassign IDs to maintain consistency
            for (int i = 0; i < items.size(); i++) {
                items.get(i).setId(i);
            }
            return true;
        }

        return false;
    }

    public FAQItem getItemsByTag(String courseCode){
        for(FAQItem item : items){
            if(item.getCourseTag().equals(courseCode)){
                return item;
            }
        }
        return null;
    }

    public boolean hasTopic(String topic){
        return this.topic.equals(topic);
    }

    public List<FAQSection> getSubsections() {
        return subsections;
    }

    public String getTopic() {
        return topic;
    }

    public List<FAQItem> getItems() {
        return items;
    }

    public FAQSection getParent() {
        return parent;
    }

    public void setParent(FAQSection parent) {
        this.parent = parent;
    }
}
