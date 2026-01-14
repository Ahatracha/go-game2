package com.roahacha.gogame.Common;

public enum GameAction {
    // PLAYER_* actions are sent by players
    PLAYER_MOVE(1),
    PLAYER_PASS(2),
    PLAYER_SURRENDER(3),
    PLAYER_QUIT(4),
    // GAME_* actions are sent by the server
    GAME_STONE_BLACK(5),
    GAME_STONE_WHITE(6),
    GAME_SEND_GRID(7),
    GAME_END_WIN(8),
    GAME_END_LOSS(9),
    GAME_END_DRAW(10),
    GAME_YOUR_TURN(11),
    GAME_INCORRENT_MOVE(12),
    // Special action for unknown/invalid commands
    UNKNOWN(-1);

    private final int index;

    GameAction(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public static GameAction fromIndex(int index) {
        for (GameAction action : values()) {
            if (action.index == index) {
                return action;
            }
        }
        return UNKNOWN;
    }
}