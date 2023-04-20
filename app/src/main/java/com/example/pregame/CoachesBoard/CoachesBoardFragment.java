package com.example.pregame.CoachesBoard;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.pregame.R;

@SuppressLint("ClickableViewAccessibility")
public class CoachesBoardFragment extends Fragment {
    public static final String TAG = "CoachesBoardFragment";
    private View view;
    private ViewGroup mainLayout;
    private ImageView basketballIv, offensivePlayerIv, defensivePlayerIv, movementIv, passingIv, screenIv, addPageIv, deletePageIv, menuIv;
    private String selectedItem = "";
    private int xDelta, yDelta;

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
        ImageView imageView = new ImageView(getContext());
        setItemBackground(imageView);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(90, 90);
        params.setMargins(xCord, yCord, 0, 0);
        imageView.setLayoutParams(params);
        mainLayout.addView(imageView);
        imageView.setOnTouchListener(onTouchListener());
    }

    private void setItemBackground(ImageView imageView) {
        switch (selectedItem) {
            case "Basketball":
                imageView.setBackgroundResource(R.drawable.basketball);
                break;
            case "Offence":
                imageView.setBackgroundResource(R.drawable.offensive_player);
                break;
            case "Defence":
                imageView.setBackgroundResource(R.drawable.defensive_player);
                break;
            default:
                imageView.setBackgroundResource(R.drawable.ic_add);
                break;
        }
    }

    private void setup() {
        basketballIv = view.findViewById(R.id.hsv_basketball_iv);
        offensivePlayerIv = view.findViewById(R.id.hsv_offensive_player_iv);
        defensivePlayerIv = view.findViewById(R.id.hsv_defensive_player_iv);
        movementIv = view.findViewById(R.id.hsv_player_movement_iv);
        passingIv = view.findViewById(R.id.hsv_player_pass_iv);
        screenIv = view.findViewById(R.id.hsv_player_screen_iv);
        addPageIv = view.findViewById(R.id.hsv_play_plus_iv);
        deletePageIv = view.findViewById(R.id.hsv_play_minus_iv);
        menuIv = view.findViewById(R.id.hsv_play_menu_iv);
        mainLayout = view.findViewById(R.id.basketball_court);

        selectItem(basketballIv, "Basketball");
        selectItem(offensivePlayerIv, "Offence");
        selectItem(defensivePlayerIv, "Defence");

        mainLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    int xCord = (int) motionEvent.getX();
                    int yCord = (int) motionEvent.getY();
                    if (!selectedItem.isEmpty())
                        createItem(xCord, yCord);
                }
                return true;
            }
        });
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
        addPageIv.setBackgroundResource(R.drawable.transparent_border);
        deletePageIv.setBackgroundResource(R.drawable.transparent_border);
        menuIv.setBackgroundResource(R.drawable.transparent_border);
    }
}