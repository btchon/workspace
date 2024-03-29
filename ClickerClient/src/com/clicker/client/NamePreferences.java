package com.clicker.client;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

// TODO: Auto-generated Javadoc
/**
 * The Class NamePreferences.
 */
public class NamePreferences extends PreferenceActivity 
{
    
    /** The Constant FIRST_KEY_MY_PREFERENCE. */
    public static final String FIRST_KEY_MY_PREFERENCE = "first_name_edittext_preference";
    
    /** The Constant LAST_KEY_MY_PREFERNCE. */
    public static final String LAST_KEY_MY_PREFERNCE = "last_name_edittext_preference";
    
    /** The Constant FIRST_CONNECTION. */
    public static final String FIRST_CONNECTION = "first_connection_preference";
    
    /** The Constant SECOND_CONNECTION. */
    public static final String SECOND_CONNECTION = "second_connection_preference";
    
    /** The Constant THIRD_CONNECTION. */
    public static final String THIRD_CONNECTION = "third_connection_preference";
    
    /** The Constant FOURTH_CONNECTION. */
    public static final String FOURTH_CONNECTION = "fourth_connection_preference";
    
    /** The Constant FIFTH_CONNECTION. */
    public static final String FIFTH_CONNECTION = "fifth_connection_preference";
    
    /** The Constant LAST_CONNECTION. */
    public static final String LAST_CONNECTION = "last_connection_preference";
    
    /* (non-Javadoc)
     * @see android.preference.PreferenceActivity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Load the preferences from an XML resource	
        addPreferencesFromResource(R.xml.name_input_preference);
        
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        
        CharSequence[] entries = { "", "", "", "", "" };
        
        entries[0] = sharedPref.getString(NamePreferences.FIRST_CONNECTION, "");
        entries[1] = sharedPref.getString(NamePreferences.SECOND_CONNECTION, "");
        entries[2] = sharedPref.getString(NamePreferences.THIRD_CONNECTION, "");
        entries[3] = sharedPref.getString(NamePreferences.FOURTH_CONNECTION, "");
        entries[4] = sharedPref.getString(NamePreferences.FIFTH_CONNECTION, "");
        
        ListPreference lp = (ListPreference)getPreferenceScreen().findPreference("listPref");
        lp.setEntries(entries);
        lp.setEntryValues(entries);
        
    }
    
}
