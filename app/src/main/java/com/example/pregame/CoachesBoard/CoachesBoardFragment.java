package com.example.pregame.CoachesBoard;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.pregame.R;

import java.util.ArrayList;

@SuppressLint("ClickableViewAccessibility")
public class CoachesBoardFragment extends Fragment {
    public static final String TAG = "CoachesBoardFragment";
    private View view;
    private ViewGroup mainLayout;
    private ImageView basketballIv, offensivePlayerIv, defensivePlayerIv, movementIv, passingIv, screenIv, removeItemIv, addItemIv, addPageIv, deletePageIv, menuIv;
    private String selectedItem = "", courtType = "Full Court ";
    private final ArrayList<TextView> itemsOnScreen = new ArrayList<>();
    private final ArrayList<TextView> removedItems = new ArrayList<>();
    private int xDelta, yDelta, basketballCount = 0, offensivePlayerCount = 0, defensivePlayerCount = 0;

    public CoachesBoardFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       view = inflater.inflate(R.layout.fragment_coaches_board, container, false);

       setup();

       return view;
    }

    private View.OnTouchListener onTouchListener() {
        return new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                int x = (int) event.getRawX();
                int y = (int) event.getRawY();

                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                        xDelta = x - lParams.leftMargin;
                        yDelta = y - lParams.topMargin;
                        break;

                    case MotionEvent.ACTION_UP:
                        break;

                    case MotionEvent.ACTION_MOVE:
                        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                        layoutParams.leftMargin = x - xDelta;
                        layoutParams.topMargin = y - yDelta;
                        layoutParams.rightMargin = 0;
                        layoutParams.bottomMargin = 0;
                        view.setLayoutParams(layoutParams);
                        break;
                }
                mainLayout.invalidate();
                return true;
            }
        };
    }

    private void createItem(int xCord, int yCord) {
        TextView textView = new TextView(getContext());
        setItemBackground(textView);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(90, 90);
        params.setMargins(xCord, yCord, 0, 0);
        textView.setLayoutParams(params);
        mainLayout.addView(textView);
        itemsOnScreen.add(textView);
        textView.setOnTouchListener(onTouchListener());
    }

    private void setItemBackground(TextView textView) {
        switch (selectedItem) {
            case "Basketball":
                basketballCount += 1;
                textView.setBackgroundResource(R.drawable.basketball);
                textView.setTag("BC" + basketballCount);
                break;
            case "Offence":
                offensivePlayerCount += 1;
                setTextViewAttributes(textView, R.drawable.offensive_player, offensivePlayerCount, "OP");
                break;
            case "Defence":
                defensivePlayerCount += 1;
                setTextViewAttributes(textView, R.drawable.defensive_player, defensivePlayerCount, "DP");
                break;
            default:
                break;
        }
    }

    private void setTextViewAttributes(TextView textView, int id, int count, String tag) {
        textView.setBackgroundResource(id);
        textView.setText(String.valueOf(count));
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(getResources().getColor(R.color.white));
        textView.setTextSize(18);
        textView.setTag(tag + count);
    }

    private void removeItemFromScreen() {
        if (!itemsOnScreen.isEmpty()) {
            int lastPosition = itemsOnScreen.size() - 1;
            TextView lastTv = itemsOnScreen.get(lastPosition);
            mainLayout.removeView(lastTv);
            itemsOnScreen.remove(lastPosition);
            removedItems.add(lastTv);
            decreaseCount(lastTv);
        } else {
            Log.e(TAG, "There's nothing to remove from the screen");
        }
    }

    private void addItemBackToScreen() {
        if (!removedItems.isEmpty()) {
            int lastPosition = removedItems.size() - 1;
            TextView addBackTv = removedItems.get(lastPosition);
            mainLayout.addView(addBackTv);
            itemsOnScreen.add(addBackTv);
            removedItems.remove(lastPosition);
            increaseCount(addBackTv);
            addBackTv.setOnTouchListener(onTouchListener());
        } else {
            Log.e(TAG, "There's nothing to add back to the screen");
        }
    }

    private void decreaseCount(TextView textView) {
        String textViewTag = (String) textView.getTag();

        if (textViewTag.contains("BC"))
            basketballCount -= 1;
        else if (textViewTag.contains("OP"))
            offensivePlayerCount -= 1;
        else if (textViewTag.contains("DP"))
            defensivePlayerCount -= 1;
        else
            Log.e(TAG, "Can't decrease that count");
    }

    private void increaseCount(TextView textView) {
        String textViewTag = (String) textView.getTag();

        if (textViewTag.contains("BC"))
            basketballCount += 1;
        else if (textViewTag.contains("OP"))
            offensivePlayerCount += 1;
        else if (textViewTag.contains("DP"))
            defensivePlayerCount += 1;
        else
            Log.e(TAG, "Can't increase that count");
    }

    private void setup() {
        basketballIv = view.findViewById(R.id.hsv_basketball_iv);
        offensivePlayerIv = view.findViewById(R.id.hsv_offensive_player_iv);
        defensivePlayerIv = view.findViewById(R.id.hsv_defensive_player_iv);
        movementIv = view.findViewById(R.id.hsv_player_movement_iv);
        passingIv = view.findViewById(R.id.hsv_player_pass_iv);
        screenIv = view.findViewById(R.id.hsv_player_screen_iv);
        removeItemIv = view.findViewById(R.id.hsv_remove_item_iv);
        addItemIv = view.findViewById(R.id.hsv_add_item_iv);
        addPageIv = view.findViewById(R.id.hsv_play_plus_iv);
        deletePageIv = view.findViewById(R.id.hsv_play_minus_iv);
        menuIv = view.findViewById(R.id.hsv_play_menu_iv);
        mainLayout = view.findViewById(R.id.basketball_court);

        removeItemIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeItemFromScreen();
            }
        });

        addItemIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItemBackToScreen();
            }
        });

        menuIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupMenu(menuIv);
            }
        });

        selectItem(basketballIv, "Basketball");
        selectItem(offensivePlayerIv, "Offence");
        selectItem(defensivePlayerIv, "Defence");
        selectItem(movementIv, "Movement");
        selectItem(passingIv, "Pass");
        selectItem(screenIv, "Screen");

        mainLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    int xCord = (int) motionEvent.getX();
                    int yCord = (int) motionEvent.getY();

                    if (selectedItem.equals("Basketball") && basketballCount == 0)
                        createItem(xCord, yCord);
                    else if (selectedItem.equals("Offence") && offensivePlayerCount < 5)
                        createItem(xCord, yCord);
                    else if (selectedItem.equals("Defence") && defensivePlayerCount < 5)
                        createItem(xCord, yCord);
                    else
                        Log.e(TAG, "Can't add item " + selectedItem + " to the screen.");

                }
                return true;
            }
        });
    }

    private void popupMenu(ImageView imageView) {
        PopupMenu menu = new PopupMenu(getContext(), imageView);
        menu.getMenuInflater().inflate(R.menu.coaches_board_menu, menu.getMenu());

        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.nav_clear_screen) {
                    basketballCount = 0; defensivePlayerCount = 0; offensivePlayerCount = 0;
                    itemsOnScreen.removeAll(itemsOnScreen); removedItems.removeAll(removedItems);
                    mainLayout.removeAllViews();
                    return true;
                }
                if (menuItem.getItemId() == R.id.nav_full_court) {
                    mainLayout.setBackgroundResource(R.drawable.basketball_court_full);
                    courtType = "Full Court";
                    return true;
                }
                if (menuItem.getItemId() == R.id.nav_half_court) {
                    mainLayout.setBackgroundResource(R.drawable.basketball_court_half);
                    courtType = "Half Court";
                    return true;
                }
                return false;
            }
        });
        menu.show();
    }

    private void selectItem(ImageView imageView, String itemName) {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearSelectedItemsBackgrounds();
                selectedItem = itemName;
                imageView.setBackgroundResource(R.drawable.black_border);
            }
        });
    }

    private void clearSelectedItemsBackgrounds() {
        basketballIv.setBackgroundResource(R.drawable.transparent_border);
        offensivePlayerIv.setBackgroundResource(R.drawable.transparent_border);
        defensivePlayerIv.setBackgroundResource(R.drawable.transparent_border);
        movementIv.setBackgroundResource(R.drawable.transparent_border);
        passingIv.setBackgroundResource(R.drawable.transparent_border);
        screenIv.setBackgroundResource(R.drawable.transparent_border);
        removeItemIv.setBackgroundResource(R.drawable.transparent_border);
        addItemIv.setBackgroundResource(R.drawable.transparent_border);
        addPageIv.setBackgroundResource(R.drawable.transparent_border);
        deletePageIv.setBackgroundResource(R.drawable.transparent_border);
        menuIv.setBackgroundResource(R.drawable.transparent_border);
    }
}