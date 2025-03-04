package com.mancj.materialsearchbar;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.TextAppearanceSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mancj.materialsearchbar.adapter.DefaultSuggestionsAdapter;
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter;
import com.mancj.materialsearchbar.util.PrefixStyle;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by mancj on 19.07.2016.
 */
public class MaterialSearchBar extends FrameLayout implements View.OnClickListener,
        Animation.AnimationListener, SuggestionsAdapter.OnItemViewClickListener,
        View.OnFocusChangeListener, TextView.OnEditorActionListener {
    public static final int BUTTON_SPEECH = 1;
    public static final int BUTTON_NAVIGATION = 2;
    public static final int BUTTON_BACK = 3;
    public static final int VIEW_VISIBLE = 1;
    public static final int VIEW_INVISIBLE = 0;
    private CardView searchBarCardView;
    private LinearLayout inputContainer;
    private ImageView navIcon;
    private ImageView menuIcon;
    private ImageView searchIcon;
    private ImageView arrowIcon;
    private ImageView clearIcon;
    private EditText searchEdit;
    private TextView placeHolder;
    private View suggestionDivider;
    private OnSearchActionListener onSearchActionListener;
    private boolean searchOpened;
    private boolean suggestionsVisible;
    private boolean isSuggestionsEnabled = true;
    private SuggestionsAdapter adapter;
    private float destiny;

    private PopupMenu popupMenu;

    private int navIconResId;
    private int menuIconRes;
    private int searchIconRes;
    private int speechIconRes;
    private int arrowIconRes;
    private int clearIconRes;

    private boolean speechMode;
    private int maxSuggestionCount;
    private boolean navButtonEnabled;
    private boolean roundedSearchBarEnabled;
    private int dividerColor;
    private int searchBarColor;

    private CharSequence hintText;
    private CharSequence placeholderText;
    private int textColor;
    private int hintColor;
    private int hintStyle;
    private int searchStyle;
    private int placeholderColor;
    private int navIconTint;
    private int menuIconTint;
    private int searchIconTint;
    private int arrowIconTint;
    private int clearIconTint;
    private int textSize;
    private boolean isMarquee;
    private int marqueeRepeatLimit = -1;

    private int  fontFamily = 0;

    private boolean navIconTintEnabled;
    private boolean menuIconTintEnabled;
    private boolean searchIconTintEnabled;
    private boolean arrowIconTintEnabled;
    private boolean clearIconTintEnabled;
    private boolean borderlessRippleEnabled = false;

    private int textCursorColor;
    private int highlightedTextColor;


    public enum TEXT_STYLES {
        NORMAL(0), BOLD(1), ITALIC(2);

        private final int val;

        TEXT_STYLES(int val) {
            this.val = val;
        }

        public int getVal() {
            return val;
        }
    }

    public MaterialSearchBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public MaterialSearchBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MaterialSearchBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        inflate(getContext(), R.layout.searchbar, this);

        TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.MaterialSearchBar);

        try{

            //Base Attributes
            speechMode = array.getBoolean(R.styleable.MaterialSearchBar_mt_speechMode, false);
            maxSuggestionCount = array.getInt(R.styleable.MaterialSearchBar_mt_maxSuggestionsCount, 3);
            navButtonEnabled = array.getBoolean(R.styleable.MaterialSearchBar_mt_navIconEnabled, false);
            roundedSearchBarEnabled = array.getBoolean(R.styleable.MaterialSearchBar_mt_roundedSearchBarEnabled, false);
            dividerColor = array.getColor(R.styleable.MaterialSearchBar_mt_dividerColor, ContextCompat.getColor(getContext(), R.color.searchBarDividerColor));
            searchBarColor = array.getColor(R.styleable.MaterialSearchBar_mt_searchBarColor, ContextCompat.getColor(getContext(), R.color.searchBarPrimaryColor));

            //Icon Related Attributes
            menuIconRes = array.getResourceId(R.styleable.MaterialSearchBar_mt_menuIconDrawable, R.drawable.ic_dots_vertical_black_48dp);
            searchIconRes = array.getResourceId(R.styleable.MaterialSearchBar_mt_searchIconDrawable, R.drawable.ic_magnify_black_48dp);
            speechIconRes = array.getResourceId(R.styleable.MaterialSearchBar_mt_speechIconDrawable, R.drawable.ic_microphone_black_48dp);
            arrowIconRes = array.getResourceId(R.styleable.MaterialSearchBar_mt_backIconDrawable, R.drawable.ic_arrow_left_black_48dp);
            clearIconRes = array.getResourceId(R.styleable.MaterialSearchBar_mt_clearIconDrawable, R.drawable.ic_close_black_48dp);
            navIconTint = array.getColor(R.styleable.MaterialSearchBar_mt_navIconTint, ContextCompat.getColor(getContext(), R.color.searchBarNavIconTintColor));
            menuIconTint = array.getColor(R.styleable.MaterialSearchBar_mt_menuIconTint, ContextCompat.getColor(getContext(), R.color.searchBarMenuIconTintColor));
            searchIconTint = array.getColor(R.styleable.MaterialSearchBar_mt_searchIconTint, ContextCompat.getColor(getContext(), R.color.searchBarSearchIconTintColor));
            arrowIconTint = array.getColor(R.styleable.MaterialSearchBar_mt_backIconTint, ContextCompat.getColor(getContext(), R.color.searchBarBackIconTintColor));
            clearIconTint = array.getColor(R.styleable.MaterialSearchBar_mt_clearIconTint, ContextCompat.getColor(getContext(), R.color.searchBarClearIconTintColor));
            navIconTintEnabled = array.getBoolean(R.styleable.MaterialSearchBar_mt_navIconUseTint, true);
            menuIconTintEnabled = array.getBoolean(R.styleable.MaterialSearchBar_mt_menuIconUseTint, true);
            searchIconTintEnabled = array.getBoolean(R.styleable.MaterialSearchBar_mt_searchIconUseTint, true);
            arrowIconTintEnabled = array.getBoolean(R.styleable.MaterialSearchBar_mt_backIconUseTint, true);
            clearIconTintEnabled = array.getBoolean(R.styleable.MaterialSearchBar_mt_clearIconUseTint, true);
            borderlessRippleEnabled = array.getBoolean(R.styleable.MaterialSearchBar_mt_borderlessRippleEnabled, false);
           fontFamily= array.getResourceId(R.styleable.MaterialSearchBar_android_fontFamily, 0);

            //Text Related Attributes
            hintText = array.getString(R.styleable.MaterialSearchBar_mt_hint);
            placeholderText = array.getString(R.styleable.MaterialSearchBar_mt_placeholder);
            textColor = array.getColor(R.styleable.MaterialSearchBar_mt_textColor, ContextCompat.getColor(getContext(), R.color.searchBarTextColor));
            textSize = array.getDimensionPixelSize(R.styleable.MaterialSearchBar_mt_textSize,12);
            hintColor = array.getColor(R.styleable.MaterialSearchBar_mt_hintColor, ContextCompat.getColor(getContext(), R.color.searchBarHintColor));
            hintStyle = array.getInt(R.styleable.MaterialSearchBar_mt_hintStyle,0);
            searchStyle = array.getInt(R.styleable.MaterialSearchBar_mt_searchStyle,0);


            placeholderColor = array.getColor(R.styleable.MaterialSearchBar_mt_placeholderColor, ContextCompat.getColor(getContext(), R.color.searchBarPlaceholderColor));
            textCursorColor = array.getColor(R.styleable.MaterialSearchBar_mt_textCursorTint, ContextCompat.getColor(getContext(), R.color.searchBarCursorColor));
            highlightedTextColor = array.getColor(R.styleable.MaterialSearchBar_mt_highlightedTextColor, ContextCompat.getColor(getContext(), R.color.searchBarTextHighlightColor));
            isMarquee = array.getBoolean(R.styleable.MaterialSearchBar_mt_marquee, false);
            marqueeRepeatLimit = array.getInt(R.styleable.MaterialSearchBar_mt_marqueeRepeatLimit,-1);
            destiny = getResources().getDisplayMetrics().density;
            if (adapter == null) {
                adapter = new DefaultSuggestionsAdapter(LayoutInflater.from(getContext()),fontFamily);
            }
            if (adapter instanceof DefaultSuggestionsAdapter)
                ((DefaultSuggestionsAdapter) adapter).setListener(this);
            adapter.setMaxSuggestionsCount(maxSuggestionCount);
            RecyclerView recyclerView = findViewById(R.id.mt_recycler);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            //View References
            searchBarCardView = findViewById(R.id.mt_container);
            suggestionDivider = findViewById(R.id.mt_divider);
            menuIcon = findViewById(R.id.mt_menu);
            clearIcon = findViewById(R.id.mt_clear);
            searchIcon = findViewById(R.id.mt_search);
            arrowIcon = findViewById(R.id.mt_arrow);
            searchEdit = findViewById(R.id.mt_editText);
            placeHolder = findViewById(R.id.mt_placeholder);
            inputContainer = findViewById(R.id.inputContainer);
            navIcon = findViewById(R.id.mt_nav);
            findViewById(R.id.mt_clear).setOnClickListener(this);

            //Listeners
            setOnClickListener(this);
            arrowIcon.setOnClickListener(this);
            searchIcon.setOnClickListener(this);
            searchEdit.setOnFocusChangeListener(this);
            searchEdit.setOnEditorActionListener(this);
            navIcon.setOnClickListener(this);

            postSetup();

        }finally {
            array.recycle();
        }

    }

    /**
     * Inflate menu for searchBar
     *
     * @param menuResource - menu resource
     */
    public void inflateMenu(int menuResource) {
        inflateMenuRequest(menuResource, -1);
    }

    /**
     * Inflate menu for searchBar with custom Icon
     *
     * @param menuResource - menu resource
     * @param icon         - icon resource id
     */
    public void inflateMenu(int menuResource, int icon) {
        inflateMenuRequest(menuResource, icon);
    }

    private void inflateMenuRequest(int menuResource, int iconResId
    ) {
        int menuResource1 = menuResource;
        if (menuResource1 > 0) {
            ImageView menuIcon = findViewById(R.id.mt_menu);
            if (iconResId != -1) {
                menuIconRes = iconResId;
                menuIcon.setImageResource(menuIconRes);
            }
            menuIcon.setVisibility(VISIBLE);
            menuIcon.setOnClickListener(this);
            popupMenu = new PopupMenu(getContext(), menuIcon);
            popupMenu.inflate(menuResource);
            popupMenu.setGravity(Gravity.RIGHT);
        }
    }

    /**
     * Get popup menu
     *
     * @return PopupMenu
     */
    public PopupMenu getMenu() {
        return this.popupMenu;
    }

    private void postSetup() {
        setupText();
        setupRoundedSearchBarEnabled();
        setupSearchBarColor();
        setupIcons();
        setupSearchEditText();

        setFontFamily();


    }



    private void setFontFamily() {
        if (fontFamily > 0) {
            placeHolder.setTypeface(ResourcesCompat.getFont(getContext(), fontFamily));
            searchEdit.setTypeface(ResourcesCompat.getFont(getContext(), fontFamily));
        }
    }


    private void setupText(){
        setupTextColors();
        setupTextSize();
        setupEffects();
    }


    /**
     * Capsule shaped searchbar enabled
     * Only works on SDK V21+ due to odd behavior on lower
     */
    private void setupRoundedSearchBarEnabled() {
        if (roundedSearchBarEnabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            searchBarCardView.setRadius(getResources().getDimension(R.dimen.corner_radius_rounded));
        } else {
            searchBarCardView.setRadius(getResources().getDimension(R.dimen.corner_radius_default));
        }
    }

    private void setupSearchBarColor() {
        searchBarCardView.setCardBackgroundColor(searchBarColor);
        setupDividerColor();
    }

    private void setupDividerColor() {
        suggestionDivider.setBackgroundColor(dividerColor);
    }

    private void setupEffects(){
        setupMarquee();
        setupPlaceHolderFontStyle();
        setupSearchViewStyle();
    }



    private void setupSearchViewStyle() {

        if(searchStyle==0){
            searchEdit.setTypeface(null, Typeface.NORMAL);
            return;
        }
        if((searchStyle & PrefixStyle.BOLD) == PrefixStyle.BOLD) {
            searchEdit.setTypeface(null, Typeface.BOLD);

        }
        if((searchStyle & PrefixStyle.ITALIC) == PrefixStyle.ITALIC) {
            searchEdit.setTypeface(null, Typeface.ITALIC);
        }
        if((searchStyle & PrefixStyle.NORMAL) == PrefixStyle.NORMAL) {
            searchEdit.setTypeface(null, Typeface.NORMAL);
        }
    }

    private void setupPlaceHolderFontStyle() {

        if(hintStyle==0){
            placeHolder.setTypeface(null, Typeface.NORMAL);
            return;
        }
        if((hintStyle & PrefixStyle.BOLD) == PrefixStyle.BOLD) {
            placeHolder.setTypeface(null, Typeface.BOLD);

        }
        if((hintStyle & PrefixStyle.ITALIC) == PrefixStyle.ITALIC) {
            placeHolder.setTypeface(null, Typeface.ITALIC);
           }
        if((hintStyle & PrefixStyle.NORMAL) == PrefixStyle.NORMAL) {
            placeHolder.setTypeface(null, Typeface.NORMAL);
           }
    }

    private void setupMarquee(){
        if(isMarquee){
            placeHolder.setHorizontallyScrolling(true);
            placeHolder.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            placeHolder.setMarqueeRepeatLimit(marqueeRepeatLimit);
            placeHolder.setSelected(true);
        }
    }

    private void setupTextColors() {
        searchEdit.setHintTextColor(hintColor);
        searchEdit.setTextColor(textColor);
        placeHolder.setTextColor(placeholderColor);
    }

    private void setupTextSize(){
        searchEdit.setTextSize(textSize);
        placeHolder.setTextSize(textSize);
    }



    /**
     * Setup editText coloring and drawables
     */
    private void setupSearchEditText() {
        setupCursorColor();
        searchEdit.setHighlightColor(highlightedTextColor);

        if (hintText != null)
            searchEdit.setHint(hintText);
        if (placeholderText != null) {
            arrowIcon.setBackground(null);
            placeHolder.setText(placeholderText);
        }
    }

    private void setupCursorColor() {
        try {
            Field field = TextView.class.getDeclaredField("mEditor");
            field.setAccessible(true);
            Object editor = field.get(searchEdit);

            field = TextView.class.getDeclaredField("mCursorDrawableRes");
            field.setAccessible(true);
            int cursorDrawableRes = field.getInt(searchEdit);
            Drawable cursorDrawable = ContextCompat.getDrawable(getContext(), cursorDrawableRes).mutate();
            cursorDrawable.setColorFilter(textCursorColor, PorterDuff.Mode.SRC_IN);
            Drawable[] drawables = {cursorDrawable, cursorDrawable};
            field = editor.getClass().getDeclaredField("mCursorDrawable");
            field.setAccessible(true);
            field.set(editor, drawables);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    //Setup Icon Colors And Drawables
    private void setupIcons() {
        //Drawables
        //Animated Nav Icon
        navIconResId = R.drawable.ic_menu_animated;
        this.navIcon.setImageResource(navIconResId);
        setNavButtonEnabled(navButtonEnabled);

        //Menu
        if (popupMenu == null) {
            findViewById(R.id.mt_menu).setVisibility(GONE);
        }

        //Search
        setSpeechMode(speechMode);

        //Arrow
        this.arrowIcon.setImageResource(arrowIconRes);

        //Clear
        this.clearIcon.setImageResource(clearIconRes);

        //Colors
        setupNavIconTint();
        setupMenuIconTint();
        setupSearchIconTint();
        setupArrowIconTint();
        setupClearIconTint();
        setupIconRippleStyle();
    }

    private void setupNavIconTint() {
        if (navIconTintEnabled) {
            navIcon.setColorFilter(navIconTint, PorterDuff.Mode.SRC_IN);
        } else {
            navIcon.clearColorFilter();
        }
    }

    private void setupMenuIconTint() {
        if (menuIconTintEnabled) {
            menuIcon.setColorFilter(menuIconTint, PorterDuff.Mode.SRC_IN);
        } else {
            menuIcon.clearColorFilter();
        }
    }

    private void setupSearchIconTint() {
        if (searchIconTintEnabled) {
            searchIcon.setColorFilter(searchIconTint, PorterDuff.Mode.SRC_IN);
        } else {
            searchIcon.clearColorFilter();
        }
    }

    private void setupArrowIconTint() {
        if (arrowIconTintEnabled) {
            arrowIcon.setColorFilter(arrowIconTint, PorterDuff.Mode.SRC_IN);
        } else {
            arrowIcon.clearColorFilter();
        }
    }

    private void setupClearIconTint() {
        if (clearIconTintEnabled) {
            clearIcon.setColorFilter(clearIconTint, PorterDuff.Mode.SRC_IN);
        } else {
            clearIcon.clearColorFilter();
        }
    }

    private void setupIconRippleStyle() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            TypedValue rippleStyle = new TypedValue();
            if (borderlessRippleEnabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, rippleStyle, true);
            } else {
                getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, rippleStyle, true);
            }
            navIcon.setBackgroundResource(rippleStyle.resourceId);
            searchIcon.setBackgroundResource(rippleStyle.resourceId);
            menuIcon.setBackgroundResource(rippleStyle.resourceId);
            arrowIcon.setBackgroundResource(rippleStyle.resourceId);
            clearIcon.setBackgroundResource(rippleStyle.resourceId);
        } else {
            Log.w(TAG, "setupIconRippleStyle() Only Available On SDK Versions Higher Than 16!");
        }
    }

    /**
     * Register listener for search bar callbacks.
     *
     * @param onSearchActionListener the callback listener
     */
    public void setOnSearchActionListener(OnSearchActionListener onSearchActionListener) {
        this.onSearchActionListener = onSearchActionListener;
    }

    /**
     * Hides search input and close arrow
     */
    public void closeSearch() {
        animateNavIcon(false);
        searchOpened = false;
        Animation out = AnimationUtils.loadAnimation(getContext(), R.anim.fade_out);
        Animation in = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in_right);
        out.setAnimationListener(this);
        searchIcon.setVisibility(VISIBLE);
        inputContainer.startAnimation(out);
        searchIcon.startAnimation(in);

        if (placeholderText != null) {
            placeHolder.setVisibility(VISIBLE);
            placeHolder.startAnimation(in);
        }
        if (listenerExists())
            onSearchActionListener.onSearchStateChanged(false);
        if (suggestionsVisible) animateSuggestions(getListHeight(false), 0);
    }

    /**
     * Shows search input and close arrow
     */
    public void openSearch() {
        if (isSearchOpened()) {
            onSearchActionListener.onSearchStateChanged(true);
            searchEdit.requestFocus();
            return;
        }
        animateNavIcon(true);
        adapter.notifyDataSetChanged();
        searchOpened = true;
        Animation left_in = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in_left);
        Animation left_out = AnimationUtils.loadAnimation(getContext(), R.anim.fade_out_left);
        left_in.setAnimationListener(this);
        placeHolder.setVisibility(GONE);
        inputContainer.setVisibility(VISIBLE);
        inputContainer.startAnimation(left_in);
        if (listenerExists()) {
            onSearchActionListener.onSearchStateChanged(true);
        }
        searchIcon.startAnimation(left_out);
    }

    private void animateNavIcon(boolean menuState) {
        if (menuState) {
            this.navIcon.setImageResource(R.drawable.ic_menu_animated);
        } else {
            this.navIcon.setImageResource(R.drawable.ic_back_animated);
        }
        Drawable mDrawable = navIcon.getDrawable();
        if (mDrawable instanceof Animatable) {
            ((Animatable) mDrawable).start();
        }
    }

    private void animateSuggestions(int from, int to) {
        suggestionsVisible = to > 0;
        final RecyclerView suggestionsList = findViewById(R.id.mt_recycler);
        final ViewGroup.LayoutParams lp = suggestionsList.getLayoutParams();
        if (to == 0 && lp.height == 0)
            return;
        findViewById(R.id.mt_divider).setVisibility(to > 0 ? View.VISIBLE : View.GONE);

        ValueAnimator animator = ValueAnimator.ofInt(from, to);
        animator.setDuration(1200);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                lp.height = (int) animation.getAnimatedValue();
                suggestionsList.setLayoutParams(lp);
            }
        });
        if (adapter.getItemCount() > 0)
            animator.start();
    }

    public void showSuggestionsList() {
        animateSuggestions(0, getListHeight(false));
    }

    public void hideSuggestionsList() {
        animateSuggestions(getListHeight(false), 0);
    }

    public void clearSuggestions() {
        if (suggestionsVisible)
            animateSuggestions(getListHeight(false), 0);
        adapter.clearSuggestions();
    }

    /**
     * Check if suggestions are shown
     *
     * @return return result
     */
    public boolean isSuggestionsVisible() {
        return suggestionsVisible;
    }

    /**
     * Check if suggestions are enabled
     */
    public boolean isSuggestionsEnabled() {
        return isSuggestionsEnabled;
    }

    /**
     * Set suggestions enabled
     */
    public void setSuggestionsEnabled(boolean suggestionsEnabled) {
        isSuggestionsEnabled = suggestionsEnabled;
    }

    /**
     * Set Menu Icon Drawable
     *
     * @param menuIconResId icon resource id
     */
    public void setMenuIcon(int menuIconResId) {
        this.menuIconRes = menuIconResId;
        this.menuIcon.setImageResource(this.menuIconRes);
    }

    /**
     * Set search icon drawable
     *
     * @param searchIconResId icon resource id
     */
    public void setSearchIcon(int searchIconResId) {
        this.searchIconRes = searchIconResId;
        this.searchIcon.setImageResource(searchIconResId);
    }

    /**
     * Set back arrow icon drawable
     *
     * @param arrowIconResId icon resource id
     */
    public void setArrowIcon(int arrowIconResId) {
        this.arrowIconRes = arrowIconResId;
        this.arrowIcon.setImageResource(arrowIconRes);
    }

    /**
     * Set clear icon drawable
     *
     * @param clearIconResId icon resource id
     */
    public void setClearIcon(int clearIconResId) {
        this.clearIconRes = clearIconResId;
        this.clearIcon.setImageResource(clearIconRes);
    }

    /**
     * Set the tint color of the navigation icon
     *
     * @param navIconTint nav icon color
     */
    public void setNavIconTint(int navIconTint) {
        this.navIconTint = navIconTint;
        setupNavIconTint();
    }

    /**
     * Set the tint color of the menu icon
     *
     * @param menuIconTint menu icon color
     */
    public void setMenuIconTint(int menuIconTint) {
        this.menuIconTint = menuIconTint;
        setupMenuIconTint();
    }

    /**
     * Set the tint color of the search/speech icon
     *
     * @param searchIconTint search icon color
     */
    public void setSearchIconTint(int searchIconTint) {
        this.searchIconTint = searchIconTint;
        setupSearchIconTint();
    }

    /**
     * Set the tint color of the back arrow icon
     *
     * @param arrowIconTint arrow icon color
     */
    public void setArrowIconTint(int arrowIconTint) {
        this.arrowIconTint = arrowIconTint;
        setupArrowIconTint();
    }

    /**
     * Set the tint color of the clear icon
     *
     * @param clearIconTint clear icon tint
     */
    public void setClearIconTint(int clearIconTint) {
        this.clearIconTint = clearIconTint;
        setupClearIconTint();
    }

    /**
     * Show a borderless ripple(circular) when icon is pressed
     * Borderless only available on SDK V21+
     *
     * @param borderlessRippleEnabled true for borderless, false for default
     */
    public void setIconRippleStyle(boolean borderlessRippleEnabled) {
        this.borderlessRippleEnabled = borderlessRippleEnabled;
        setupIconRippleStyle();
    }

    /**
     * Sets search bar hintText
     *
     * @param hintText hintText text
     */
    public void setHint(CharSequence hintText) {
        this.hintText = hintText;
        searchEdit.setHint(hintText);
    }

    /**
     * Set the place holder text
     *
     * @return placeholder text
     */
    public CharSequence getPlaceHolderText() {
        return placeHolder.getText();
    }

    /**
     * sets the speechMode for the search bar.
     * If set to true, microphone icon will display instead of the search icon.
     * Also clicking on this icon will trigger the callback method onButtonClicked()
     *
     * @param speechMode enable speech
     * @see #BUTTON_SPEECH
     * @see OnSearchActionListener#onButtonClicked(int)
     */
    public void setSpeechMode(boolean speechMode) {
        this.speechMode = speechMode;
        if (speechMode) {
            searchIcon.setImageResource(speechIconRes);
            searchIcon.setClickable(true);
        } else {
            searchIcon.setImageResource(searchIconRes);
            searchIcon.setClickable(false);
        }
    }

    /**
     * True if MaterialSearchBar is in speech mode
     *
     * @return speech mode
     */
    public boolean isSpeechModeEnabled() {
        return speechMode;
    }

    /**
     * Check if search bar is in edit mode
     *
     * @return true if search bar is in edit mode
     */
    public boolean isSearchOpened() {
        return searchOpened;
    }

    /**
     * Specifies the maximum number of search queries stored until the activity is destroyed
     *
     * @param maxSuggestionsCount maximum queries
     */
    public void setMaxSuggestionCount(int maxSuggestionsCount) {
        this.maxSuggestionCount = maxSuggestionsCount;
        adapter.setMaxSuggestionsCount(maxSuggestionsCount);
    }

    /**
     * Sets a custom adapter for suggestions list view.
     *
     * @param suggestionAdapter customized adapter
     */
    public void setCustomSuggestionAdapter(SuggestionsAdapter suggestionAdapter) {
        this.adapter = suggestionAdapter;
        RecyclerView recyclerView = findViewById(R.id.mt_recycler);
        recyclerView.setAdapter(adapter);
    }

    /**
     * Returns the last search queries.
     * The queries are stored only for the duration of one activity session.
     * When the activity is destroyed, the queries will be deleted.
     * To save queries, use the method getLastSuggestions().
     * To recover the queries use the method setLastSuggestions().
     * <p><b color="red">List< String >  will be returned if You don't use custom adapter.</b></p>
     *
     * @return array with the latest search queries
     * @see #setLastSuggestions(List)
     * @see #setMaxSuggestionCount(int)
     */
    public List getLastSuggestions() {
        return adapter.getSuggestions();
    }

    /**
     * Sets the array of recent search queries.
     * It is advisable to save the queries when the activity is destroyed
     * and call this method when creating the activity.
     * <p><b color="red">Pass a List< String > if You don't use custom adapter.</b></p>
     *
     * @param suggestions an array of queries
     * @see #getLastSuggestions()
     * @see #setMaxSuggestionCount(int)
     */
    public void setLastSuggestions(List suggestions) {
        adapter.setSuggestions(suggestions);
    }

    /**
     * Changes the array of recent search queries with animation.
     * <p><b color="red">Pass a List< String >  if You don't use custom adapter.</b></p>
     *
     * @param suggestions an array of queries
     */
    public void updateLastSuggestions(List suggestions) {
        int startHeight = getListHeight(false);
        if (suggestions.size() > 0) {
            List newSuggestions = new ArrayList<>(suggestions);
            adapter.setSuggestions(newSuggestions);
            animateSuggestions(startHeight, getListHeight(false));
        } else {
            animateSuggestions(startHeight, 0);
        }
    }

    /**
     * Allows you to intercept the suggestions click event
     * <p><b color="red">This method will not work with custom Suggestion Adapter</b></p>
     *
     * @param listener click listener
     */
    public void setSuggestionsClickListener(SuggestionsAdapter.OnItemViewClickListener listener) {
        if (adapter instanceof DefaultSuggestionsAdapter)
            ((DefaultSuggestionsAdapter) adapter).setListener(listener);
    }

    /**
     * Set search input text color
     *
     * @param textColor text color
     */
    public void setTextColor(int textColor) {
        this.textColor = textColor;
        setupTextColors();
    }

    /**
     * Set text input hintText color
     *
     * @param hintColor text hintText color
     */
    public void setTextHintColor(int hintColor) {
        this.hintColor = hintColor;
        setupTextColors();
    }

    /**
     * Set placeholder text color
     *
     * @param placeholderColor placeholder color
     */
    public void setPlaceHolderColor(int placeholderColor) {
        this.placeholderColor = placeholderColor;
        setupTextColors();
    }

    /**
     * Set the color of the highlight when text is selected
     *
     * @param highlightedTextColor selected text highlight color
     */
    public void setTextHighlightColor(int highlightedTextColor) {
        this.highlightedTextColor = highlightedTextColor;
        searchEdit.setHighlightColor(highlightedTextColor);
    }

    public void setDividerColor(int dividerColor) {
        this.dividerColor = dividerColor;
        setupDividerColor();
    }

    /**
     * Set navigation drawer menu icon enabled
     *
     * @param navButtonEnabled icon enabled
     */
    public void setNavButtonEnabled(boolean navButtonEnabled) {
        this.navButtonEnabled = navButtonEnabled;
        if (navButtonEnabled) {
            navIcon.setVisibility(VISIBLE);
            navIcon.setClickable(true);
            arrowIcon.setVisibility(GONE);
        } else {
            navIcon.setVisibility(GONE);
            navIcon.setClickable(false);
            arrowIcon.setVisibility(VISIBLE);
        }
        navIcon.requestLayout();
        placeHolder.requestLayout();
        arrowIcon.requestLayout();
    }

    /**
     * Enable capsule shaped SearchBar (API 21+)
     *
     * @param roundedSearchBarEnabled capsule shape enabled
     * @
     */
    public void setRoundedSearchBarEnabled(boolean roundedSearchBarEnabled) {
        this.roundedSearchBarEnabled = roundedSearchBarEnabled;
        setupRoundedSearchBarEnabled();
    }

    /**
     * Set CardView elevation
     *
     * @param elevation desired elevation
     */
    public void setCardViewElevation(int elevation) {
        CardView cardView = findViewById(R.id.mt_container);
        cardView.setCardElevation(elevation);
    }

    /**
     * Get search text
     *
     * @return text
     */
    public String getText() {
        return searchEdit.getText().toString();
    }

    /**
     * Set search text
     *
     * @param text text
     */
    public void setText(String text) {
        searchEdit.setText(text);
    }

    /**
     * Add text watcher to searchbar's EditText
     *
     * @param textWatcher textWatcher to add
     */
    public void addTextChangeListener(TextWatcher textWatcher) {
        searchEdit.addTextChangedListener(textWatcher);
    }

    public EditText getSearchEditText() {
        return searchEdit;
    }

    public TextView getPlaceHolderView() {
        return placeHolder;
    }

    /**
     * Set the place holder text
     *
     * @param placeholder placeholder text
     */
    public void setPlaceHolder(CharSequence placeholder) {
        this.placeholderText = placeholder;
        placeHolder.setText(placeholder);
    }

    private boolean listenerExists() {
        return onSearchActionListener != null;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == getId()) {
            if (!searchOpened) {
                openSearch();
            }
        } else if (id == R.id.mt_arrow) {
            closeSearch();
        } else if (id == R.id.mt_search) {
            if (listenerExists())
                onSearchActionListener.onButtonClicked(BUTTON_SPEECH);
        } else if (id == R.id.mt_clear) {
            searchEdit.setText("");
        } else if (id == R.id.mt_menu) {
            popupMenu.show();
        } else if (id == R.id.mt_nav) {
            int button = searchOpened ? BUTTON_BACK : BUTTON_NAVIGATION;
            if (searchOpened) {
                closeSearch();
            }
            if (listenerExists()) {
                onSearchActionListener.onButtonClicked(button);
            }
        }
    }

    @Override
    public void onAnimationStart(Animation animation) {
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        if (!searchOpened) {
            inputContainer.setVisibility(GONE);
            searchEdit.setText("");
        } else {
            searchIcon.setVisibility(GONE);
            searchEdit.requestFocus();
            if (!suggestionsVisible && isSuggestionsEnabled)
                showSuggestionsList();
        }
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (hasFocus) {
            imm.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT);
        } else {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (listenerExists())
            onSearchActionListener.onSearchConfirmed(searchEdit.getText());
        if (suggestionsVisible)
            hideSuggestionsList();
        if (adapter instanceof DefaultSuggestionsAdapter)
            adapter.addSuggestion(searchEdit.getText().toString());
        return true;
    }

    /**
     * For calculate the height change when item delete or add animation
     * false is return the full height of item,
     * true is return the height of position subtraction one
     *
     * @param isSubtraction is subtraction enabled
     */
    private int getListHeight(boolean isSubtraction) {
        if (!isSubtraction)
            return (int) (adapter.getListHeight() * destiny);
        return (int) (((adapter.getItemCount() - 1) * adapter.getSingleViewHeight()) * destiny);
    }

    @Override
    public void OnItemClickListener(int position, View v) {
        if (v.getTag() instanceof String) {
            searchEdit.setText((String) v.getTag());
        }
    }

    @Override
    public void OnItemDeleteListener(int position, View v) {
        if (v.getTag() instanceof String) {
            /*Order of two line should't be change,
            because should calculate the height of item first*/
            animateSuggestions(getListHeight(false), getListHeight(true));
            adapter.deleteSuggestion(position, v.getTag());
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        SavedState savedState = new SavedState(super.onSaveInstanceState());
        savedState.isSearchBarVisible = searchOpened ? VIEW_VISIBLE : VIEW_INVISIBLE;
        savedState.suggestionsVisible = suggestionsVisible ? VIEW_VISIBLE : VIEW_INVISIBLE;
        savedState.speechMode = speechMode ? VIEW_VISIBLE : VIEW_INVISIBLE;
        savedState.navIconResId = navIconResId;
        savedState.searchIconRes = searchIconRes;
        savedState.suggestions = getLastSuggestions();
        savedState.maxSuggestions = maxSuggestionCount;
        if (hintText != null) savedState.hint = hintText.toString();
        return savedState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        searchOpened = savedState.isSearchBarVisible == VIEW_VISIBLE;
        suggestionsVisible = savedState.suggestionsVisible == VIEW_VISIBLE;
        setLastSuggestions(savedState.suggestions);
        if (suggestionsVisible)
            animateSuggestions(0, getListHeight(false));
        if (searchOpened) {
            inputContainer.setVisibility(VISIBLE);
            placeHolder.setVisibility(GONE);
            searchIcon.setVisibility(GONE);
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && searchOpened) {
            animateSuggestions(getListHeight(false), 0);
            closeSearch();
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    /**
     * Interface definition for MaterialSearchBar callbacks.
     */
    public interface OnSearchActionListener {
        /**
         * Invoked when SearchBar opened or closed
         *
         * @param enabled state
         */
        void onSearchStateChanged(boolean enabled);

        /**
         * Invoked when search confirmed and "search" button is clicked on the soft keyboard
         *
         * @param text search input
         */
        void onSearchConfirmed(CharSequence text);

        /**
         * Invoked when "speech" or "navigation" buttons clicked.
         *
         * @param buttonCode {@link #BUTTON_NAVIGATION}, {@link #BUTTON_SPEECH} or {@link #BUTTON_BACK} will be passed
         */
        void onButtonClicked(int buttonCode);
    }

    private static class SavedState extends BaseSavedState {
        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel source) {
                return new SavedState(source);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
        private int isSearchBarVisible;
        private int suggestionsVisible;
        private int speechMode;
        private int searchIconRes;
        private int navIconResId;
        private String hint;
        private List suggestions;
        private int maxSuggestions;

        public SavedState(Parcel source) {
            super(source);
            isSearchBarVisible = source.readInt();
            suggestionsVisible = source.readInt();
            speechMode = source.readInt();

            navIconResId = source.readInt();
            searchIconRes = source.readInt();
            hint = source.readString();
            suggestions = source.readArrayList(null);
            maxSuggestions = source.readInt();
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(isSearchBarVisible);
            out.writeInt(suggestionsVisible);
            out.writeInt(speechMode);

            out.writeInt(searchIconRes);
            out.writeInt(navIconResId);
            out.writeString(hint);
            out.writeList(suggestions);
            out.writeInt(maxSuggestions);
        }
    }
}
