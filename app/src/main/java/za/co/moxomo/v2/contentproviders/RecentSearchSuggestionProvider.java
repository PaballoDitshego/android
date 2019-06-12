package za.co.moxomo.v2.contentproviders;

import android.content.SearchRecentSuggestionsProvider;

public class RecentSearchSuggestionProvider extends SearchRecentSuggestionsProvider {
    public final static String AUTHORITY = "za.co.moxomo.v2.contentproviders.RecentSearchSuggestionProvider";
    public final static int MODE = DATABASE_MODE_QUERIES;

    public RecentSearchSuggestionProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }
}
