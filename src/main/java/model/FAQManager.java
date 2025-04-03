package model;

import model.FAQ.FAQItem;
import model.FAQ.FAQSection;
import view.TextUserInterface;
import view.View;

import java.util.LinkedList;
import java.util.List;

public class FAQManager {
    private final List<FAQSection> sections = new LinkedList<>();
    private final View view = new TextUserInterface();

    public void addSection(FAQSection section) {
        sections.add(section);
        section.setParent(null);
    }
    /**
     * Asks user to remove an FAQ item from a section.
     * @param section The section to remove the item from.
     */
    public void removeSection(FAQSection section) {
        view.displayFAQSection(section);
        int id = Integer.parseInt(view.getInput("Enter the ID of the section to remove: "));
        if(section.removeItem(id)){
            view.displaySuccess("Item removed successfully.");
        } else {
            view.displayError("Invalid id.");
        }
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
