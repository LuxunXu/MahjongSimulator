package edu.ucr.lxu051.Util;

public class Tool {

    public static Tile positionToTile(int i) {
        int number = i % 9 + 1;
        if (i < 9) {
            return new Tile(Simple.B, number);
        } else if (i > 17) {
            return new Tile(Simple.W, number);
        } else {
            return new Tile(Simple.T, number);
        }
    }

    public static int tileToPosition(Tile tile) {
        int index = tile.getNumber() - 1;
        switch (tile.getSimple()) {
            case B:
                return index;
            case T:
                return index + 9;
            case W:
                return index + 18;
        }
        return -1;
    }

    public static Simple indexToSimple(int i) {
        if (i == 0) {
            return Simple.B;
        }
        if (i == 1) {
            return Simple.T;
        }
        if (i == 2) {
            return Simple.W;
        }
        return null;
    }

    public static int simpleToIndex(Simple simple) {
        if (simple.equals(Simple.B)) {
            return 0;
        }
        if (simple.equals(Simple.T)) {
            return 1;
        }
        if (simple.equals(Simple.W)) {
            return 2;
        }
        return -1;
    }

}
