package model;

import java.util.*;

public class SharedContext {
    public static final String ADMIN_STAFF_EMAIL = "inquiries@hindeburg.ac.nz";
    public User currentUser;

    public final List<Inquiry> inquiries;
    public final FAQManager faqManager;
    private final Map<String, Set<String>> faqTopicsUpdateSubscribers;

    public SharedContext() {
        this.currentUser = new Guest();
        this.inquiries = new ArrayList<>();
        faqManager = new FAQManager();
        faqTopicsUpdateSubscribers = new HashMap<>();
    }

    public FAQManager getFAQ() {
        return faqManager;
    }

    public boolean registerForFAQUpdates(String email, String topic) {
        if (faqTopicsUpdateSubscribers.containsKey(topic)) {
            return faqTopicsUpdateSubscribers.get(topic).add(email);
        } else {
            Set<String> subscribers = new HashSet<>();
            subscribers.add(email);
            faqTopicsUpdateSubscribers.put(topic, subscribers);
            return true;
        }
    }

    public boolean unregisterForFAQUpdates(String email, String topic) {
        return faqTopicsUpdateSubscribers.getOrDefault(topic, new HashSet<>()).remove(email);
    }

    public Set<String> usersSubscribedToFAQTopic(String topic) {
        return faqTopicsUpdateSubscribers.getOrDefault(topic, new HashSet<>());
    }
}
