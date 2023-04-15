package player.ai;

import java.util.HashMap;

public class TranspositionTable {

    private static HashMap<Long, Entry> table;

    protected static final int EXACT = 0;
    protected static final int LOWER = 1;
    protected static final int UPPER = 2;

    public static class Entry {

        private int score;
        private int depth;
        private int flag;

        public Entry(int score, int depth, int flag) {
            this.score = score;
            this.depth = depth;
            this.flag = flag;
        }

        public int getScore() {
            return score;
        }

        public int getDepth() {
            return depth;
        }

        public int getFlag() {
            return flag;
        }

    }

    public TranspositionTable(int size) {
        table = new HashMap<Long, Entry>(size);
    }

    public Entry get(long key) {
        return table.get(key);
    }

    public void put(long key, Entry entry) {
        table.put(key, entry);
    }

    public boolean contains(long key) {
        return table.containsKey(key);
    }

    public void clear() {
        table.clear();
    }





    
}