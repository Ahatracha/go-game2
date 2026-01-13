package com.roahacha.gogame;

import com.roahacha.gogame.Common.Stone;
import com.roahacha.gogame.Common.GameAction;

public interface GameClientObserver {
    void onGameStart(GameAction action);
    void onStonePlaced(int row, int col, Stone stone);
    void onBoardUpdate(Stone[][] grid);
    void onGameAction(GameAction action);
}
