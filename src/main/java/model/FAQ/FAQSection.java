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

    public List<FAQSection> getSubsections() {
        return subsections;
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

    public boolean removeItem(int index) {
        if (index < 0 || index >= items.size()) {
            return false;
        }
        for(FAQItem item : items) {
            if (item.getId() == index) {
                freeIds.add(index);
                items.remove(item);
                return true;
            }
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

    public boolean hadTopic(String topic){
        return this.topic.equals(topic);
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
