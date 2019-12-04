package edu.ucr.lxu051.Util;

public class Tile {
    private Simple simple;
    private int number;

    public Tile(Simple simple, int number) {
        if (number < 1 || number > 9) {
            throw new IllegalArgumentException("Number must be between 1 to 9.");
        }
        this.simple = simple;
        this.number = number;
    }

    public Tile(String tile) { // 1B, 2T, 3W
        Simple simple = Simple.valueOf(tile.substring(1));
        int number = Integer.valueOf(tile.substring(0, 1));
        this.simple = simple;
        this.number = number;
    }

    public Simple getSimple() {
        return simple;
    }

    public int getNumber() {
        return number;
    }

    @Override
    public String toString() {
        switch (this.simple) {
            case B:
                if (number == 1) return "\uD83C\uDC19";
                if (number == 2) return "\uD83C\uDC1A";
                if (number == 3) return "\uD83C\uDC1B";
                if (number == 4) return "\uD83C\uDC1C";
                if (number == 5) return "\uD83C\uDC1D";
                if (number == 6) return "\uD83C\uDC1E";
                if (number == 7) return "\uD83C\uDC1F";
                if (number == 8) return "\uD83C\uDC20";
                if (number == 9) return "\uD83C\uDC21";
            case T:
                if (number == 1) return "\uD83C\uDC10";
                if (number == 2) return "\uD83C\uDC11";
                if (number == 3) return "\uD83C\uDC12";
                if (number == 4) return "\uD83C\uDC13";
                if (number == 5) return "\uD83C\uDC14";
                if (number == 6) return "\uD83C\uDC15";
                if (number == 7) return "\uD83C\uDC16";
                if (number == 8) return "\uD83C\uDC17";
                if (number == 9) return "\uD83C\uDC18";
            case W:
                if (number == 1) return "\uD83C\uDC07";
                if (number == 2) return "\uD83C\uDC08";
                if (number == 3) return "\uD83C\uDC09";
                if (number == 4) return "\uD83C\uDC0A";
                if (number == 5) return "\uD83C\uDC0B";
                if (number == 6) return "\uD83C\uDC0C";
                if (number == 7) return "\uD83C\uDC0D";
                if (number == 8) return "\uD83C\uDC0E";
                if (number == 9) return "\uD83C\uDC0F";
        }
        return null;
    }
}
