package com.sunnykwong.HCLW;

import android.app.Activity;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.app.AlertDialog;
import android.appwidget.AppWidgetManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnKeyListener;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceManager;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.preference.PreferenceCategory;

import android.preference.Preference.OnPreferenceChangeListener;
import android.text.util.Linkify;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class HCLWPrefsActivity extends PreferenceActivity { 
    /** Called when the activity is first created. */
    static AlertDialog mAD;
    CheckBox mCheckBox;
    TextView mTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);

    	PreferenceManager.getDefaultSharedPreferences(this);
    	
    	addPreferencesFromResource(getResources().getIdentifier("hclwprefs", "xml", HCLW.PKGNAME));
    	findPreference("dpi").setTitle("Screen DPI " +HCLW.SCRNDPI);
    	findPreference("dpi").setSummary("Screen dimension " +HCLW.SCRNWIDTH + "x" + HCLW.SCRNHEIGHT);
    	
    	if (HCLW.FREEEDITION) {
    		findPreference("sVersion").setTitle("Version " + HCLW.THISVERSION + " Free");
    		findPreference("sVersion").setSummary("Tap me to get the full version!");
    		findPreference("sVersion").setSelectable(true);
    	} else {
    		findPreference("sVersion").setTitle("Version " + HCLW.THISVERSION);
    		findPreference("sVersion").setSummary("Thanks for your support!");
    		findPreference("sVersion").setSelectable(false);
    	}

		// This is the help/FAQ dialog.
    	if (HCLW.PREFS.getBoolean("Egg", false)) {
    		//Populate current flow direction.
			if (HCLW.REVERSE) {
				findPreference("reverseFlow").setSummary("Flowing Uphill.");
			} else {
				findPreference("reverseFlow").setSummary("Flowing Downhill.");
			}
    	} else {
    		//hide the reverse Flow option
    		getPreferenceScreen().removePreference(findPreference("reverseFlow"));
    	}

		
		if (HCLW.SHOWHELP) {
			LayoutInflater li = LayoutInflater.from(this);
			LinearLayout ll = (LinearLayout)(li.inflate(getResources().getIdentifier("faqdialog", "layout", HCLW.PKGNAME), null));
			mTextView = (TextView)ll.findViewById(getResources().getIdentifier("splashtext", "id", HCLW.PKGNAME));
			mTextView.setAutoLinkMask(Linkify.ALL);
			mTextView.setMinLines(8);
			mTextView.setText(HCLW.FAQS[HCLW.faqtoshow++]);
			HCLW.faqtoshow = HCLW.faqtoshow==HCLW.FAQS.length?0:HCLW.faqtoshow;
			
			mCheckBox = (CheckBox)ll.findViewById(getResources().getIdentifier("splashcheck", "id", HCLW.PKGNAME));
			mCheckBox.setChecked(!HCLW.SHOWHELP);
			mCheckBox.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					// TODO Auto-generated method stub
					HCLW.SHOWHELP = !isChecked;
				}
			});

			((Button)ll.findViewById(getResources().getIdentifier("faqOK", "id", HCLW.PKGNAME))).setOnClickListener(new Button.OnClickListener() {
				
				@Override
				public void onClick(android.view.View v) {
					HCLW.PREFS.edit().putBoolean("showhelp", HCLW.SHOWHELP).commit();
					mAD.dismiss();
				}
			});
			((Button)ll.findViewById(getResources().getIdentifier("faqNeutral", "id", HCLW.PKGNAME))).setOnClickListener(new Button.OnClickListener() {
				
				@Override
				public void onClick(android.view.View v) {
					mTextView.setText(HCLW.FAQS[HCLW.faqtoshow++]);
					mTextView.invalidate();
					HCLW.faqtoshow = HCLW.faqtoshow==HCLW.FAQS.length?0:HCLW.faqtoshow;
				}
			});;
			
			mAD = new AlertDialog.Builder(this)
			.setTitle("Useful Tip")
		    .setCancelable(true)
		    .setView(ll)
		    .setOnKeyListener(new OnKeyListener() {
		    	public boolean onKey(DialogInterface arg0, int arg1, android.view.KeyEvent arg2) {
		    		if (arg2.getKeyCode()==android.view.KeyEvent.KEYCODE_BACK) mAD.cancel();
		    		return true;
		    	};
		    })
		    .show();
		}

    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
    		Preference preference) {
		final CharSequence[] items = {"Email", "Donate"};
    	if (preference == this.findPreference("reverseFlow")) {
			LayoutInflater li = LayoutInflater.from(this);
			LinearLayout ll = (LinearLayout)(li.inflate(getResources().getIdentifier("reverse", "layout", HCLW.PKGNAME), null));
			TextView tv = (TextView)ll.findViewById(getResources().getIdentifier("reversetext", "id", HCLW.PKGNAME));
			tv.setAutoLinkMask(Linkify.ALL);
			tv.setMinLines(10);
			tv.setText(HCLW.EGG);
			if (HCLW.REVERSE) {
				preference.setSummary("Flowing Downhill.");
				HCLW.REVERSE=false;
			} else {
				preference.setSummary("Flowing Uphill.");
				HCLW.REVERSE=true;
				mAD = new AlertDialog.Builder(this)
				.setTitle("Congratulations!")
			    .setCancelable(true)
			    .setView(ll)
			    .setOnKeyListener(new OnKeyListener() {
			    	public boolean onKey(DialogInterface arg0, int arg1, android.view.KeyEvent arg2) {
			    		if (arg2.getKeyCode()==android.view.KeyEvent.KEYCODE_BACK) {
			    			mAD.cancel();
			    		}
			    		return true;
			    	};
			    })
			    .show();
			}
			
    	}
    	if (preference == this.findPreference("flarecolors")) {
			LayoutInflater li = LayoutInflater.from(this);
			ScrollView sv = (ScrollView)(li.inflate(getResources().getIdentifier("flares", "layout", HCLW.PKGNAME), null));
			CheckBox cb = (CheckBox)sv.findViewById(this.getResources().getIdentifier("showcolor0", "id", HCLW.PKGNAME));
			cb.setChecked(HCLW.PREFS.getBoolean("showcolor0", true));
			cb.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					// TODO Auto-generated method stub
					HCLW.PREFS.edit().putBoolean("showcolor0", isChecked).commit();
				}
			});
			cb = (CheckBox)sv.findViewById(this.getResources().getIdentifier("showcolor1", "id", HCLW.PKGNAME));
			cb.setChecked(HCLW.PREFS.getBoolean("showcolor1", true));
			cb.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					// TODO Auto-generated method stub
					HCLW.PREFS.edit().putBoolean("showcolor1", isChecked).commit();
				}
			});
			cb = (CheckBox)sv.findViewById(this.getResources().getIdentifier("showcolor2", "id", HCLW.PKGNAME));
			cb.setChecked(HCLW.PREFS.getBoolean("showcolor2", true));
			cb.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					// TODO Auto-generated method stub
					HCLW.PREFS.edit().putBoolean("showcolor2", isChecked).commit();
				}
			});
			cb = (CheckBox)sv.findViewById(this.getResources().getIdentifier("showcolor3", "id", HCLW.PKGNAME));
			cb.setChecked(HCLW.PREFS.getBoolean("showcolor3", true));
			cb.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					// TODO Auto-generated method stub
					HCLW.PREFS.edit().putBoolean("showcolor3", isChecked).commit();
				}
			});
			cb = (CheckBox)sv.findViewById(this.getResources().getIdentifier("showcolor4", "id", HCLW.PKGNAME));
			cb.setChecked(HCLW.PREFS.getBoolean("showcolor4", true));
			cb.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					// TODO Auto-generated method stub
					HCLW.PREFS.edit().putBoolean("showcolor4", isChecked).commit();
				}
			});

			mAD = new AlertDialog.Builder(this)
			.setTitle("Toggle Colors")
		    .setCancelable(true)
		    .setView(sv)
		    .setOnKeyListener(new OnKeyListener() {
		    	public boolean onKey(DialogInterface arg0, int arg1, android.view.KeyEvent arg2) {
		    		if (arg2.getKeyCode()==android.view.KeyEvent.KEYCODE_BACK) mAD.cancel();
		    		return true;
		    	};
		    })
		    .show();

    	}
    	if (preference == findPreference("contactProg")) {
			new AlertDialog.Builder(this)
				.setTitle("Email or Donate to Xaffron")
				.setItems(items, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int item) {
							switch (item) {
								case 0: //Email
									Intent it = new Intent(android.content.Intent.ACTION_SEND)
		    		   					.setType("plain/text")
		    		   					.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"skwong@consultant.com"})
		    		   					.putExtra(android.content.Intent.EXTRA_SUBJECT, "HCLW Feedback v" + HCLW.THISVERSION);
					    		   	startActivity(Intent.createChooser(it, "Contact Xaffron for issues, help & support."));  
					    		   	finish();
					    		   	break;
								case 1: //Donate
						    		it = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=S9VEL3WFGXK48"));
						    		startActivity(it);
						    		finish();
									break;
								default:
									//do nothing
							}
						}
				})
				.show();
    	}
    	if (preference == findPreference("contactArt")) {
			new AlertDialog.Builder(this)
				.setTitle("Email or Donate to Nemuro")
				.setItems(items, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int item) {
							switch (item) {
								case 0: //Email
									Intent it = new Intent(android.content.Intent.ACTION_SEND)
		    		   					.setType("plain/text")
		    		   					.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"brcosmin@gmail.com"})
		    		   					.putExtra(android.content.Intent.EXTRA_SUBJECT, "HCLW Feedback v" + HCLW.THISVERSION);
					    		   	startActivity(Intent.createChooser(it, "Contact Xaffron for issues, help & support."));  
					    		   	finish();
					    		   	break;
								case 1: //Donate
						    		it = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=brcosmin%40gmail%2ecom&lc=RO&item_name=Cosmin%20Bizon&item_number=cosminbizon&currency_code=EUR&bn=PP%2dDonationsBF%3abtn_donateCC_LG%2egif%3aNonHosted"));
						    		startActivity(it);
						    		finish();
									break;
								default:
									//do nothing
							}
						}
				})
				.show();
    	}
    	if (preference == getPreferenceScreen().findPreference("sVersion")) {
			this.startActivity(HCLW.HCLWMARKETINTENT);
        	this.finish();
    	}

    	return super.onPreferenceTreeClick(preferenceScreen, preference);
    }
    @Override
    protected void onPause() {
		HCLW.HDRENDERING = HCLW.PREFS.getBoolean("HDRendering", true);
		if (HCLW.HDRENDERING) HCLW.SCREENSCALEFACTOR=1;
		else HCLW.SCREENSCALEFACTOR=2;
		((HCLW)getApplication()).prepareBitmaps();
		
		HCLW.DEBUG=HCLW.PREFS.getBoolean("Debug", false);
		HCLW.DEFAULTEFFECTCOLOR = Color.parseColor(HCLW.PREFS.getString("TrailLength", "#051b1939"));
		HCLW.RENDERWHILESWIPING = HCLW.PREFS.getBoolean("RenderWhileSwiping", true);
		HCLW.FPS = Integer.parseInt(HCLW.PREFS.getString("FrameRates", "25"));
		HCLW.TARGETTIME=1000l/HCLW.FPS;
		
		((HCLW)getApplication()).countFlareColors();
    	HCLW.FPS = Integer.parseInt(HCLW.PREFS.getString("FrameRates", "25"));
		HCLW.TARGETTIME=1000l/HCLW.FPS;
		
		//Translate Changes
    	String sLAF = HCLW.PREFS.getString("HCLWLAF", "Racing Flares");
    	if (sLAF.equals("Racing Flares")) {
    		HCLW.PREFS.edit().putBoolean("FlaresAboveSurface", false)
    		.putBoolean("LightningEffect", false)
    		.putBoolean("SparkEffect", false)
    		.putBoolean("Searchlight", false)
    		.commit();
    		HCLW.TRIALOVERTIME=0l;
    	} else if (sLAF.equals("Lightning Strikes")) {
    		// Lightning Strikes
    		HCLW.PREFS.edit().putBoolean("FlaresAboveSurface", false)
    		.putBoolean("LightningEffect", true)
    		.putBoolean("SparkEffect", false)
    		.putBoolean("Searchlight", false)
    		.commit();
    		if (HCLW.FREEEDITION) {
    			HCLW.TRIALOVERTIME = System.currentTimeMillis()+ 60000l;
    			Toast.makeText(this, "This look is limited to 1 minute in the free version.  Consider donating if you like this wallpaper!", Toast.LENGTH_LONG).show();
    		}
    	} else if (sLAF.equals("Searchlight")) {
    		// Searchlight
    		HCLW.PREFS.edit().putBoolean("FlaresAboveSurface", false)
    		.putBoolean("LightningEffect", false)
    		.putBoolean("Searchlight", true)
    		.commit();
    		if (HCLW.FREEEDITION) {
    			HCLW.TRIALOVERTIME = System.currentTimeMillis()+ 60000l;
    			Toast.makeText(this, "This look is limited to 1 minute in the free version.  Consider donating if you like this wallpaper!", Toast.LENGTH_LONG).show();
    		}
    	} else {
    		// Electric Sparks
    		HCLW.PREFS.edit().putBoolean("FlaresAboveSurface", true)
    		.putBoolean("LightningEffect", false)
    		.putBoolean("SparkEffect", true)
    		.putBoolean("Searchlight", false)
    		.commit();

    		((HCLW)getApplication()).countFlareColors();
    		
    		if (HCLW.FREEEDITION) {
    			HCLW.TRIALOVERTIME = System.currentTimeMillis()+ 60000l;
    			Toast.makeText(this, "This look is limited to 1 minute in the free version.  Consider donating if you like this wallpaper!", Toast.LENGTH_LONG).show();
    		}
    		
    	}
    	if (HCLW.JSON && HCLW.REVERSE) {
    		((HCLW)getApplication()).loadEggFromJSON();
    	} else if (HCLW.JSON && !HCLW.REVERSE) {
    		((HCLW)getApplication()).loadFlaresFromJSON();
    	}
    	
        boolean bAllColorsDisabled=true;
        for (int i =0; i<5; i++) {
        	if (HCLW.PREFS.getBoolean("showcolor"+i, false)) {
        		bAllColorsDisabled = false;
        		break;
        	}
        }
        if (bAllColorsDisabled) {
        	HCLW.PREFS.edit()
    		.putBoolean("showcolor0", true)
    		.putBoolean("showcolor1", true)
    		.putBoolean("showcolor2", true)
    		.putBoolean("showcolor3", true)
    		.putBoolean("showcolor4", true)
    		.commit();
    		((HCLW)getApplication()).countFlareColors();
        }

		HCLW.FLARESABOVESURFACE=HCLW.PREFS.getBoolean("FlaresAboveSurface", false);
		HCLW.LIGHTNINGEFFECT=HCLW.PREFS.getBoolean("LightningEffect", false);
		HCLW.SPARKEFFECT=HCLW.PREFS.getBoolean("SparkEffect", false);
		HCLW.SEARCHLIGHTEFFECT=HCLW.PREFS.getBoolean("Searchlight", false);
		HCLW.LIGHTNFREQUENCY=Double.parseDouble(HCLW.PREFS.getString("LightnFrequency","0.05"));

    	super.onPause();
    }
    
    @Override
    public void onDestroy() {
      super.onDestroy();
    }
} 