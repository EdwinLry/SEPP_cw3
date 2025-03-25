package model;

import java.util.LinkedList;
import java.util.List;

public class FAQ {
    private final List<FAQSection> sections = new LinkedList<>();

    public void addSection(FAQSection section) {
        sections.add(section);
        section.setParent(null);
    }

    public void removeSection(FAQSection section) {
        sections.remove(section);
        promoteSection(section);
    }

    private void promoteSection(FAQSection section) {
        if (section.getParent() == null) {
            return;
        }
        FAQSection parent = section.getParent();
        sections.add(parent);
        parent.setParent(null);
    }

    public List<FAQSection> getSections() {
        return sections;
    }
}
