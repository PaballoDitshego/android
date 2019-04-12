package za.co.moxomo.contentproviders;

import android.content.SearchRecentSuggestionsProvider;

public class RecentSearchSuggestionProvider extends SearchRecentSuggestionsProvider {
    public final static String AUTHORITY = "za.co.moxomo.contentproviders.RecentSearchSuggestionProvider";
    public final static int MODE = DATABASE_MODE_QUERIES;

    public RecentSearchSuggestionProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }
}
