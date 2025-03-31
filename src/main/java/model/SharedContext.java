package model;

import java.util.*;

public class SharedContext {
    public static final String ADMIN_STAFF_EMAIL = "inquiries@hindeburg.ac.nz";
    public User currentUser;

    public final List<Inquiry> inquiries;
    public final FAQManager faqManager;

    public SharedContext() {
        this.currentUser = new Guest();
        this.inquiries = new ArrayList<>();
        faqManager = new FAQManager();
    }

    public FAQManager getFAQ() {
        return faqManager;
    }
}
